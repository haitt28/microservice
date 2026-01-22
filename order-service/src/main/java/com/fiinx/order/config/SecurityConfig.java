package com.fiinx.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * BEST PRACTICE #26: Bảo mật OAuth2 Resource Server.
 * 
 * - Xác thực JWT với Keycloak.
 * - Trích xuất Role từ claim "realm_access".
 * - Bảo mật mức phương thức (Method-level security) với @PreAuthorize.
 * - Cơ chế Stateless session (không lưu trạng thái phiên làm việc).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Vô hiệu hóa CSRF vì hệ thống sử dụng stateless API (JWT)
            .csrf(csrf -> csrf.disable())
            
            // Cấu hình Stateless session
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Các quy tắc phân quyền (Authorization rules)
            .authorizeHttpRequests(auth -> auth
                // Các endpoint công khai (Public) không cần xác thực
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info",
                    "/actuator/prometheus",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                
                // Tất cả các request khác đều bắt buộc phải xác thực (Authenticated)
                .anyRequest().authenticated()
            )
            
            // Cấu hình OAuth2 Resource Server để giải mã JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        
        return http.build();
    }
    
    /**
     * Bộ chuyển đổi JWT tùy chỉnh để trích xuất các role từ Keycloak JWT
     * 
     * Keycloak JWT structure:
     * {
     *   "realm_access": {
     *     "roles": ["USER", "ADMIN"]
     *   },
     *   "resource_access": {
     *     "order-service": {
     *       "roles": ["order_create", "order_view"]
     *     }
     *   }
     * }
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }
    
    static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        
        @Override
        @SuppressWarnings("unchecked")
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            // Trích xuất các role ở mức Realm (Toàn cục của Keycloak)
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            List<String> realmRoles = realmAccess != null 
                ? (List<String>) realmAccess.get("roles") 
                : List.of();
            
            // Trích xuất các role cụ thể cho Client Resource (Microservice) này
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            List<String> resourceRoles = List.of();
            if (resourceAccess != null) {
                Map<String, Object> serviceAccess = (Map<String, Object>) resourceAccess.get("order-service");
                if (serviceAccess != null) {
                    resourceRoles = (List<String>) serviceAccess.get("roles");
                }
            }
            
            // Hợp nhất tất cả các role với tiền tố ROLE_ theo chuẩn Spring Security
            return Stream.concat(
                    realmRoles.stream().map(role -> "ROLE_" + role.toUpperCase()),
                    resourceRoles.stream().map(role -> "ROLE_" + role.toUpperCase())
                )
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        }
    }
}
