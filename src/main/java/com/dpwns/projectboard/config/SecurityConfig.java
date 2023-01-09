package com.dpwns.projectboard.config;

import com.dpwns.projectboard.dto.UserAccountDto;
import com.dpwns.projectboard.dto.security.BoardPrincipal;
import com.dpwns.projectboard.dto.security.KakaoOAuth2Response;
import com.dpwns.projectboard.repository.UserAccountRepository;
import com.dpwns.projectboard.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    // 필터 체인에 Rule 설정
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService)throws Exception {
        // 최신 Security 5.2버전부터는 람다식을 활용해 메소드체인 방식(and사용)을 지양하는 것 같다. 람다식으로 바꿔보자
        return http
                .csrf().disable().cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()   // 일반적으로 사용되는 경로를 확인해보고 우리 프로젝트에 적용 ex) image -> images
                        .mvcMatchers(
                                HttpMethod.GET,
                                "/",
                                "/articles",
                                "/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated())
                .formLogin(withDefaults())  // withDefaults는 아무일도 안하는 디폴트 메서드로 람다식을 지원
                .logout(logout -> logout
                        .logoutSuccessUrl("/"))
                .oauth2Login(oAuth -> oAuth
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                )

                .build();
    }
//    스프링부트 2.7부터는 이렇게 하는 방식을 권장하지 않고 필터체인에서 한번에 처리하는 것을 권장하며 Warning을 주고있음.
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer(){
//        // static resource, css - js 등 권한 체크가 필요없는 정적 리소스
//        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
//    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService){
        return username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        /*
            IoC 컨테이너에선 new를 지양해야하고 Bean을 통한 DI를 모색하자. 하지만, 스프링부트컨테이너에서
            해당 기존 OAuth2.0 기본 서비스는 항상 필요한 것이아니라 AutoConfigure에도 들어가지 않았고,
            공식 문서에서도 Bean으로 등록해서 사용하지 않고, 여기에서만 사용하기 때문에
            new 키워드로 여기서 필요할 때 인스턴스화 하여 사용하도록 한다.
        */
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest); // UserDetailsService에서 제공해야하는 loadByUserName과 같음
            KakaoOAuth2Response kakaoResponse = KakaoOAuth2Response.from(oAuth2User.getAttributes());
            String registrationId = userRequest.getClientRegistration().getRegistrationId();    // yml에 설정한 고유값을 반환 -> "kakao"
            String providerId = String.valueOf(kakaoResponse.id());
            // 카카오에선 username을 주지 않는다. pk로 사용할 id를 생성해야 한다.
            String username = registrationId + "_" + providerId;
            // password는 카카오에서 관리할 일이지 우리 DB와는 관계가 없지만 DB 테이블 설계상 notnull이기에 생성해준다.
            String dummyPassword = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());   // {}에 encoding 알고리즘을 넣어준다.
            return userAccountService.searchUser(username)
                    .map(BoardPrincipal::from)
                    .orElseGet(() ->
                            BoardPrincipal.from(
                                    userAccountService.saveUser(
                                            username,
                                            dummyPassword,
                                            kakaoResponse.email(),
                                            kakaoResponse.nickname(),
                                            null    // memo는 null로 넣어줌
                                    )
                            ));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // TODO: front 웹 도메인으로 변경
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
