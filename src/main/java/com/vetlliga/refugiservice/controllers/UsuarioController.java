package com.vetlliga.refugiservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetlliga.refugiservice.dtos.UsuarioDto;
import com.vetlliga.refugiservice.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuarioController {

  private final UsuarioService usuarioService;
  private final ObjectMapper objectMapper;

  @PostMapping
  public ResponseEntity<UsuarioDto> crearUsuario(@RequestBody UsuarioDto usuarioDto) {

    final var usuario = usuarioService.createUsuario(usuarioDto);
    return ResponseEntity.ok(usuario);
  }

  @PutMapping
  @RequestMapping("/{id}")
  public ResponseEntity<UsuarioDto> actualizarPassword(@RequestBody UsuarioDto usuarioDto) {

    return ResponseEntity.ok(usuarioDto);
  }
}
