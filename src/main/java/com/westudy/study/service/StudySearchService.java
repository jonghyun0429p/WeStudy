package com.westudy.study.service;

import com.westudy.study.document.StudyDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudySearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public List<StudyDocument> searchStudy(String keyword, String category, String techStack) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q.bool(b -> {
                    // 1. 만약 모든 조건이 비어있다면 match_all 수행
                    if ((keyword == null || keyword.trim().isEmpty()) && 
                        (category == null || category.trim().isEmpty() || "ALL".equalsIgnoreCase(category)) && 
                        (techStack == null || techStack.trim().isEmpty())) {
                        b.must(m -> m.matchAll(ma -> ma));
                    } else {
                        // 2. 키워드 검색
                        if (keyword != null && !keyword.trim().isEmpty()) {
                            b.must(m -> m.multiMatch(mm -> mm
                                    .fields("title^2", "location")
                                    .query(keyword)
                                    .fuzziness("2")
                            ));
                        }
                    }
                    
                    // 3. 분야 필터링
                    if (category != null && !category.trim().isEmpty() && !"ALL".equalsIgnoreCase(category)) {
                        b.filter(f -> f.term(t -> t.field("category").value(category)));
                    }
                    
                    // 4. 기술 스택 필터링
                    if (techStack != null && !techStack.trim().isEmpty()) {
                        b.filter(f -> f.match(m -> m.field("techStacks").query(techStack)));
                    }
                    
                    return b;
                }))
                .build();

        SearchHits<StudyDocument> searchHits = elasticsearchOperations.search(query, StudyDocument.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
