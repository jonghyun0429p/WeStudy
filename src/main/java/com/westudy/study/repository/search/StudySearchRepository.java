package com.westudy.study.repository.search;

import com.westudy.study.document.StudyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudySearchRepository extends ElasticsearchRepository<StudyDocument, String> {
}
