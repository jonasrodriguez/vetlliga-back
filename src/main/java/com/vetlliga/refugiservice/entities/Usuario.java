package com.vetlliga.refugiservice.entities;

import com.vetlliga.refugiservice.constants.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Integer id;
  private String username;
  private String password;
  private String email;
  private String firstName;
  private String lastName;
  @Enumerated(EnumType.STRING)
  private Rol rol;
  private Boolean enabled = true;
  private Boolean hidden = false;
  private LocalDateTime lastLogin;
}
