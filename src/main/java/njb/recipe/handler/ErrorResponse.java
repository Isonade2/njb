package njb.recipe.handler;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ErrorResponse {
    private int status;
    private String error;
    private String message;


}
