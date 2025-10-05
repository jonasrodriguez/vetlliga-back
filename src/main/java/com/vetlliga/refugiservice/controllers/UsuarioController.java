package com.vetlliga.refugiservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetlliga.refugiservice.dtos.PasswordUpdateDto;
import com.vetlliga.refugiservice.dtos.UsuarioDto;
import com.vetlliga.refugiservice.services.UsuarioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

  private final UsuarioService usuarioService;
  private final ObjectMapper objectMapper;

  @GetMapping
  public ResponseEntity<List<UsuarioDto>> listadoUsuarios() {
    final var usuarios = usuarioService.getAllUsuarios();
    return ResponseEntity.ok(usuarios);
  }

  @PostMapping
  public ResponseEntity<UsuarioDto> crearUsuario(@RequestBody UsuarioDto usuarioDto) {

    final var usuario = usuarioService.createUsuario(usuarioDto);
    return ResponseEntity.ok(usuario);
  }

  @PutMapping("/{id}")
  public ResponseEntity<UsuarioDto> editarUsuario(@PathVariable Integer id, @RequestBody UsuarioDto usuarioDto) {

    final var usuario = usuarioService.editarUsuario(id, usuarioDto);
    return ResponseEntity.ok(usuario);
  }

  @PutMapping("/{id}/password")
  public ResponseEntity<Void> actualizarPassword(@PathVariable Integer id, @RequestBody PasswordUpdateDto dto) {
    usuarioService.changeUserPassword(id, dto.getPassword());

    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarUsuario(@PathVariable Integer id) {
    usuarioService.deleteUsuario(id);
    return ResponseEntity.ok().build();
  }
}
