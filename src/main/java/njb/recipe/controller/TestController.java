package njb.recipe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.global.jwt.CustomUserDetails;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
public class TestController {

    private final OpenAiChatModel chatModel;


    @GetMapping("/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("hello");
    }


    @GetMapping("/auth-test")
    public ResponseEntity<String> authTest(@AuthenticationPrincipal CustomUserDetails userDetails){
        log.info("userDetails: {}", userDetails);
        return ResponseEntity.ok("You are authenticated, "+"memberId: " +userDetails.getMemberId());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("server is running");
    }

    @PostMapping("/ai-test")
    public String aiTest(@RequestParam("file")MultipartFile file){
        UserMessage userMessage = new UserMessage("Explain what do you see ingredient on this picture? please respond json format", new Media(MimeTypeUtils.IMAGE_PNG, file.getResource()));
        ChatResponse call = chatModel.call(new Prompt(userMessage));
        return call.toString();
    }
}
