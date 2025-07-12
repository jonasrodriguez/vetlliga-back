package com.vetlliga.refugiservice;

import com.vetlliga.refugiservice.constants.Rol;
import com.vetlliga.refugiservice.entities.Usuario;
import com.vetlliga.refugiservice.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

  @Value("${app.initial.admin-username}")
  private String adminUsername;

  @Value("${app.initial.admin-password}")
  private String adminPassword;

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) {
    if (usuarioRepository.count() > 0) {
      return;
    }

    Usuario admin = new Usuario();

    admin.setUsername(adminUsername);
    admin.setPassword(passwordEncoder.encode(adminPassword));
    admin.setEmail("admin@vetlliga.com");
    admin.setRol(Rol.ADMIN);
    admin.setEnabled(true);
    admin.setHidden(true);

    usuarioRepository.save(admin);
  }
}
