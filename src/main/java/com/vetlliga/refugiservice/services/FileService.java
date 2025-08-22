package com.vetlliga.refugiservice.services;

import com.vetlliga.refugiservice.dtos.DocumentoDto;
import com.vetlliga.refugiservice.entities.Documento;
import com.vetlliga.refugiservice.exceptions.ResourceNotFoundException;
import com.vetlliga.refugiservice.mappers.DocumentosMapper;
import com.vetlliga.refugiservice.repositories.AnimalRepository;
import com.vetlliga.refugiservice.repositories.DocumentoRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

  @Value("${app.upload.dir}")
  private String uploadDir;
  private final AnimalRepository animalRepository;
  private final DocumentoRepository documentoRepository;
  private final DocumentosMapper documentosMapper;

  public DocumentoDto storeFile(Integer id, MultipartFile file, String descripcion) throws IOException {

    log.debug("Nuevo documento: {} para animal: {}", file.getOriginalFilename(), id);

    Path uploadPath = Paths.get(uploadDir + "/" + id);
    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    if (file.isEmpty()) {
      throw new IllegalArgumentException("El archivo no puede estar vacío");
    }

    Path filePath = uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));

    if (Files.exists(filePath)) {
      log.warn("El archivo {} ya existe en la ruta: {}", file.getOriginalFilename(), filePath);
      throw new IllegalArgumentException("El archivo ya existe: " + file.getOriginalFilename());
    }
    file.transferTo(filePath.toFile());

    log.debug("Archivo guardado en: {}", filePath);

    final var animalEntity = animalRepository.findById(id).orElseThrow(() ->
        new ResourceNotFoundException("Animal con id " + id + " no encontrado"));

    var documento = new Documento();
    documento.setFecha(LocalDateTime.now());
    documento.setNombre(file.getOriginalFilename());
    documento.setRuta(filePath.toString());
    documento.setDescripcion(descripcion);
    documento.setAnimal(animalEntity);
    documentoRepository.save(documento);

    return documentosMapper.toDto(documento);
  }

  public Resource getFileResource(Integer id, Integer fileId) throws IOException {

    log.debug("Obteniendo documento: {} para animal: {}", fileId, id);

    final var animalEntity = animalRepository.findById(id).orElseThrow(() ->
        new ResourceNotFoundException("Animal con id " + id + " no encontrado"));

    final var ruta = animalEntity.getDocumentos().stream().filter(d -> d.getId().equals(fileId))
        .findFirst().orElseThrow(() -> new ResourceNotFoundException("Documento con id " + fileId + " no encontrado"))
        .getRuta();

    Path filePath = Paths.get(ruta);
    if (!Files.exists(filePath)) {
      throw new ResourceNotFoundException("Archivo no encontrado en la ruta: " + ruta);
    }

    return new FileSystemResource(filePath);
  }

  public void deleteFile(Integer fileId) throws IOException {
    log.debug("Eliminando documento: {}", fileId);

    final var documento = documentoRepository.findById(fileId)
        .orElseThrow(() -> new ResourceNotFoundException("Documento con id " + fileId + " no encontrado"));

    Path filePath = Paths.get(documento.getRuta());
    if (Files.exists(filePath)) {
      Files.delete(filePath);
      log.debug("Archivo eliminado: {}", filePath);
    } else {
      log.warn("Archivo no encontrado para eliminar: {}", filePath);
    }

    documentoRepository.delete(documento);
  }

}
