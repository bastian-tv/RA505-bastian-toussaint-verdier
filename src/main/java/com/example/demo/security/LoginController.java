package com.example.demo.security;

import java.time.Duration;
import java.time.Instant;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
class LoginController {
    private static final Duration TOKEN_LIFETIME = Duration.ofHours(1);

    private final AuthenticationManager authentication;
    private final JwtEncoder encoder;

    LoginController(AuthenticationManager authentication, JwtEncoder encoder) {
        this.authentication = authentication;
        this.encoder = encoder;
    }

    record Credentials(String username, String password) {}
    record TokenResponse(String accessToken, String tokenType, long expiresIn) {}

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody Credentials credentials) {
        if (credentials.username() == null || credentials.username().isBlank()
                || credentials.password() == null || credentials.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required");
        }

        org.springframework.security.core.Authentication user;
        try {
            user = authentication.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(
                    credentials.username(), credentials.password()));
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer(SecurityConfig.ISSUER)
                .subject(user.getName())
                .issuedAt(now)
                .expiresAt(now.plus(TOKEN_LIFETIME))
                .claim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                        .filter(authority -> authority.startsWith("ROLE_")).toList())
                .build();
        var headers = JwsHeader.with(MacAlgorithm.HS256).build();
        var token = encoder.encode(JwtEncoderParameters.from(headers, claims));
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(new TokenResponse(token.getTokenValue(), "Bearer", TOKEN_LIFETIME.toSeconds()));
    }
}
