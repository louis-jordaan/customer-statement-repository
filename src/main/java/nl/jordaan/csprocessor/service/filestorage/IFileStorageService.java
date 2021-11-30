package nl.jordaan.csprocessor.service.filestorage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface IFileStorageService {
    Path store(MultipartFile file) throws IOException;
}
