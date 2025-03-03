package njb.recipe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.dto.ApiResponseDTO;
import njb.recipe.dto.ResponseUtils;
import njb.recipe.dto.refri.IngredientImageRecognitionDTO;
import njb.recipe.entity.AiApiUsage;
import njb.recipe.entity.Member;
import njb.recipe.handler.exception.UserIdNotFountException;
import njb.recipe.repository.AiApiUsageRepository;
import njb.recipe.repository.MemberRepository;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AiService {
    private final AiApiUsageRepository aiApiUsageRepository;
    private final MemberRepository memberRepository;
    private final OpenAiChatModel chatModel;

    public IngredientImageRecognitionDTO recognizeIngredient(MultipartFile file) {
        try {
            // AI에게 전달할 프롬프트 구성
            UserMessage userMessage = new UserMessage("냉장고 관리 앱의 기능 개발을 위해, 첨부된 이미지에서 보이는 음식 재료를 인식하여 " +
                    "특정 JSON 형식으로 응답해주는 AI 기능이 필요합니다. 이 기능은 사용자가 사진 촬영을 통해 간편하게 냉장고 안에 있는 재료를 추가할 수 있도록 합니다. " +
                    "첨부된 이미지에서 재료를 인식한 후, 오직 다음과 같은 형식의 JSON으로만 응답해야 합니다:\n" +
                    "\n\n" +
                    " " +
                    "형식예시: " +
                    "{\n" +
                    "    \"name\": \"토마토\",\n" +
                    "    \"quantity\": \"3\",\n" +
                    "    \"category\": \"[육류, 채소, 과일, 수산물, 달걀/유제품, 양념/소스, 가공식품, 곡류, 기타]\"\n" +
                    "}",
                    new Media(MimeTypeUtils.IMAGE_PNG, file.getResource()));

            // AI API 호출
            ChatResponse chatResponse = chatModel.call(new Prompt(userMessage));
            String jsonResponse = chatResponse.getResult().getOutput().getText();

            // AI API 응답 문자열 전처리
            jsonResponse = cleanJson(jsonResponse);

            // 응답 문자열을 DTO로 파싱
            ObjectMapper mapper = new ObjectMapper();
            IngredientImageRecognitionDTO result = mapper.readValue(jsonResponse, IngredientImageRecognitionDTO.class);
            return result;


        } catch (Exception e) {
            log.error("AI API Error", e);
            throw new RuntimeException("AI API Error");
        }

    }

    private String cleanJson(String jsonResponse) {
        String cleanJson = jsonResponse.trim();
        if (cleanJson.startsWith("```json")) {
            cleanJson = cleanJson.substring("```json".length()).trim();
        }
        if (cleanJson.endsWith("```")) {
            cleanJson = cleanJson.substring(0, cleanJson.lastIndexOf("```")).trim();
        }
        return cleanJson;
    }

    public void checkAiApiUsage(Long memberId) {
        aiApiUsageRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        aiApiUsage -> {
                            if (aiApiUsage.getCallCount() > 10) {
                                throw new RuntimeException("AI API 사용량 초과");
                            }
                            aiApiUsage.updateCallCount(aiApiUsage.getCallCount() + 1);
                        },
                        () -> {
                            Member member = memberRepository.findById(memberId)
                                    .orElseThrow(() -> new UserIdNotFountException("사용자를 찾을 수 없습니다."));
                            AiApiUsage apiUsage = AiApiUsage.builder()
                                    .member(member)
                                    .usageDate(LocalDate.now())
                                    .callCount(1)
                                    .build();

                            aiApiUsageRepository.save(apiUsage);
                        }
                );
    }
}
