package com.dpwns.projectboard.repository;

import com.dpwns.projectboard.domain.Article;
import com.dpwns.projectboard.domain.QArticle;
import com.dpwns.projectboard.repository.querydsl.ArticleRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        ArticleRepositoryCustom,
        QuerydslPredicateExecutor<Article>,
        QuerydslBinderCustomizer<QArticle>
{
    Page<Article> findByTitleContaining(String title, Pageable pageable);
    Page<Article> findByContentContaining(String content, Pageable pageable);
    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);
    Page<Article> findByHashtag(String hashtag, Pageable pageable);


    @Override
    default void customize(QuerydslBindings bindings, QArticle root){
        bindings.excludeUnlistedProperties(true);       // QuerydslPredicateExecutor에 의해 article의 모든 스펙에 대한 검색이 가능함 -> 검색이 불가능하게 변경
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy);
        // 검색필터를 하나만 - first
//        bindings.bind(root.title).first(StringExpression::likeIgnoreCase);      // like `${v}`
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);  // like `%${v}%`
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);  // like `%${v}%`
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);

    }

}
