package njb.recipe.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresignedResponseDTO {
    private String presignedUrl;
    private String path;

}
