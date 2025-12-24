package me.aco.marketplace_spring_ca.infrastructure.file;

import java.io.*;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.domain.intefrace.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    public void saveToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
        try (OutputStream out = new FileOutputStream(new File(uploadedFileLocation))) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
