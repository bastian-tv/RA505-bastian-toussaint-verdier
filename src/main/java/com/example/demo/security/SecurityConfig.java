package com.example.demo.security;

import jakarta.servlet.DispatcherType;
import java.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
class SecurityConfig {
    static final String ISSUER = "r505-rugby-api";

    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/bonjour").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(server -> server.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthorities())))
                .build();
    }

    private JwtAuthenticationConverter jwtAuthorities() {
        var roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("");
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }

    @Bean
    AuthenticationManager authenticationManager(
            @Value("${R505_ADMIN_PASSWORD:}") String admin,
            @Value("${R505_COACH_PASSWORD:}") String coach,
            @Value("${R505_PLAYER_PASSWORD:}") String player) {
        var encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        var users = new InMemoryUserDetailsManager();
        addAccount(users, encoder, "admin", admin, "ADMIN");
        addAccount(users, encoder, "coach", coach, "COACH");
        addAccount(users, encoder, "player", player, "PLAYER");
        var provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    private void addAccount(InMemoryUserDetailsManager users,
                            org.springframework.security.crypto.password.PasswordEncoder encoder,
                            String username, String password, String role) {
        if (!password.isBlank()) {
            users.createUser(User.withUsername(username).password(encoder.encode(password)).roles(role).build());
        }
    }

    @Bean
    SecretKey jwtKey(@Value("${R505_JWT_SECRET:}") String secret) {
        if (secret.isBlank()) {
            // A local instance can start without configuration. Its tokens expire on restart.
            byte[] key = new byte[32];
            new java.security.SecureRandom().nextBytes(key);
            return new SecretKeySpec(key, "HmacSHA256");
        }
        byte[] key = Base64.getDecoder().decode(secret);
        if (key.length < 32) {
            throw new IllegalArgumentException("R505_JWT_SECRET must contain at least 32 Base64-encoded bytes");
        }
        return new SecretKeySpec(key, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey key) {
        return NimbusJwtEncoder.withSecretKey(key).algorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey key) {
        var decoder = NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(ISSUER));
        return decoder;
    }
}
