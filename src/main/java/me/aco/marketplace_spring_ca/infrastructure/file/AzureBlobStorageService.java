package me.aco.marketplace_spring_ca.infrastructure.file;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import me.aco.marketplace_spring_ca.domain.intefrace.FileStorageService;

@Service
@Profile("azure")
public class AzureBlobStorageService implements FileStorageService {

    private final BlobContainerClient containerClient;
    private final String basePath;

    public AzureBlobStorageService(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.container}") String containerName,
            @Value("${azure.storage.base-path:}") String basePath) {
        this.basePath = basePath;

        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        this.containerClient = serviceClient.getBlobContainerClient(containerName);
        if (!this.containerClient.exists()) {
            this.containerClient.create();
        }
    }

    @Override
    public void saveToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
        String blobName = basePath + uploadedFileLocation;
        var blobClient = containerClient.getBlobClient(blobName);
        blobClient.upload(BinaryData.fromStream(uploadedInputStream), true);
    }
}
