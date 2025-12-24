package me.aco.marketplace_spring_ca.domain.intefrace;

import java.io.InputStream;

public interface FileStorageService {

    void saveToFile(InputStream uploadedInputStream, String uploadedFileLocation);
    
}
