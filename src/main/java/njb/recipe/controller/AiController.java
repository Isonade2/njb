package njb.recipe.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.dto.ApiResponseDTO;
import njb.recipe.dto.ResponseUtils;
import njb.recipe.dto.refri.IngredientImageRecognitionDTO;
import njb.recipe.global.jwt.CustomUserDetails;
import njb.recipe.service.AiService;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping
public class AiController {
    private final AiService aiService;
    private final OpenAiChatModel chatModel; 


    /**
     * AI 단일 이미지 인식 API
     * @param file
     * @param userDetails
     * @return
     */
    @PostMapping("/ingredients/image-recognition")
    public ResponseEntity<ApiResponseDTO<List<IngredientImageRecognitionDTO>>> imageRecognition(@RequestParam(required = false, name = "file") @NotNull MultipartFile file,
                                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {

        if(file.isEmpty()){
            throw new ConstraintViolationException("파일이 비어있습니다.", null);
        }
        String contentType = file.getContentType();
        if(contentType == null || (!contentType.equals("image/png") && !contentType.equals("image/jpeg"))){
            throw new ConstraintViolationException("이미지 파일이 아닙니다.", null);
        }

        // AI API 호출 횟수 체크
        aiService.checkAiApiUsage(Long.parseLong(userDetails.getMemberId()));

        // AI에게 전달할 프롬프트 구성
        List<IngredientImageRecognitionDTO> ingredientImageRecognitionDTOS = aiService.recognizeIngredient(file);
        ApiResponseDTO<List<IngredientImageRecognitionDTO>> responseDTO = ResponseUtils.success(ingredientImageRecognitionDTOS, "AI API 호출 성공");
        return ResponseEntity.ok(responseDTO);
    }

}
