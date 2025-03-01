package njb.recipe.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.recipe.dto.token.TokenResponseDTO;
import njb.recipe.entity.RefreshToken;
import njb.recipe.repository.MemberRepository;
import njb.recipe.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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

    // 엑세스 토큰 유효시간
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    // 리프레시 토큰 유효시간
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
    private static final String MEMBER_ID = "mid";
    private static final String ROLE = "role";

    /**
     * AccessToken 생성
     */
    public String generateAccessToken(Long id){
        Date now = new Date();

        Date date = new Date(now.getTime() + accessTokenExpirationPeriod);
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT) //JWT
                .withExpiresAt(new Date(now.getTime()+ accessTokenExpirationPeriod))
                .withClaim(MEMBER_ID, id)
                .withClaim("role", "ROLE_USER")
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * RefreshToken 생성
     *
     */
    public String generateRefreshToken(Long id){
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime()+ refreshTokenExpirationPeriod))
                .withClaim(MEMBER_ID, id)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public TokenResponseDTO generateAccessTokenAndRefreshToken(Long id, String ua, Boolean isAutoLogin){
        String accessToken = generateAccessToken(id);
        String refreshToken = generateRefreshToken(id);





        RefreshToken token = RefreshToken.builder()
                .value(refreshToken)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationPeriod/1000))
                .deviceInfo(ua)
                .autoLogin(isAutoLogin)
                .build();

        if(isAutoLogin){
            token.updateAutoLogin(true);
        }

        refreshTokenRepository.save(token);

        memberRepository.findById(id)
                .ifPresent(member -> {
                    member.updateRefreshToken(token);
                });

        return TokenResponseDTO.builder()
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





    public String updateRefreshToken(String refreshToken, Long id){
        log.info("refreshToken : {}", refreshToken);
        String newRefreshToken = generateRefreshToken(id);

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

    


    public String getID(String accessToken){
        if(!validateToken(accessToken)){
            throw new JWTDecodeException("유효하지 않은 토큰입니다.");
        }

        DecodedJWT token = JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(accessToken);

        return token.getClaim(MEMBER_ID).toString();


    }

    public Authentication getAuthentication(String accessToken){
        DecodedJWT token = getDecodedJWT(accessToken);
        //String memberId = token.getClaim(MEMBER_ID).asString();
        String memberId = token.getClaim(MEMBER_ID).toString();
        token.getClaim(ROLE).asArray(String.class);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(token.getClaim(ROLE).asString()));

        UserDetails principal = new CustomUserDetails(memberId, "","", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public DecodedJWT getDecodedJWT(String token){
        return JWT.require(Algorithm.HMAC512(secretKey))
                .build()
                .verify(token);
    }

    public boolean isAutoLogin(String refreshToken){
        long id = Long.parseLong(getID(refreshToken));
        RefreshToken token = refreshTokenRepository.findByValueAndMember_Id(refreshToken, id)
                .orElseThrow(() -> new UsernameNotFoundException("일치하는 토큰이 없습니다."));

        return token.getAutoLogin();
    }

}
