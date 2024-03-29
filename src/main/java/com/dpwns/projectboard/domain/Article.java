package com.dpwns.projectboard.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Article extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter

    @ManyToOne(optional = false)
    @JoinColumn(name="userId")
    private UserAccount userAccount;   // 유저 정보 (ID)

    @Setter @Column(nullable = false)private String title;
    @Setter @Column(nullable = false, length=10000)private String content;

    @ToString.Exclude
    @JoinTable(
            name = "article_hashtag",
            joinColumns = @JoinColumn(name="articleId"),
            inverseJoinColumns = @JoinColumn(name="hashtagId")
    )
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})     // insert, update시 hashtag에도 동기화
    private Set<Hashtag> hashtags = new LinkedHashSet<>();

    // 양방향 연관관계
    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    protected Article() {}

    private Article(UserAccount userAccount, String title, String content){
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
    }

    public static Article of(UserAccount userAccount, String title, String content){
        return new Article(userAccount, title, content);
    }

    public void addHashtag(Hashtag hashtag){
        this.getHashtags().add(hashtag);
    }

    public void addHashtags(Collection<Hashtag> hashtags){
        this.getHashtags().addAll(hashtags);
    }

    public void clearHashtags(){
        this.getHashtags().clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article that)) return false;
        return this.getId()!= null &&
                this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
