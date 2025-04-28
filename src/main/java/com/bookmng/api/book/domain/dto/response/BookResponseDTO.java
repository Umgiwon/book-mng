package com.bookmng.api.book.domain.dto.response;

import com.bookmng.global.enums.book.BookStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Schema(description = "Book 응답 DTO")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // json 데이터를 java 객체로 역직렬화 할 때 매핑되지 않은 필드를 무시
public class BookResponseDTO implements Serializable {

    @Schema(description = "도서 순번", example = "1")
    private Long bookSn;

    @Schema(description = "제목", example = "너에게 해주지 못한 말들")
    private String title;

    @Schema(description = "지은이", example = "권태영")
    private String author;

    @Schema(description = "상태 (AVAILABLE: 대여가능, DAMAGED: 훼손됨, LOST: 분실됨)", example = "LOST")
    private BookStatus status;

    @JsonIgnore
    @Schema(description = "카테고리명", example = "문학")
    private String categoryName;

    @Schema(description = "카테고리 목록", example = "[문학]")
    private List<String> categories;

    @QueryProjection
    public BookResponseDTO(Long bookSn, String title, String author, BookStatus status, String categoryName) {
        this.bookSn = bookSn;
        this.title = title;
        this.author = author;
        this.status = status;
        this.categoryName = categoryName;
    }
}
