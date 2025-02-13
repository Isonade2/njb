package njb.recipe.dto.refri;

import lombok.Data;

@Data
public class RefrigeratorResponseDTO {
    private Long id; // ID 필드
    private String name;
    private String photoUrl;
    private String description;
}
