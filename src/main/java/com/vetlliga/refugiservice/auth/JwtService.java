package com.vetlliga.refugiservice.auth;

import com.vetlliga.refugiservice.constants.Rol;
import com.vetlliga.refugiservice.dtos.UsuarioDto;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

  private final SecretKey secretKey;

  public JwtService(@Value("${app.jwt.secret}") String secret) {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }

  public String generateToken(String username, String email, String rol) {

    final var now = new Date();
    final var expiryDate = new Date(now.getTime() + 1000 * 60 * 60 * 24);

    return Jwts.builder()
        .subject(username)
        .claim("email", email)
        .claim("role", rol)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(secretKey)
        .compact();
  }

  public UsuarioDto parseToken(String token) {
    try {
      var claims = Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      if (claims.getExpiration().before(new Date())) {
        throw new IllegalArgumentException("Token has expired");
      }

      return UsuarioDto.builder()
          .username(claims.getSubject())
          .email(claims.get("email", String.class))
          .rol(Rol.valueOf(claims.get("role", String.class)))
          .build();

    } catch (JwtException e) {
      throw new IllegalArgumentException("Invalid JWT token", e);
    }
  }
}

