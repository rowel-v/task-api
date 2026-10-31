package com.rowel.taskapi.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rowel.taskapi.auth.model.TaskUser;

@Service
public class JwtService {

  private final SecretKey signingKey;
  private final Duration expiration;
  private final String issuer;

  public JwtService(
    @Value("${app.jwt.secret}") String secret,
    @Value("${app.jwt.expiration:PT15M}") Duration expiration,
    @Value("${app.jwt.issuer:task-api}") String issuer
  ) {
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

    if (expiration.isZero() || expiration.isNegative()) {
      throw new IllegalArgumentException("JWT expiration must be positive");
    }

    this.expiration = expiration;
    this.issuer = issuer;
  }

  public String generateToken(TaskUser user) {
    if (user.getId() == null) {
      throw new IllegalArgumentException(
        "User must be saved before generating a token"
      );
    }

    Instant now = Instant.now();

    return Jwts.builder()
      .issuer(issuer)
      .subject(user.getId().toString())
      .issuedAt(Date.from(now))
      .expiration(Date.from(now.plus(expiration)))
      .signWith(signingKey, Jwts.SIG.HS256)
      .compact();
  }

  public Long extractUserId(String token) {
    Claims claims = Jwts.parser()
      .verifyWith(signingKey)
      .requireIssuer(issuer)
      .build()
      .parseSignedClaims(token)
      .getPayload();

    if (claims.getExpiration() == null) {
      throw new JwtException("Token expiration is required");
    }

    try {
      long userId = Long.parseLong(claims.getSubject());

      if (userId <= 0) {
        throw new NumberFormatException();
      }

      return userId;
    } catch (NumberFormatException ex) {
      throw new JwtException("Invalid token subject", ex);
    }
  }
}
