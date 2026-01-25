package com.fiinx.identity.application.service;

import com.fiinx.common.event.UserCreatedEvent;
import com.fiinx.identity.api.dto.*;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdentityService {

    private final Keycloak adminKeycloak; // Client quản trị Keycloak (quyền admin-cli)
    private final KafkaTemplate<String, Object> kafkaTemplate; // Template để bắn event qua Kafka

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${app.kafka.topics.user-created}")
    private String userCreatedTopic;

    /**
     * Đăng ký User mới và gán Role mặc định
     */
    public void registerUser(RegistrationRequest request) {
        log.info("Registering user: {}", request.getUsername());

        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        UsersResource usersResource = adminKeycloak.realm(realm).users();
        Response response = usersResource.create(user);

        if (response.getStatus() == 201) {
            log.info("User created successfully in Keycloak: {}", request.getUsername());
            
            // Lấy userId vừa tạo
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

            // BEST PRACTICE: Gán Role mặc định (e.g., "USER")
            assignDefaultRole(userId, "USER");

            // Bắn event sang Kafka
            UserCreatedEvent event = UserCreatedEvent.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .source("identity-service")
                    .build();
            event.initializeDefaults("identity-service");

            kafkaTemplate.send(userCreatedTopic, request.getUsername(), event);
            log.info("Published UserCreatedEvent to Kafka for user: {}", request.getUsername());
        } else {
            log.error("Tạo người dùng thất bại. Mã trạng thái: {}", response.getStatus());
            throw new RuntimeException("Không thể tạo người dùng trong Keycloak");
        }
    }

    private final org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();

    /**
     * Đăng nhập lấy Access Token (Grant Type: Password)
     * Senior Note: Mặc dù Keycloak Admin Client rất mạnh, nhưng để làm tính năng Custom Login 
     * (trao đổi username/password lấy token trực tiếp), dùng RestTemplate gọi thẳng vào 
     * protocol/openid-connect/token là cách làm linh hoạt và chuẩn nhất.
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Logging in user: {}", request.getUsername());
        
        String url = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
        
        org.springframework.util.MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap<>();
        map.add("grant_type", "password");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("username", request.getUsername());
        map.add("password", request.getPassword());
        
        org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> entity = 
            new org.springframework.http.HttpEntity<>(map, new org.springframework.http.HttpHeaders());
            
        AccessTokenResponse response = restTemplate.postForObject(url, entity, AccessTokenResponse.class);
        return mapToAuthResponse(response);
    }

    /**
     * Làm mới Token (Grant Type: Refresh Token)
     * Senior Note: Refresh Token giúp duy trì phiên đăng nhập của User mà không cần hỏi lại mật khẩu.
     * Đây là yếu tố then chốt cho trải nghiệm người dùng (UX) trên Mobile/SPA.
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Refreshing token");
        
        String url = String.format("%s/realms/%s/protocol/openid-connect/token", serverUrl, realm);
        
        org.springframework.util.MultiValueMap<String, String> map = new org.springframework.util.LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("refresh_token", request.getRefreshToken());
        
        org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> entity = 
            new org.springframework.http.HttpEntity<>(map, new org.springframework.http.HttpHeaders());
            
        AccessTokenResponse response = restTemplate.postForObject(url, entity, AccessTokenResponse.class);
        return mapToAuthResponse(response);
    }

    /**
     * Đăng xuất - Thu hồi (Revoke) các token
     */
    public void logout(String userId) {
        log.info("Logging out user: {}", userId);
        adminKeycloak.realm(realm).users().get(userId).logout();
    }

    /**
     * Cập nhật Profile (Họ tên, Email)
     */
    public void updateProfile(String userId, UpdateProfileRequest request) {
        log.info("Updating profile for user: {}", userId);
        UserResource userResource = adminKeycloak.realm(realm).users().get(userId);
        UserRepresentation user = userResource.toRepresentation();
        
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        
        userResource.update(user);
    }

    /**
     * Đổi mật khẩu
     */
    public void changePassword(String userId, ChangePasswordRequest request) {
        log.info("Changing password for user: {}", userId);
        
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getNewPassword());
        credential.setTemporary(false);
        
        adminKeycloak.realm(realm).users().get(userId).resetPassword(credential);
    }

    /**
     * Phân quyền (Chỉ dành cho Admin)
     */
    public void grantRole(GrantRoleRequest request) {
        log.info("Granting role {} to user {}", request.getRoleName(), request.getUserId());
        
        UserResource userResource = adminKeycloak.realm(realm).users().get(request.getUserId());
        RoleRepresentation role = adminKeycloak.realm(realm).roles().get(request.getRoleName()).toRepresentation();
        
        userResource.roles().realmLevel().add(Collections.singletonList(role));
    }

    /**
     * Lock user account
     */
    public void lockUser(String userId) {
        log.info("Locking user account: {}", userId);
        UserResource userResource = adminKeycloak.realm(realm).users().get(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(false);
        userResource.update(user);
    }

    /**
     * Unlock user account
     */
    public void unlockUser(String userId) {
        log.info("Unlocking user account: {}", userId);
        UserResource userResource = adminKeycloak.realm(realm).users().get(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(true);
        userResource.update(user);
    }

    // Các phương thức bổ trợ (Helper methods)
    private void assignDefaultRole(String userId, String roleName) {
        try {
            RoleRepresentation role = adminKeycloak.realm(realm).roles().get(roleName).toRepresentation();
            adminKeycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
            log.info("Đã gán Role mặc định {} cho người dùng {}", roleName, userId);
        } catch (NotFoundException e) {
            log.warn("Role mặc định {} không tồn tại trong Realm", roleName);
        }
    }

    private AuthResponse mapToAuthResponse(AccessTokenResponse token) {
        return AuthResponse.builder()
                .accessToken(token.getToken())
                .expiresIn(token.getExpiresIn())
                .refreshToken(token.getRefreshToken())
                .refreshExpiresIn(token.getRefreshExpiresIn())
                .tokenType(token.getTokenType())
                .build();
    }
}
