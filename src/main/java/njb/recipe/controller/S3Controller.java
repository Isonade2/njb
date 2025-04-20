package njb.recipe.controller;

import lombok.RequiredArgsConstructor;
import njb.recipe.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;

    /**
     *  업로드용 Presigned URL 발급
     * 프론트에서 파일 업로드 전에 요청
     */
    @GetMapping("/presigned-upload")
    public ResponseEntity<String> getPresignedUploadUrl(
            @RequestParam String folder,
            @RequestParam String fileName
    ) {
        String presignedUrl = s3Service.generateUploadPresignedUrl(folder, fileName);
        return ResponseEntity.ok(presignedUrl);
    }

    /**
     * 조회용 Presigned URL 발급
     * 프론트에서 비공개 이미지 조회 시 요청
     */
    @GetMapping("/presigned-view")
    public ResponseEntity<String> getPresignedViewUrl(
            @RequestParam String key // 예: ingredient/uuid-filename.jpg
    ) {
        String viewUrl = s3Service.generateViewPresignedUrl(key);
        return ResponseEntity.ok(viewUrl);
    }

    /**
     * 이미지 삭제 (서버가 직접 삭제 요청)
     */
    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteImage(@RequestParam String imageUrl) {
        s3Service.deleteFile(imageUrl);
        return ResponseEntity.noContent().build();
    }
}
