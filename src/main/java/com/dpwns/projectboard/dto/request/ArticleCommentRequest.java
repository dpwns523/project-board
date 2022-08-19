package com.dpwns.projectboard.dto.request;

import com.dpwns.projectboard.domain.UserAccount;
import com.dpwns.projectboard.dto.ArticleCommentDto;
import com.dpwns.projectboard.dto.UserAccountDto;
import lombok.Data;

public record ArticleCommentRequest(Long articleId, String content) {
    public  static ArticleCommentRequest of(Long articleId, String content){
        return new ArticleCommentRequest(articleId, content);
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto){
        return ArticleCommentDto.of(
                articleId,
                userAccountDto,
                content
        );
    }
}
