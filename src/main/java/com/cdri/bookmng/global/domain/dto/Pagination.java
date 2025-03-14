package com.cdri.bookmng.global.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Page;

@Schema(description = "페이지네이션 정보")
@Data
public class Pagination {

    @Schema(description = "현재 페이지 번호", example = "1")
    private int pageNumber;

    @Schema(description = "한 페이지당 보여질 데이터 개수", example = "10")
    private int pageSize;

    @Schema(description = "현재 페이지 데이터 개수", example = "3")
    private int numberOfElements;

    @Schema(description = "전체 페이지 개수", example = "3")
    private int totalPages;

    @Schema(description = "전체 데이터 개수", example = "17")
    private long totalElements;

    public Pagination(Page page) {
        this.pageNumber = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.numberOfElements = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
    }
}
