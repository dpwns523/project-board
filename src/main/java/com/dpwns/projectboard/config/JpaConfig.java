package com.dpwns.projectboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    // CreatedBy나 ModifiedBy에 생성자나 수정자를 넣어주는 역할
    @Bean
    public AuditorAware<String> auditorAware(){
        return () -> Optional.of("uno");    // TODO: 스프링 시큐리티로 인증 기능을 붙이게 될 때 수정
    }
}
