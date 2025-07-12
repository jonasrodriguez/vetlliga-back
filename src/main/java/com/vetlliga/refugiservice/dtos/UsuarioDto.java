package com.vetlliga.refugiservice.dtos;

import com.vetlliga.refugiservice.constants.Rol;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDto {

  private Integer id;
  private String username;
  private String password;
  private String email;
  private String firstName;
  private String lastName;
  private Rol rol;
  private LocalDateTime lastLogin;
}
