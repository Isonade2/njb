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
import njb.recipe.dto.token.TokenResponseDTO;
import njb.recipe.dto.token.TokenRequestDTO;
import njb.recipe.global.jwt.TokenProvider;
import njb.recipe.handler.exception.DuplicateEmailException;
import njb.recipe.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenProvider tokenProvider;

    @Value("${jwt.access.expiration_seconds}")
    private Integer accessTokenExpirationSeconds;
    @Value("${jwt.refresh.expiration_seconds}")
    private Integer refreshTokenExpirationSeconds;
    @Value("${jwt.domain}")
    private String domain;


    /**
     * 이메일 중복 체크 API
     * @param email
     */
    @GetMapping("/checkEmail")
    public ResponseEntity<ApiResponseDTO<Boolean>> checkEmail(@RequestParam String email){
        if(email != null && !email.isEmpty()){
            boolean checked = authService.checkEmail(email);
            if(checked){
                throw new DuplicateEmailException("이미 사용중인 이메일입니다.");
            }else{
                return ResponseEntity.ok(ResponseUtils.success(!checked, "사용 가능 이메일"));
            }
        }
        return null;
    }


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
    public ResponseEntity<ApiResponseDTO<TokenResponseDTO>> login(@Valid @RequestBody MemberRequestDTO memberRequestDTO, HttpServletRequest request, HttpServletResponse response){
        String ua = request.getHeader("User-Agent");

        TokenResponseDTO tokenResponseDTO = authService.login(memberRequestDTO, ua);


        addCookiesToResponse(tokenResponseDTO, response, memberRequestDTO.getAutoLogin());


        ApiResponseDTO<TokenResponseDTO> responseDTO = ResponseUtils.success(tokenResponseDTO, "로그인 성공");
        return ResponseEntity.ok(responseDTO);
    }



    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<TokenResponseDTO>> reissue(@Valid @RequestBody TokenRequestDTO tokenRequestDTO, HttpServletRequest request, HttpServletResponse response){
        String ua = request.getHeader("User-Agent");


        TokenResponseDTO tokenResponseDTO = authService.reissue(tokenRequestDTO, ua);

        log.info("refresh token : {}", tokenResponseDTO.getRefreshToken());


        boolean autoLogin = tokenProvider.isAutoLogin(tokenResponseDTO.getRefreshToken());
        addCookiesToResponse(tokenResponseDTO, response, autoLogin);

        ApiResponseDTO<TokenResponseDTO> responseDTO = ResponseUtils.success(tokenResponseDTO, "토큰 재발급 성공");
        return ResponseEntity.ok(responseDTO);
    }


    private void addCookiesToResponse(TokenResponseDTO tokenResponseDTO, HttpServletResponse response, Boolean autoLogin) {
        Cookie accessToken = new Cookie("accessToken", tokenResponseDTO.getAccessToken());
        Cookie refreshToken = new Cookie("refreshToken", tokenResponseDTO.getRefreshToken());


        //accessToken.setSecure(true); //HTTPS 설정
        accessToken.setHttpOnly(true);
        accessToken.setPath("/");
        accessToken.setDomain(domain);
        accessToken.setMaxAge(accessTokenExpirationSeconds); // 5분

        //refreshToken.setSecure(true); // HTTPS 설정
        refreshToken.setHttpOnly(true);
        refreshToken.setPath("/");
        refreshToken.setDomain(domain);
        if(autoLogin != null && autoLogin){ // 자동 로그인이 체크 되었을 떄
            refreshToken.setMaxAge(refreshTokenExpirationSeconds); // 14일
        }
        response.addCookie(accessToken);
        response.addCookie(refreshToken);

//        response.addHeader("Set-Cookie", String.format("accessToken=%s; Max-Age=%d; Path=/; Domain=nang.n-e.kr; HttpOnly; SameSite=Lax",
//                tokenDTO.getAccessToken(), 60 * 60 * 24));
//        response.addHeader("Set-Cookie", String.format("refreshToken=%s; Max-Age=%d; Path=/; Domain=nang.n-e.kr; HttpOnly; SameSite=Lax",
//                tokenDTO.getRefreshToken(), 60 * 60 * 24 * 14));
    }

    private void addCookiesToResponse(TokenResponseDTO tokenResponseDTO, HttpServletResponse response) {
        Cookie accessToken = new Cookie("accessToken", tokenResponseDTO.getAccessToken());
        Cookie refreshToken = new Cookie("refreshToken", tokenResponseDTO.getRefreshToken());

        accessToken.setHttpOnly(true);
        //accessToken.setSecure(true); //HTTPS 설정
        accessToken.setPath("/");
        accessToken.setDomain("nang.n-e.kr");
        //accessToken.setMaxAge(accessTokenExpirationSeconds); // 5분

        refreshToken.setHttpOnly(true);
        //refreshToken.setSecure(true); // HTTPS 설정
        refreshToken.setPath("/");
        accessToken.setDomain("nang.n-e.kr");
        response.addCookie(accessToken);
        response.addCookie(refreshToken);
//        response.addHeader("Set-Cookie", String.format("accessToken=%s; Max-Age=%d; Path=/; Domain=nang.n-e.kr; HttpOnly; SameSite=Lax",
//                tokenDTO.getAccessToken(), 60 * 60 * 24));
//        response.addHeader("Set-Cookie", String.format("refreshToken=%s; Max-Age=%d; Path=/; Domain=nang.n-e.kr; HttpOnly; SameSite=Lax",
//                tokenDTO.getRefreshToken(), 60 * 60 * 24 * 14));
    }

}
