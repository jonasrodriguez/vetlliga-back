package com.vetlliga.refugiservice.auth;

import com.vetlliga.refugiservice.dtos.LoginRequest;
import com.vetlliga.refugiservice.repositories.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
@AllArgsConstructor
public class AuthManager {

  private final JwtService jwtService;
  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  public Map<String, String> login(@RequestBody LoginRequest request) {

    var user = usuarioRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("Invalid credentials");
    }

    final var token = jwtService.generateToken(user.getUsername(), user.getEmail(), user.getRol().name());

    Map<String, String> response = new HashMap<>();
    response.put("token", token);

    user.setLastLogin(LocalDateTime.now());
    usuarioRepository.save(user);

    return response;
  }

}
