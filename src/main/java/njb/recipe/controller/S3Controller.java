package njb.recipe.controller;

import lombok.RequiredArgsConstructor;
import njb.recipe.domain.member.dto.ApiResponseDTO;
import njb.recipe.dto.ResponseUtils;
import njb.recipe.dto.refri.ImageResponseDTO;
import njb.recipe.dto.token.PresignedResponseDTO;
import njb.recipe.global.jwt.CustomUserDetails;
import njb.recipe.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static njb.recipe.dto.ResponseUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class S3Controller {

    private final S3Service s3Service;

    /**
     *  업로드용 Presigned URL 발급
     * 프론트에서 파일 업로드 전에 요청
     */
    @GetMapping("/presigned")
    public ResponseEntity<ApiResponseDTO<PresignedResponseDTO>> getPresignedUploadUrl(
            @AuthenticationPrincipal CustomUserDetails userDetails,  // 인증된 사용자 정보 (확인용)
            @RequestParam String folder,
            @RequestParam String fileName
    ) {
        // 서비스에서 Presigned URL + Path 생성
        PresignedResponseDTO response = s3Service.generateUploadPresignedUrl(folder, fileName);
        return ResponseEntity.ok(success(response, "Presigned URL 발급 성공"));  // JSON 형식으로 응답
    }


    /**
     * 조회용 Presigned URL 발급
     * 프론트에서 비공개 이미지 조회 시 요청
     */
    // @GetMapping("/presigned-view")
    // public ResponseEntity<ApiResponseDTO<String>> getPresignedViewUrl(
    //     @AuthenticationPrincipal CustomUserDetails userDetails,
    //         @RequestParam String key // 예: ingredient/uuid-filename.jpg
    // ) {
    //     String viewUrl = s3Service.generateViewPresignedUrl(key);
    //     return ResponseEntity.ok(success(viewUrl, "Presigned URL 발급 성공"));
    // }

    /**
     * 이미지 삭제 (서버가 직접 삭제 요청)
     */
    @DeleteMapping("/")
    public ResponseEntity<Void> deleteImage(@RequestParam String imageUrl,
    @AuthenticationPrincipal CustomUserDetails userDetails) {
        s3Service.deleteFile(imageUrl);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponseDTO<List<ImageResponseDTO>>> getUserImages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String type) {

        if (type == null || type.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ResponseUtils.fail("type 파라미터는 필수입니다."));
        }

        List<ImageResponseDTO> result = s3Service.getUserImageList(userDetails.getMemberId(), type);
        return ResponseEntity.ok(ResponseUtils.success(result, "유저 이미지 조회 성공"));
    }

}
