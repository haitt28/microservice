package com.fiinx.identity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cấu hình bảo mật cho Identity Service sử dụng Spring Security và OAuth2.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Senior Note: Cho phép dùng @PreAuthorize trên Controller
public class SecurityConfig {

    /**
     * Cấu hình chuỗi lọc bảo mật chính cho Identity Service
     * Senior Note: Trong Microservices, Identity Service đóng vai trò là "Cửa ngõ" xác thực.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Vô hiệu hóa CSRF vì dùng stateless JWT
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/api/v1/identity/register",
                        "/api/v1/identity/login",
                        "/api/v1/identity/refresh"
                ).permitAll() // Các endpoint công khai không cần auth
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated() // Tất cả các request khác phải có JWT hợp lệ
            )
            // Senior Note: Biến service này thành OAuth2 Resource Server để tự động giải mã JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
            // Senior Note: Stateless là "bắt buộc" trong Microservices để đảm bảo tính Scalability
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            
        return http.build();
    }

    /**
     * Senior Note: Đây là trái tim của việc phân quyền.
     * Keycloak lưu Role trong claim "realm_access.roles". Chúng ta cần map nó về định dạng ROLE_...
     * mà Spring Security có thể hiểu được để dùng với hasRole().
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || realmAccess.isEmpty()) {
                return new JwtGrantedAuthoritiesConverter().convert(jwt);
            }
            @SuppressWarnings("unchecked")
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
        });
        return converter;
    }
}
