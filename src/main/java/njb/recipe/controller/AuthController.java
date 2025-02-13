package njb.recipe.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.dto.ApiResponseDTO;
import njb.recipe.dto.ResponseUtils;
import njb.recipe.dto.member.MemberRequestDTO;
import njb.recipe.dto.member.MemberResponseDTO;
import njb.recipe.dto.member.SignupRequestDTO;
import njb.recipe.dto.token.TokenDTO;
import njb.recipe.dto.token.TokenRequestDTO;
import njb.recipe.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDTO<?>> signup(@Validated @RequestBody SignupRequestDTO signupRequestDTO){
        authService.registerUser(signupRequestDTO);

        ApiResponseDTO<Object> response = ResponseUtils.success("회원가입 성공");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/activate")
    public ResponseEntity<ApiResponseDTO<?>> activate(@RequestParam String token){
        authService.activateUser(token);
        return ResponseEntity.ok(ResponseUtils.success("계정 활성화 성공"));
    }


    @PostMapping("/signup1")
    public ResponseEntity<MemberResponseDTO> signup1(@RequestBody MemberRequestDTO memberRequestDTO){
        return ResponseEntity.ok(authService.signup(memberRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody MemberRequestDTO memberRequestDTO, HttpServletRequest request, HttpServletResponse response){
        String ua = request.getHeader("User-Agent");

        TokenDTO tokenDTO = authService.login(memberRequestDTO, ua);
        addCookiesToResponse(tokenDTO, response);

        return ResponseEntity.ok(tokenDTO);
    }



    @PostMapping("/refresh")
    public ResponseEntity<TokenDTO> reissue(@RequestBody TokenRequestDTO tokenRequestDTO, HttpServletRequest request, HttpServletResponse response){
        String ua = request.getHeader("User-Agent");


        TokenDTO tokenDTO = authService.reissue(tokenRequestDTO, ua);

        log.info("refresh token : {}", tokenDTO.getRefreshToken());
        addCookiesToResponse(tokenDTO, response);

        return ResponseEntity.ok(tokenDTO);
    }



    private void addCookiesToResponse(TokenDTO tokenDTO, HttpServletResponse response) {
        Cookie accessToken = new Cookie("accessToken", tokenDTO.getAccessToken());
        Cookie refreshToken = new Cookie("refreshToken", tokenDTO.getRefreshToken());

        accessToken.setHttpOnly(true);
        //accessToken.setSecure(true);
        accessToken.setPath("/");
        accessToken.setMaxAge(60 * 60 * 24); // 1일

        refreshToken.setHttpOnly(true);
        //refreshToken.setSecure(true);
        refreshToken.setPath("/");
        refreshToken.setMaxAge(60 * 60 * 24 * 14); // 14일

        response.addCookie(accessToken);
        response.addCookie(refreshToken);
    }

}
