package com.westudy;

import com.westudy.study.repository.search.StudySearchRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

@Configuration
@ConditionalOnProperty(name = "is.test", havingValue = "true")
public class TestElasticsearchConfig {

    @Bean(name = "elasticsearchTemplate")
    @Primary
    public ElasticsearchOperations elasticsearchTemplate() {
        return Mockito.mock(ElasticsearchOperations.class);
    }

    @Bean
    @Primary
    public StudySearchRepository studySearchRepository() {
        return Mockito.mock(StudySearchRepository.class);
    }
}
