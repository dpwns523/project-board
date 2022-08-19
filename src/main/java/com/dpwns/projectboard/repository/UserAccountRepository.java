package com.dpwns.projectboard.repository;

import com.dpwns.projectboard.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccount, String> {
}
