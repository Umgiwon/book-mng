package com.cdri.bookmng.api.book.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "도서 목록 조회 요청 DTO")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // json 데이터를 java 객체로 역직렬화 할 때 매핑되지 않은 필드를 무시
public class BookListRequestDTO {

    @Schema(description = "제목", example = "너에게 해주지 못한 말들")
    private String title;

    @Schema(description = "지은이", example = "권태영")
    private String author;

    @Schema(description = "카테고리", example = "문학")
    private String categoryName;
}
