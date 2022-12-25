package com.dpwns.projectboard.service;


import com.dpwns.projectboard.domain.Hashtag;
import com.dpwns.projectboard.repository.HashtagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;

    public Set<String> parseHashtagNames(String content) {
        if(content == null){
            return Set.of();
        }
        // 참고 : https://en.wikipedia.org/wiki/Regular_expression
        Pattern pattern = Pattern.compile("#[\\w가-힣]+");
        Matcher matcher = pattern.matcher(content.strip());
        Set<String> result = new HashSet<>();

        while(matcher.find()){
            result.add(matcher.group().replace("#", ""));   // group -> parsing , # 제거
        }

        return Set.copyOf(result);  // 불변성 return unmodified set
    }

    public Set<Hashtag> findHashtagsByNames(Set<String> expectedHashtagNames) {
        return new HashSet<>(hashtagRepository.findByHashtagNameIn(expectedHashtagNames));
    }

    // 글이 삭제되었다고 해서 해시태그가 사라지는 것은 아니다. 다른 게시글에 해시태그가 존재할 수 있음
    public void deleteHashtagWithoutArticles(Long hashtagId) {
        Hashtag hashtag = hashtagRepository.getReferenceById(hashtagId);
        if(hashtag.getArticles().isEmpty()){
            hashtagRepository.delete(hashtag);
        }
    }
}