package njb.recipe.global.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import njb.recipe.global.jwt.filter.JwtFilter;
import njb.recipe.global.jwt.TokenProvider;
import njb.recipe.global.jwt.handler.JwtAccessDeniedHandler;
import njb.recipe.global.jwt.handler.JwtAuthenticationEntryPoint;
import njb.recipe.global.jwt.CustomEmailPwdAuthenticationProvider;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.cors(Customizer.withDefaults());

        http.addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);



        http
                .authorizeHttpRequests((request) -> request
                        .requestMatchers("/api/hello","/auth/**").permitAll()
                        .requestMatchers("/favicon.ico","/error").permitAll()
                        .requestMatchers("/").permitAll()
                                .anyRequest().authenticated());
                        //.anyRequest().authenticated());

        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new JwtAccessDeniedHandler()));
        http.exceptionHandling(ehc -> ehc.authenticationEntryPoint(new JwtAuthenticationEntryPoint()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 origin 설정 (예: 로컬과 배포 환경 도메인)
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        // 허용할 HTTP 메소드 설정
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 허용할 헤더 설정
        configuration.setAllowedHeaders(List.of("*"));
        // 자격 증명(쿠키 등) 허용 여부
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder){
        CustomEmailPwdAuthenticationProvider authenticationProvider = new CustomEmailPwdAuthenticationProvider(userDetailsService, passwordEncoder);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }
}
