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

    public List<StudyDocument> searchStudy(String keyword) {
        Query query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(m -> m
                        .fields("title^2", "location")
                        .query(keyword)
                        .fuzziness("2")
                ))
                .build();

        SearchHits<StudyDocument> searchHits = elasticsearchOperations.search(query, StudyDocument.class);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
