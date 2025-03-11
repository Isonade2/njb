package njb.recipe.dto.refri;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngredientResponseDTO {
    private Long id; // 재료 ID
    private Long refrigeratorId; // 냉장고 ID
    private String name; // 재료 이름
    private String photoUrl; // 재료 사진 URL
    private int quantity; // 갯수
    private String category; // 카테고리 이름
    private Long categoryId; // 카테고리 ID
    private LocalDateTime registrationDate; // 등록 날짜
    private LocalDate expirationDate; // 유통기한
}
