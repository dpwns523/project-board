package com.dpwns.projectboard.config;

import com.dpwns.projectboard.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    // CreatedBy나 ModifiedBy에 생성자나 수정자를 넣어주는 역할
    @Bean
    public AuditorAware<String> auditorAware(){
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())// SecurityContextHolder에는 security 정보를 모두 들고 있는 contenxt
                .map(SecurityContext::getAuthentication)    // Context에는 Authenticate정보가 들어있음
                .filter(Authentication::isAuthenticated)    // 인증되었는지
                .map(Authentication::getAuthorities)    // Principal 정보를 가져옴
                .map(BoardPrincipal.class::cast)        // Principal 정보를 캐스팅 (UserDetails의 구현체)
                .map(BoardPrincipal::getUsername);
    }
}
