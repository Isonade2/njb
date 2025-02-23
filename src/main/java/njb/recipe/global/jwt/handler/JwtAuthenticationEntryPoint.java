package njb.recipe.global.jwt.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("JwtAuthenticationEntryPoint 진입");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=utf-8");

        // 응답에 담을 메시지(필요한 만큼 확장 가능)
        /**
         * {
         *     "status": "fail",
         *     "message": "오류 메시지",
         * }
         */
        String json = String.format("{\"status\": \"%s\", \"message\": \"%s\"}",
                "fail",
                "Unauthorized: "+ authException.getMessage()
        );

        // 실제로 write
        response.getWriter().write(json);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
