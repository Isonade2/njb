package njb.recipe.dto.refri;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientImageRecognitionDTO {
    private String name;
    private int quantity;
    private String category;

}
