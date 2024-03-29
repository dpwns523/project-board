package com.dpwns.projectboard.dto.request;

import com.dpwns.projectboard.domain.UserAccount;
import com.dpwns.projectboard.dto.ArticleCommentDto;
import com.dpwns.projectboard.dto.UserAccountDto;
import lombok.Data;

public record ArticleCommentRequest(
        Long articleId,
        Long parentCommentId,
        String content) {
    public  static ArticleCommentRequest of(Long articleId, String content){
        return ArticleCommentRequest.of(articleId, null, content);
    }
    public  static ArticleCommentRequest of(Long articleId, Long parentCommentId, String content){
        return new ArticleCommentRequest(articleId, parentCommentId, content);
    }

    public ArticleCommentDto toDto(UserAccountDto userAccountDto){
        return ArticleCommentDto.of(
                articleId,
                userAccountDto,
                parentCommentId,
                content
        );
    }
}
