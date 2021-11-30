package nl.jordaan.csprocessor.service.filestorage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@Transactional
public class FileStorageService implements IFileStorageService {

    @Value("${statement-processing.validation.storage-directory}")
    private String storageDirectory;

    @Override
    public Path store(MultipartFile file) throws IOException {
        if (file != null) {
            Path filePath = Path.of(createFileDirectory().toString(), file.getOriginalFilename());
            file.transferTo(filePath);
            return filePath;
        }
        return null;
    }

    private Path createFileDirectory() throws IOException {
        if (!Files.exists(Path.of(storageDirectory))) {
            Files.createDirectory(Path.of(storageDirectory));
        }
        return Files.createDirectory(Path.of(storageDirectory, UUID.randomUUID().toString()));
    }
}
