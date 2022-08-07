package com.dpwns.projectboard.repository;


import com.dpwns.projectboard.config.JpaConfig;
import com.dpwns.projectboard.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("testdb")
@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;


    public JpaRepositoryTest(
            @Autowired ArticleCommentRepository articleCommentRepository,
            @Autowired ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void given_whenSelecting_thenWorksFine(){
        // given
        long previousCount = articleRepository.count();
        Article article = Article.of("new Article", "new Content", "#spring");

        // when
        Article savedArticle = articleRepository.save(article);
        List<Article> articles = articleRepository.findAll();

        // then
        assertThat(articles).isNotNull().hasSize(124);

    }
    @DisplayName("insert 테스트")
    @Test
    void given_whenInserting_thenWorksFine(){
        // given
        long previousCount = articleRepository.count();
        Article article = Article.of("new Article", "new Content", "#spring");

        // when
        Article savedArticle = articleRepository.save(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousCount+1);
    }

    @DisplayName("update 테스트")
    @Test
    void given_whenUpdating_thenWorksFine(){
        // given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHastag = "#springboot";
        article.setHashtag(updatedHastag);

        // when
        Article savedArticle = articleRepository.saveAndFlush(article);

        // then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHastag);
    }

    @DisplayName("delete 테스트")
    @Test
    void given_whendeleting_thenWorksFine(){
        // given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        // when
        articleRepository.delete(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }

}

