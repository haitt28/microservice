package com.fiinx.identity.api.controller;

import com.fiinx.identity.api.dto.*;
import com.fiinx.identity.application.service.IdentityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Controller xử lý các yêu cầu liên quan đến định danh và xác thực.
 * Senior Note: Đây là API Facade bao bọc các tính năng của Keycloak.
 */
@RestController
@RequestMapping("/api/v1/identity")
@RequiredArgsConstructor
public class IdentityController {

    private final IdentityService identityService;

    /**
     * Senior Note: Endpoint đăng ký là "Public".
     * Chúng ta sử dụng @Valid để đảm bảo dữ liệu đầu vào sạch sẽ trước khi gửi sang Keycloak.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        identityService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    /**
     * Senior Note: Custom Login API giúp chúng ta có thể track được login history 
     * hoặc thực hiện các logic bổ sung trước/sau khi Keycloak cấp token.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(identityService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(identityService.refreshToken(request));
    }

    /**
     * Senior Note: Sử dụng java.security.Principal là cách làm "Best Practice" 
     * để lấy thông tin User ID (subject) từ JWT mà không cần parse thủ công.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Principal principal) {
        identityService.logout(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(Principal principal, @Valid @RequestBody UpdateProfileRequest request) {
        identityService.updateProfile(principal.getName(), request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(Principal principal, @Valid @RequestBody ChangePasswordRequest request) {
        identityService.changePassword(principal.getName(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Senior Note: RBAC (Role Based Access Control) ở mức method.
     * Chỉ những user có Token chứa role 'ADMIN' mới có thể thực thi method này.
     */
    @PostMapping("/roles/grant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> grantRole(@Valid @RequestBody GrantRoleRequest request) {
        identityService.grantRole(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/users/{userId}/lock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lock user", description = "Disable user account in Keycloak")
    public ResponseEntity<Void> lockUser(@PathVariable String userId) {
        identityService.lockUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/users/{userId}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unlock user", description = "Enable user account in Keycloak")
    public ResponseEntity<Void> unlockUser(@PathVariable String userId) {
        identityService.unlockUser(userId);
        return ResponseEntity.noContent().build();
    }
}
