package com.example.jwt.service;

import com.example.jwt.model.LoginResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    private final int ACCESS_TOKEN_EXPIRY_IN_MINUTES = 1000 * 60 * 2;

    private final int REFRESH_TOKEN_EXPIRY_IN_MINUTES = 1000 * 60 * 5;

    private final String secretKey;

    public JwtService() {
        this.secretKey = generateSecretKey();
    }


    public LoginResponse generateTokens(String userName) {
        return LoginResponse.builder().accessToken(generateAccessToken(userName))
            .refreshToken(generateRefreshToken(userName)).refreshTokenInMinutes(60).build();
    }

    public LoginResponse generateTokens(String userName, String refreshToken) {
        return LoginResponse.builder().accessToken(generateAccessToken(userName))
            .refreshToken(refreshToken).refreshTokenInMinutes(60).build();
    }

    // Generating the token
    public String generateAccessToken(String username) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("username", username);

        return Jwts.builder().setClaims(claims).setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY_IN_MINUTES))
            .signWith(getKey(), SignatureAlgorithm.HS256).compact();

    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            Key secretKey = keyGenerator.generateKey();
            System.out.println("Secret Key : " + secretKey.toString());
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException ex) {
            log.error("Error in generating token due to algo not found:{}", ex.getMessage(), ex);
            throw new RuntimeException("Error generating secret key", ex);
        }
    }


    // validating the token
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractUserName(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build().parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return expiryDate(token).before(new Date());
    }

    private Date expiryDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Give the refresh token
    public String generateRefreshToken(String username) {

        // generating another token called refresh token setting the validity to 1 hour

        Map<String, Object> claims = new HashMap<>();

        claims.put("username", username);

        return Jwts.builder().setClaims(claims).setSubject(username)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY_IN_MINUTES))
            .signWith(getKey(), SignatureAlgorithm.HS256).compact();

    }

    public boolean validateRefreshToken(String refreshToken, UserDetails userDetails) {
        return validateToken(refreshToken, userDetails);
    }
}
