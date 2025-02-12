package njb.recipe.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.dto.member.MemberRequestDTO;
import njb.recipe.dto.member.MemberResponseDTO;
import njb.recipe.dto.token.TokenDTO;
import njb.recipe.dto.token.TokenRequestDTO;
import njb.recipe.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MemberResponseDTO> signup(@RequestBody MemberRequestDTO memberRequestDTO){
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
