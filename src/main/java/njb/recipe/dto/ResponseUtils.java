package njb.recipe.dto;

public class ResponseUtils {

    public static <T> ApiResponseDTO<T> success(T data, String message){
        return ApiResponseDTO.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .build();
    }
    public static <T> ApiResponseDTO<T> success(String message){
        return ApiResponseDTO.<T>builder()
                .status("success")
                .message(message)
                .build();
    }

    public static <T> ApiResponseDTO<T> fail(String message){
        return ApiResponseDTO.<T>builder()
                .status("fail")
                .message(message)
                .build();
    }

    public static <T> ApiResponseDTO<T> error(String message){
        return ApiResponseDTO.<T>builder()
                .status("error")
                .message(message)
                .build();
    }
}
