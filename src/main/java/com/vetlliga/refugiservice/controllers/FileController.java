package com.vetlliga.refugiservice.controllers;

import com.vetlliga.refugiservice.dtos.DocumentoDto;
import com.vetlliga.refugiservice.services.FileService;
import java.io.IOException;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/animales/{id}")
public class FileController {

  private final FileService fileService;

  @PostMapping("/file")
  public ResponseEntity<DocumentoDto> uploadFile(@PathVariable Integer id, @RequestParam("file") MultipartFile file, @RequestParam("descripcion") String descripcion) {
    try {
      return ResponseEntity.ok(fileService.storeFile(id, file, descripcion));
    } catch (IOException e) {
      return ResponseEntity.status(500).body(null);
    }
  }

  @GetMapping("/file/{fileId}")
  public ResponseEntity<Resource> downloadFile(@PathVariable Integer id, @PathVariable Integer fileId) throws IOException {
    final var resource = fileService.getFileResource(id, fileId);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(resource.getFile().toPath()))
        .body(resource);
  }

  @DeleteMapping("/file/{fileId}")
  public ResponseEntity<Void> deleteFile( @PathVariable Integer fileId) {
    try {
      fileService.deleteFile(fileId);
      return ResponseEntity.noContent().build();
    } catch (IOException e) {
      return ResponseEntity.status(500).build();
    }
  }

}
