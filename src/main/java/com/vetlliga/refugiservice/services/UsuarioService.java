package com.vetlliga.refugiservice.services;

import com.vetlliga.refugiservice.dtos.UsuarioDto;
import com.vetlliga.refugiservice.mappers.UsuarioMapper;
import com.vetlliga.refugiservice.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import java.util.List;
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

  public List<UsuarioDto> getAllUsuarios() {
    final var usuarios = usuarioRepository.findAll();
    return usuarios.stream()
        .filter(u -> !u.getUsername().equals("admin"))
        .map(usuarioMapper::toDto)
        .toList();
  }

  public UsuarioDto createUsuario(UsuarioDto dto) {

    if (dto.getUsername() == null || dto.getUsername().isBlank()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }

    if (dto.getPassword() == null || dto.getPassword().isBlank()) {
      throw new IllegalArgumentException("Password cannot be empty");
    }

    if (usuarioRepository.existsByUsername(dto.getUsername())) {
      throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario");
    }

    final var entity = usuarioMapper.toEntity(dto);
    final var encodedPassword = passwordEncoder.encode(dto.getPassword());
    entity.setPassword(encodedPassword);

    final var respuesta =  usuarioRepository.save(entity);
    return usuarioMapper.toDto(respuesta);
  }

  public UsuarioDto editarUsuario(Integer userId, UsuarioDto dto) {

    if (dto.getUsername() == null || dto.getUsername().isBlank()) {
      throw new IllegalArgumentException("Username cannot be empty");
    }

    final var usuario = usuarioRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!usuario.getUsername().equals(dto.getUsername()) && usuarioRepository.existsByUsername(dto.getUsername())) {
      throw new IllegalArgumentException("Ya existe un usuario con ese nombre de usuario");
    }

    usuarioMapper.updateEntityFromDto(dto, usuario);

    final var respuesta =  usuarioRepository.save(usuario);
    return usuarioMapper.toDto(respuesta);
  }

  public void changeUserPassword(Integer userId, String newPassword) {
    var usuario = usuarioRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (newPassword == null || newPassword.isBlank()) {
      throw new IllegalArgumentException("New password cannot be empty");
    }

    if (newPassword.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 6 characters long");
    }

    usuario.setPassword(passwordEncoder.encode(newPassword));
    usuarioRepository.save(usuario);
  }

  public void deleteUsuario(Integer userId) {
    if (!usuarioRepository.existsById(userId)) {
      throw new IllegalArgumentException("User not found");
    }
    usuarioRepository.deleteById(userId);
  }

}
