package com.szampchat.server;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CryptoService {
    private static final TemporalAmount EXPIRATION_TIME = Duration.ofHours(1);

    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;
    private final JwtDecoder jwtDecoder;

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(EXPIRATION_TIME))
                .subject(userDetails.getPassword())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String decodedToken = jwtDecoder.decode(token).getSubject();
        if(decodedToken == null)
            return false;
        return decodedToken.equals(userDetails.getUsername());
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}
