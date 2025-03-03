package njb.recipe.controller;

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

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping
public class AiController {
    private final AiService aiService;
    private final OpenAiChatModel chatModel;

    @PostMapping("/ai-test")
    public ResponseEntity<ApiResponseDTO<IngredientImageRecognitionDTO>> aiTest(@RequestParam("file") @NotNull MultipartFile file,
                                                                                @AuthenticationPrincipal CustomUserDetails userDetails) {



        // AI API 호출 횟수 체크
        aiService.checkAiApiUsage(Long.parseLong(userDetails.getMemberId()));

        // AI에게 전달할 프롬프트 구성
        IngredientImageRecognitionDTO ingredientImageRecognitionDTO = aiService.recognizeIngredient(file);
        ApiResponseDTO<IngredientImageRecognitionDTO> responseDTO = ResponseUtils.success(ingredientImageRecognitionDTO, "AI API 호출 성공");
        return ResponseEntity.ok(responseDTO);
    }

}
