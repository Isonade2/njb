package njb.recipe.dto.refri;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class RefrigeratorRequestDTO {
    @NotNull(message = "Name is required.")
    private String name;
    private String photoUrl;
    private String description;   
}
