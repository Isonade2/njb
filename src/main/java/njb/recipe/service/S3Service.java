package njb.recipe.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    
     //  S3에 이미지 업로드용 Presigned URL 발급 (PUT 방식)    
    public String generateUploadPresignedUrl(String folder, String originalFileName) {
        // 고유한 파일 경로 생성
        String key = folder + "/" + UUID.randomUUID() + "-" + originalFileName;

        // S3에 업로드할 파일 요청 정보
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/jpeg")
                .build();

        // Presigned URL 발급 요청
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                r -> r.putObjectRequest(objectRequest)
                      .signatureDuration(Duration.ofMinutes(5))
        );

        return presignedRequest.url().toString(); // 프론트가 이 URL로 업로드하게 됨
    }

    
     // S3에 있는 이미지 조회용 Presigned URL 발급 (GET 방식)
    public String generateViewPresignedUrl(String key) {
        // 조회 요청 정보 생성
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // Presigned GET URL 발급
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest =
                s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }

    // S3에서 이미지 삭제
    public void deleteFile(String imageUrl) {
        String key = extractKeyFromUrl(imageUrl);

        S3Client s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    /**
     *  S3 URL에서 Key 추출
     * ex: https://bucket.s3.amazonaws.com/folder/abc.jpg → folder/abc.jpg
     */
    private String extractKeyFromUrl(String url) {
        int idx = url.indexOf(".amazonaws.com/") + ".amazonaws.com/".length();
        return url.substring(idx);
    }
}
