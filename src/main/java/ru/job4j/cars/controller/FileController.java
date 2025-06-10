package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.service.photo.PhotoService;

import java.util.Optional;

@Controller
@RequestMapping("/files")
@AllArgsConstructor
public class FileController {
    private final PhotoService photoService;

    @GetMapping("/{filePath}")
    public ResponseEntity<?> getByFilePath(@PathVariable String filePath) {
        Optional<FileDto> file = photoService.findFileDtoByFilePath(filePath);
        if (file.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(file.get().getContent());
    }
}
