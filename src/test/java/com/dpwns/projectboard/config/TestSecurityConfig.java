package com.dpwns.projectboard.config;

import com.dpwns.projectboard.domain.UserAccount;
import com.dpwns.projectboard.repository.UserAccountRepository;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean private UserAccountRepository userAccountRepository;

    @BeforeTestMethod
    public void securitySetUp(){
        given(userAccountRepository.findById(anyString())).willReturn(Optional.of(UserAccount.of(
                "dpwnsTest",
                "pw",
                "dpwnsTest@email.com",
                "dpwnsTest",
                "test memo"
        )));
    }
}
