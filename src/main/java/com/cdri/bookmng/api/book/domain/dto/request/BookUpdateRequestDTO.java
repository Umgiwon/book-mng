package com.cdri.bookmng.api.book.domain.dto.request;

import com.cdri.bookmng.global.enums.book.BookStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Schema(description = "도서 수정 요청 DTO")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // json 데이터를 java 객체로 역직렬화 할 때 매핑되지 않은 필드를 무시
public class BookUpdateRequestDTO {

    @Length(max = 100, message = "제목은 100자 이하로 입력해야 합니다.")
    @Schema(description = "제목", example = "너에게 해주지 못한 말들")
    private String title;

    @Length(max = 100, message = "지은이는 20자 이하로 입력해야 합니다.")
    @Schema(description = "지은이", example = "권태영")
    private String author;

    @Schema(description = "상태 (AVAILABLE: 대여가능, DAMAGED: 훼손됨, LOST: 분실됨)", example = "LOST")
    private BookStatus status;

    @Schema(description = "카테고리", example = "[문학]")
    private List<String> categories;
}
