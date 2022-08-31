package com.dpwns.projectboard.repository;

import com.dpwns.projectboard.domain.ArticleComment;
import com.dpwns.projectboard.domain.QArticleComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

// querydsl추가하니까 검색기능 작동
@RepositoryRestResource
public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>,
        QuerydslBinderCustomizer<QArticleComment>
{
    List<ArticleComment> findByArticle_Id(Long articleId);

    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {
        bindings.excludeUnlistedProperties(true);       // QuerydslPredicateExecutor에 의해 article의 모든 스펙에 대한 검색이 가능함 -> 검색이 불가능하게 변경
        bindings.including(root.content, root.createdAt, root.createdBy);
        // 검색필터를 하나만 - first
//        bindings.bind(root.title).first(StringExpression::likeIgnoreCase);      // like `${v}`
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);  // like `%${v}%`
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }

    void deleteByIdAndUserAccount_UserId(Long articleCommentId, String userId);

}
