package njb.recipe.handler.exception;

public class AiResponseError extends RuntimeException{
    public AiResponseError(String message) {
        super(message);
    }
}
