package njb.recipe.service;

import lombok.RequiredArgsConstructor;
import njb.recipe.dto.refri.ImageResponseDTO;
import njb.recipe.dto.token.PresignedResponseDTO;
import njb.recipe.repository.IngredientRepository;
import njb.recipe.repository.RefrigeratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RefrigeratorRepository refrigeratorRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // Presigned URL (PUT) 생성
    public PresignedResponseDTO generateUploadPresignedUrl(String folder, String originalFileName) {
        String key = folder + "/" + UUID.randomUUID() + "-" + originalFileName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(
                r -> r.putObjectRequest(objectRequest)
                        .signatureDuration(Duration.ofMinutes(10))
        );

        String staticUrl = "https://" + bucket + ".s3." + Region.AP_NORTHEAST_2.id() + ".amazonaws.com/" + key;

        return new PresignedResponseDTO(presignedRequest.url().toString(), staticUrl);
    }

    // Presigned URL (GET) 생성
    public String generateViewPresignedUrl(String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }

    // 이미지 삭제
    public void deleteFile(String imageUrl) {
        String key = extractKeyFromUrl(imageUrl);
        System.out.println("삭제할 Key: " + key);

        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    // URL에서 Key 추출
    private String extractKeyFromUrl(String url) {
        int idx = url.indexOf(".amazonaws.com/") + ".amazonaws.com/".length();
        return url.substring(idx);
    }


    // 타입별 유저가 저장한 이미지 리스트 조회 서비스
    public List<ImageResponseDTO> getUserImageList(String memberId, String type) {
        Long userId = Long.parseLong(memberId);

        switch (type) {
            case "refrigerator":
                return refrigeratorRepository.findByMemberIdAndPhotoUrlIsNotNull(userId).stream()
                        .map(r -> ImageResponseDTO.builder()
                                .photoUrl(r.getPhotoUrl())
                                .uploadedAt(r.getCreatedAt())
                                .build())
                        .collect(Collectors.toList());

            case "ingredient":
                return ingredientRepository.findByMemberIdAndPhotoUrlIsNotNull(userId).stream()
                        .map(i -> ImageResponseDTO.builder()
                                .photoUrl(i.getPhotoUrl())
                                .uploadedAt(i.getRegistrationDate())
                                .build())
                        .collect(Collectors.toList());

            default:
                throw new IllegalArgumentException("type은 'refrigerator' 또는 'ingredient' 중 하나여야 합니다.");
        }
    }


}
