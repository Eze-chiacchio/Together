package com.chiacchio.together.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
public class ProfileImageStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    @Value("${app.storage.bucket.name:${BUCKET_NAME:}}")
    private String bucketName;

    @Value("${app.storage.bucket.endpoint:${AWS_ENDPOINT_URL_S3:}}")
    private String endpoint;

    @Value("${app.storage.bucket.region:${AWS_REGION:us-east-1}}")
    private String region;

    @Value("${app.storage.bucket.access-key:${AWS_ACCESS_KEY_ID:}}")
    private String accessKey;

    @Value("${app.storage.bucket.secret-key:${AWS_SECRET_ACCESS_KEY:}}")
    private String secretKey;

    @Value("${app.storage.bucket.max-file-size-bytes:5242880}")
    private long maxFileSizeBytes;

    @Value("${app.storage.bucket.url-duration-minutes:15}")
    private long presignedUrlDurationMinutes;

    private S3Client s3Client;
    private S3Presigner presigner;

    @PostConstruct
    public void initializeClients() {
        if (!isConfigured()) {
            return;
        }

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
        Region awsRegion = Region.of(region);
        URI endpointUri = URI.create(endpoint);
        S3Configuration s3Configuration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        this.s3Client = S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(awsRegion)
                .endpointOverride(endpointUri)
                .serviceConfiguration(s3Configuration)
                .build();

        this.presigner = S3Presigner.builder()
                .credentialsProvider(credentialsProvider)
                .region(awsRegion)
                .endpointOverride(endpointUri)
                .serviceConfiguration(s3Configuration)
                .build();
    }

    public String uploadProfileImage(Long userId, MultipartFile file, String existingKey) throws IOException {
        ensureStorageConfigured();
        validateFile(file);

        String extension = resolveExtension(file);
        String objectKey = "profile-images/" + userId + "/" + UUID.randomUUID() + extension;

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));

        if (existingKey != null && !existingKey.isBlank()) {
            deleteObject(existingKey);
        }

        return objectKey;
    }

    public String generatePresignedUrl(String objectKey) {
        if (objectKey == null || objectKey.isBlank() || !isConfigured()) {
            return null;
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(presignedUrlDurationMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        return presigner.presignGetObject(presignRequest).url().toString();
    }

    public void deleteProfileImage(String objectKey) {
        ensureStorageConfigured();

        if (objectKey == null || objectKey.isBlank()) {
            return;
        }

        deleteObject(objectKey);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar una imagen");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Formato de imagen no soportado. Usa JPG, PNG o WEBP");
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw new IllegalArgumentException("La imagen supera el tamano maximo permitido");
        }
    }

    private String resolveExtension(MultipartFile file) {
        return switch (file.getContentType()) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || !originalFilename.contains(".")) {
                    yield "";
                }

                int lastDotIndex = originalFilename.lastIndexOf('.');
                yield "." + originalFilename.substring(lastDotIndex + 1);
            }
        };
    }

    private void deleteObject(String objectKey) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            s3Client.deleteObject(request);
        } catch (Exception ignored) {
            // Si el borrado falla no frenamos el cambio de foto.
        }
    }

    private boolean isConfigured() {
        return isPresent(bucketName) && isPresent(endpoint) && isPresent(accessKey) && isPresent(secretKey);
    }

    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    private void ensureStorageConfigured() {
        if (!isConfigured() || s3Client == null || presigner == null) {
            throw new IllegalStateException("El bucket no esta configurado");
        }
    }
}
