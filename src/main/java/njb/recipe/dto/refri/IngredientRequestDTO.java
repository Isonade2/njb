package njb.recipe.dto.refri;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IngredientRequestDTO {
    @NotNull(message = "Refrigerator ID is required.")
    private Long refrigeratorId; // 냉장고 ID
    @NotNull(message = "Name is required.")
    private String name; // 재료 이름 (NOT NULL)
    private String photoUrl; // 재료 사진 URL (null 허용)
    @NotNull(message = "Quantity is required.")
    private Integer quantity; // 갯수 (NOT NULL)
    @NotNull(message = "Category is required.")
    private String category; // 분류 (예: 육류, 채소 등) (NOT NULL)
    @NotNull(message = "Expiration date is required.")
    private LocalDate expirationDate; // 유통기한 (NOT NULL)
}
