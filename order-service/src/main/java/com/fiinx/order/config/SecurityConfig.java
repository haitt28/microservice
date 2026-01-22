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
 * BEST PRACTICE #26: OAuth2 Resource Server Security
 * 
 * - JWT validation với Keycloak
 * - Role extraction từ realm_access claim
 * - Method-level security với @PreAuthorize
 * - Stateless session
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(csrf -> csrf.disable())
            
            // Stateless session
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info",
                    "/actuator/prometheus",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            
            // OAuth2 Resource Server với JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        
        return http.build();
    }
    
    /**
     * Custom JWT converter để extract roles từ Keycloak JWT
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
            // Extract realm roles
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            List<String> realmRoles = realmAccess != null 
                ? (List<String>) realmAccess.get("roles") 
                : List.of();
            
            // Extract resource roles for this service
            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            List<String> resourceRoles = List.of();
            if (resourceAccess != null) {
                Map<String, Object> serviceAccess = (Map<String, Object>) resourceAccess.get("order-service");
                if (serviceAccess != null) {
                    resourceRoles = (List<String>) serviceAccess.get("roles");
                }
            }
            
            // Combine all roles with ROLE_ prefix
            return Stream.concat(
                    realmRoles.stream().map(role -> "ROLE_" + role.toUpperCase()),
                    resourceRoles.stream().map(role -> "ROLE_" + role.toUpperCase())
                )
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        }
    }
}
