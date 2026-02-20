package com.eventflowx.gateway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                        .pathMatchers("/fallback/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/events/**").hasAnyAuthority("ROLE_event.read", "ROLE_user", "ROLE_admin")
                        .pathMatchers(HttpMethod.POST, "/bookings/**").hasAnyAuthority("ROLE_booking.write", "ROLE_user", "ROLE_admin")
                        .pathMatchers(HttpMethod.GET, "/wallets/**").hasAnyAuthority("ROLE_wallet.read", "ROLE_user", "ROLE_admin")
                        .pathMatchers(HttpMethod.POST, "/wallets/*/credit").hasAnyAuthority("ROLE_wallet.credit", "ROLE_user", "ROLE_admin")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(this::extractAuthorities);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        JwtGrantedAuthoritiesConverter scopesConverter = new JwtGrantedAuthoritiesConverter();
        authorities.addAll(scopesConverter.convert(jwt));

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Object roles = realmAccess.get("roles");
            if (roles instanceof Collection<?> roleList) {
                roleList.stream()
                        .map(Object::toString)
                        .map(role -> "ROLE_" + role)
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            }
        }

        boolean hasRoleAuthority = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.startsWith("ROLE_"));
        if (!hasRoleAuthority && jwt.getClaimAsString("preferred_username") != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_user"));
        }

        return authorities;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:6767"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
