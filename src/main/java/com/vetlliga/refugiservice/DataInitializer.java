package com.vetlliga.refugiservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetlliga.refugiservice.constants.Rol;
import com.vetlliga.refugiservice.constants.TipoAnimal;
import com.vetlliga.refugiservice.entities.Localizacion;
import com.vetlliga.refugiservice.entities.Usuario;
import com.vetlliga.refugiservice.repositories.LocalizacionRepository;
import com.vetlliga.refugiservice.repositories.UsuarioRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  @Value("${app.initial.admin-username}")
  private String adminUsername;

  @Value("${app.initial.admin-password}")
  private String adminPassword;

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;
  private final LocalizacionRepository localizacionRepository;

  @Override
  public void run(String... args) throws Exception {
    initAdmin();
    initLocalizaciones();
  }

  private void initAdmin() {
    if (usuarioRepository.count() == 0) {
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

  private void initLocalizaciones() throws IOException {
    if (localizacionRepository.count() > 0) {
      return;
    }

    ObjectMapper mapper = new ObjectMapper();
    InputStream input = getClass().getClassLoader().getResourceAsStream("data/localizacionesIniciales.json");

    @SuppressWarnings("unchecked")
    Map<String, List<Map<String, String>>> json = mapper.readValue(input, Map.class);

    var locales = json.getOrDefault("localizaciones", List.of());

    for (Map<String, String> l : locales) {
      String nombre = l.get("nombre");
      TipoAnimal tipo = TipoAnimal.valueOf(l.get("tipo"));
      localizacionRepository.save(new Localizacion(null, tipo, nombre));
    }
  }
}
