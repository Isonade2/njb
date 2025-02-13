package njb.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Builder
public class ApiResponseDTO<T> {
    private String status; // 응답 상태
    private String message; // 응답 메시지
    private T data; // data는 제네릭으로 받는다. 실제 데이터


}
