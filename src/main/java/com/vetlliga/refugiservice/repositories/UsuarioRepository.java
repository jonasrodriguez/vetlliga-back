package com.vetlliga.refugiservice.repositories;

import com.vetlliga.refugiservice.entities.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

  Optional<Usuario> findByUsername(String username);

  boolean existsByUsername(String username);
}
