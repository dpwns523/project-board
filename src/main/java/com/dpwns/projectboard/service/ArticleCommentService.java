package com.dpwns.projectboard.service;

import com.dpwns.projectboard.domain.Article;
import com.dpwns.projectboard.domain.ArticleComment;
import com.dpwns.projectboard.domain.UserAccount;
import com.dpwns.projectboard.dto.ArticleCommentDto;
import com.dpwns.projectboard.repository.ArticleCommentRepository;
import com.dpwns.projectboard.repository.ArticleRepository;
import com.dpwns.projectboard.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return articleCommentRepository.findByArticle_Id(articleId)
                .stream()
                .map(ArticleCommentDto::from)
                .toList();
    }

    public void saveArticleComment(ArticleCommentDto dto) {
        try{
            Article article = articleRepository.getReferenceById(dto.articleId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
            // 댓글이 부모, 자식 관계인지 확인
            ArticleComment articleComment = dto.toEntity(article, userAccount);
            if(dto.parentCommentId() != null){
                ArticleComment parentComment = articleCommentRepository.getReferenceById(dto.parentCommentId());
                parentComment.addChildComment(articleComment);
            } else{
                articleCommentRepository.save(articleComment);
            }
        }catch (EntityNotFoundException e){
            log.warn("댓글 저장 실패, 댓글 작성에 필요한 정보를 찾을 수 없습니다. - {}", e.getLocalizedMessage());
        }
    }
    /**
     * @deprecated 댓글 수정 기능은 클라이언트에서 생각할 점이 많아지기 떄문에, 이번 개발에서는 제공하지 않기로 했다.
     */
    @Deprecated
    public void updateArticleComment(ArticleCommentDto dto) {
        try {
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if (dto.content() != null) { articleComment.setContent(dto.content()); }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 업데이트 실패. 댓글을 찾을 수 없습니다 - dto: {}", dto);
        }
    }

    public void deleteArticleComment(Long articleCommentId, String userId) {
        articleCommentRepository.deleteByIdAndUserAccount_UserId(articleCommentId, userId);
    }
}
