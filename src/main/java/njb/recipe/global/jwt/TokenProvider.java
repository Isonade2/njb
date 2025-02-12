package njb.recipe.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.dto.token.TokenDTO;
import njb.recipe.entity.RefreshToken;
import njb.recipe.repository.MemberRepository;
import njb.recipe.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class TokenProvider {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String BEARER = "Bearer";
    private static final String EMAIL = "email";
    private static final String ROLE = "role";

    /**
     * AccessToken 생성
     */
    public String generateAccessToken(String email){
        Date now = new Date();

        Date date = new Date(now.getTime() + accessTokenExpirationPeriod);
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT) //JWT
                .withExpiresAt(new Date(now.getTime()+ accessTokenExpirationPeriod))
                .withClaim(EMAIL, email)
                .withClaim("role", "ROLE_USER")
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * RefreshToken 생성
     *
     */
    public String generateRefreshToken(){
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime()+ refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public TokenDTO generateAccessTokenAndRefreshToken(String email, String ua){
        String accessToken = generateAccessToken(email);
        String refreshToken = generateRefreshToken();




        RefreshToken token = RefreshToken.builder()
                .value(refreshToken)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationPeriod/1000))
                .deviceInfo(ua)
                .build();

        refreshTokenRepository.save(token);

        memberRepository.findByEmail(email)
                .ifPresent(member -> {
                    member.updateRefreshToken(token);
                });

        return TokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    public boolean validateToken(String token){
        try{
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e){
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }





    public String updateRefreshToken(String refreshToken){
        log.info("refreshToken : {}", refreshToken);
        String newRefreshToken = generateRefreshToken();

        Optional<RefreshToken> findRefreshToken = refreshTokenRepository.findByValue(refreshToken);
        findRefreshToken.ifPresentOrElse(token -> {
            token.updateValue(newRefreshToken);
        }, () -> {
            throw new UsernameNotFoundException("일치하는 토큰이 없습니다.");
        });

//        refreshTokenRepository.findByValue(refreshToken)
//                .ifPresentOrElse(token -> {
//                    token.updateValue(newRefreshToken);
//                }, () -> {
//                    throw new UsernameNotFoundException("일치하는 토큰이 없습니다.");
//                });

        return newRefreshToken;
    }

    


    public String getEmail(String accessToken){
        if(!validateToken(accessToken)){
            throw new JWTDecodeException("유효하지 않은 토큰입니다.");
        }

        DecodedJWT token = JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(accessToken);

        return token.getClaim(EMAIL).asString();


    }

    public Authentication getAuthentication(String accessToken){
        DecodedJWT token = getDecodedJWT(accessToken);
        String email = token.getClaim(EMAIL).asString();
        token.getClaim(ROLE).asArray(String.class);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(token.getClaim(ROLE).asString()));

        UserDetails principal = new User(token.getClaim(EMAIL).asString(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public DecodedJWT getDecodedJWT(String token){
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token);
    }

}
