package njb.recipe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.dto.ApiResponseDTO;
import njb.recipe.dto.ResponseUtils;
import njb.recipe.dto.refri.IngredientImageRecognitionDTO;
import njb.recipe.entity.AiApiUsage;
import njb.recipe.entity.Member;
import njb.recipe.handler.exception.AiResponseError;
import njb.recipe.handler.exception.ApiUsageExceedException;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AiService {
    private final AiApiUsageRepository aiApiUsageRepository;
    private final MemberRepository memberRepository;
    private final OpenAiChatModel chatModel;

    public List<IngredientImageRecognitionDTO> recognizeIngredient(MultipartFile file) {
        try {
            // AI에게 전달할 프롬프트 구성
            UserMessage userMessage = new UserMessage(
                    "냉장고 관리 앱의 기능 개발을 위해, 첨부된 이미지에서 보이는 음식 재료를 인식하여 "
                            + "특정 JSON 형식으로만 응답해주는 AI 기능이 필요합니다. "
                            + "만약 여러 재료가 인식된다면 JSON 배열로 모두 반환해주세요. 예를 들어, "
                            + "토마토 3개, 양파 2개가 인식된다면:\n\n"
                            + "[\n"
                            + "  {\n"
                            + "    \"name\": \"토마토\",\n"
                            + "    \"quantity\": \"3\",\n"
                            + "    \"category\": \"과일\"\n"
                            + "  },\n"
                            + "  {\n"
                            + "    \"name\": \"양파\",\n"
                            + "    \"quantity\": \"2\",\n"
                            + "    \"category\": \"채소\"\n"
                            + "  }\n"
                            + "]\n\n"
                            + "만약 이미지를 인식할 수 없다면 다음과 같은 JSON 객체로만 응답해주세요:\n"
                            + "{ \"error\": \"이미지를 인식할 수 없습니다.\" }",
                    new Media(MimeTypeUtils.IMAGE_PNG, file.getResource())
            );


            // AI API 호출
            ChatResponse chatResponse = chatModel.call(new Prompt(userMessage));
            String jsonResponse = chatResponse.getResult().getOutput().getText();
            log.info("AI API Response: {}", jsonResponse);

            // AI API 응답 문자열 전처리
            jsonResponse = cleanJson(jsonResponse);

            // 응답 문자열을 DTO로 파싱
            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(jsonResponse);
            if(rootNode.has("error")){
                throw new AiResponseError(rootNode.get("error").asText());
            }
            if(rootNode.isArray()) {
                List<IngredientImageRecognitionDTO> resultList = mapper.readValue(jsonResponse,
                        mapper.getTypeFactory().constructCollectionType(List.class, IngredientImageRecognitionDTO.class));
                return resultList;
            }else{
                IngredientImageRecognitionDTO singleResult = mapper.readValue(jsonResponse, IngredientImageRecognitionDTO.class);
                return List.of(singleResult);
            }


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private String cleanJson(String jsonResponse) {
        Pattern pattern = Pattern.compile("\\{.*\\}|\\[.*\\]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(jsonResponse.trim());
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new AiResponseError("유효한 JSON 응답을 추출할 수 없습니다.");
        }
//        String cleanJson = jsonResponse.trim();
//        if (cleanJson.startsWith("```json")) {
//            cleanJson = cleanJson.substring("```json".length()).trim();
//        }
//        if (cleanJson.endsWith("```")) {
//            cleanJson = cleanJson.substring(0, cleanJson.lastIndexOf("```")).trim();
//        }
//        return cleanJson;
    }

    public void checkAiApiUsage(Long memberId) {
        aiApiUsageRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        aiApiUsage -> {
                            if (aiApiUsage.getCallCount() > 10) {
                                throw new ApiUsageExceedException("AI API 사용량 초과");
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
