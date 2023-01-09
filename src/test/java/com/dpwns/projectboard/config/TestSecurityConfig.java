package com.dpwns.projectboard.config;

import com.dpwns.projectboard.domain.UserAccount;
import com.dpwns.projectboard.dto.UserAccountDto;
import com.dpwns.projectboard.repository.UserAccountRepository;
import com.dpwns.projectboard.service.UserAccountService;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean private UserAccountService userAccountService;

    @BeforeTestMethod
    public void securitySetUp(){
        given(userAccountService.searchUser(anyString()))
                .willReturn(Optional.of(createUserAccountDto()));
        given(userAccountService.saveUser(anyString(), anyString(), anyString(), anyString(), anyString()))
                .willReturn(createUserAccountDto());
    }

    private UserAccountDto createUserAccountDto(){
        return UserAccountDto.of(
                "dpwnsTest",
                "pw",
                "dpwnsTest@email.com",
                "dpwnsTest",
                "test memo"
        );
    }
}
