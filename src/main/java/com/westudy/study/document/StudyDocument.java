package com.westudy.study.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.westudy.study.enums.StudyStates;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "studies")
@Setting(settingPath = "/elasticsearch/settings.json")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long postId;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Text, analyzer = "nori_synonym_analyzer", searchAnalyzer = "nori_synonym_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori_synonym_analyzer")
    private String location;

    @Field(type = FieldType.Integer)
    private int maxMember;

    @Field(type = FieldType.Keyword)
    private StudyStates state;

    @Field(type = FieldType.Integer)
    private int approvedMemberCount;
}
