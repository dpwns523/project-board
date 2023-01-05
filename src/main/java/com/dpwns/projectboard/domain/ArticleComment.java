package com.dpwns.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;


@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class ArticleComment extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter

    @ManyToOne(optional = false)

    private Article article;   // 게시글 (ID)

    @Setter

    @JoinColumn(name="userId")
    @ManyToOne(optional = false)
    private UserAccount userAccount;   // 유저 정보(ID)

    @Setter
    @Column(updatable = false)  // 한 번 부모댓글을 세팅했으면 변경할 수 없다.
    private Long parentCommentId;   // 부모 댓글 ID, 연관관계를 객체로 하는 것이 아니라 ID로 새로운 Column을 생성

    @ToString.Exclude   // 무한 로딩 방지
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL) // 부모 댓글을 지우면 자식 댓글들을 지운다.
    private Set<ArticleComment> childComments = new LinkedHashSet<>();

    @Setter @Column(nullable = false, length = 500) private String content;

    protected ArticleComment() {}

    private ArticleComment(Article article, UserAccount userAccount, Long parentCommentId, String content){
        this.article = article;
        this.userAccount = userAccount;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }

    public static ArticleComment of(Article article, UserAccount userAccount, String content){
        return new ArticleComment(article, userAccount, null, content);
    }

    public void addChildComment(ArticleComment child){
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;  // 패턴 매칭
        return this.getId() != null &&
                this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
