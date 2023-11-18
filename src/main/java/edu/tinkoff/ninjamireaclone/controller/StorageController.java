package edu.tinkoff.ninjamireaclone.controller;

import edu.tinkoff.ninjamireaclone.exception.storage.StorageFileNotFoundException;
import edu.tinkoff.ninjamireaclone.service.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class StorageController {
    private static final List<String> imageExtensions = List.of("png", "jpg", "gif");

    private final StorageService storageService;

    @PostMapping("/files")
    public void uploadFile(@RequestParam("file") MultipartFile file) {
        storageService.store(file);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"%s\"".formatted(
                        storageService.getOriginalName(filename)
                ))
                .body(file);
    }

    @GetMapping(value = "/images/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        var ext = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));

        if (ext.isEmpty() || imageExtensions.stream().noneMatch(ext.get()::equals))
            return ResponseEntity.badRequest().build();

        MediaType mediaType = MediaType.parseMediaType("image/" + ext.get());

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(file);
    }


    @ExceptionHandler
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
