package njb.recipe.handler;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private String status;
    private String error;
    private String message;


}
