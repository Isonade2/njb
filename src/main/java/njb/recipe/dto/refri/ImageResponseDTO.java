package njb.recipe.dto.refri;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ImageResponseDTO {
    private String photoUrl;
    private LocalDateTime uploadedAt; // 냉장고는 생성날짜, 재료는 등록날짜
}
