package njb.recipe.global.oauth2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.controller.AuthController;
import njb.recipe.dto.token.TokenResponseDTO;
import njb.recipe.entity.Member;
import njb.recipe.global.jwt.TokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;
    private static final String URI = "/auth/success";

    @Value("${jwt.access.expiration_seconds}")
    private Integer accessTokenExpirationSeconds;
    @Value("${jwt.refresh.expiration_seconds}")
    private Integer refreshTokenExpirationSeconds;
    @Value("${jwt.domain}")
    private String domain;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2SuccessHandler.onAuthenticationSuccess");
        String ua = request.getHeader("User-Agent");
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Member member = principal.member();
        Long id = principal.getId();
        // accessToken, refreshToken 발급
        TokenResponseDTO tokenResponseDTO = tokenProvider.generateAccessTokenAndRefreshToken(member, ua, false);

        addCookiesToResponse(tokenResponseDTO, response, false);

        response.sendRedirect(URI);
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
}
