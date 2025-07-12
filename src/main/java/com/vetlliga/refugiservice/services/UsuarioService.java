package com.vetlliga.refugiservice.services;

import com.vetlliga.refugiservice.dtos.UsuarioDto;
import com.vetlliga.refugiservice.mappers.UsuarioMapper;
import com.vetlliga.refugiservice.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final UsuarioMapper usuarioMapper;
  private final PasswordEncoder passwordEncoder;

  public UsuarioDto createUsuario(UsuarioDto dto) {

    if (usuarioRepository.existsByUsername(dto.getUsername())) {
      throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario");
    }

    final var entity = usuarioMapper.toEntity(dto);
    final var encodedPassword = passwordEncoder.encode(entity.getPassword());
    entity.setPassword(encodedPassword);

    final var respuesta =  usuarioRepository.save(entity);
    return usuarioMapper.toDto(respuesta);
  }

  /*public Usuario login(String username, String rawPassword) {
    Usuario usuario = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    if (!passwordEncoder.matches(rawPassword, usuario.getPassword())) {
      throw new IllegalArgumentException("Invalid credentials");
    }
    return usuario;
  }

  public void changeUserPassword(String username, String oldPassword, String newPassword) {
    Usuario usuario = usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    if (!passwordEncoder.matches(oldPassword, usuario.getPassword())) {
      throw new IllegalArgumentException("Old password is incorrect");
    }
    usuario.setPassword(passwordEncoder.encode(newPassword));
    usuarioRepository.save(usuario);
  }*/


}
