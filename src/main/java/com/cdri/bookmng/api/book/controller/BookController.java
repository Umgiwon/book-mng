package com.cdri.bookmng.api.book.controller;

import com.cdri.bookmng.api.book.domain.dto.request.BookListRequestDTO;
import com.cdri.bookmng.api.book.domain.dto.request.BookSaveRequestDTO;
import com.cdri.bookmng.api.book.domain.dto.request.BookUpdateRequestDTO;
import com.cdri.bookmng.api.book.domain.dto.response.BookResponseDTO;
import com.cdri.bookmng.api.book.service.BookService;
import com.cdri.bookmng.api.book.service.BookServiceTx;
import com.cdri.bookmng.global.constant.ResponseMessageConst;
import com.cdri.bookmng.global.domain.dto.BaseResponse;
import com.cdri.bookmng.global.domain.dto.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Book API", description = "카테고리 별로 분류되는 도서 시스템 API 설명")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/book")
public class BookController {

    private final BookService bookService;
    private final BookServiceTx bookServiceTx;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "204", description = "내용 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @Operation(summary = "도서 저장", description = "도서 저장 API " +
                                            "<br>  - 제목, 지은이, 카테고리 필수 " +
                                            "<br>  - 하나 이상의 카테고리 가능 " +
                                            "<br>  - 상태값 default : AVAILABLE(대여가능)")
    @PostMapping("")
    public BaseResponse saveBook(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "json", content = @Content(
                    examples = {
                            @ExampleObject(name = "저장 예제1", value = """
                                      {
                                          "title": "너에게 해주지 못한 말들",
                                          "author": "권태영",
                                          "categories": [
                                              "문학"
                                            ]
                                      }
                            """),
                            @ExampleObject(name = "저장 예제2", value = """
                                      {
                                          "title": "단순하게 배부르게",
                                          "author": "현영서",
                                          "categories": [
                                              "문학",
                                              "경제경영"
                                            ]
                                      }
                            """)
                    }
            ))
            @Valid @RequestBody BookSaveRequestDTO dto
    ) throws Exception {
        BaseResponse baseResponse;

        // 도서 저장
        boolean result = bookServiceTx.saveBook(dto);

        // response set
        baseResponse = result
                ? BaseResponse.getBaseResponseBuilder(HttpStatus.OK.value(), ResponseMessageConst.SAVE_SUCCESS, 1 , true)
                : BaseResponse.getBaseResponseBuilder(HttpStatus.BAD_REQUEST.value(), ResponseMessageConst.SAVE_FAIL, 0, false);

        return baseResponse;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = BookResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "내용 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @Operation(summary = "도서 목록 조회", description = "도서 목록 조회 API " +
                                                "<br>  - 카테고리별 검색 가능" +
                                                "<br>  - 지은이, 제목으로 검색 가능" +
                                                "<br>  - 전체 조건으로 검색 가능" +
                                                "<br>  - 정렬순서 default : 데이터 생성일자")
    @GetMapping("")
    public BaseResponse getBookList(
            @Parameter(name = "title", description = "도서명", example = "너에게 해주지 못한 말들", in = ParameterIn.QUERY, schema = @Schema(implementation = String.class))
            @RequestParam(required = false) String title,
            @Parameter(name = "author", description = "지은이", example = "권태영", in = ParameterIn.QUERY, schema = @Schema(implementation = String.class))
            @RequestParam(required = false) String author,
            @Parameter(name = "categoryName", description = "카테고리", example = "문학", in = ParameterIn.QUERY, schema = @Schema(implementation = String.class))
            @RequestParam(required = false) String categoryName,
            @PageableDefault(page = 0, size = 10, sort = "regDt", direction = Sort.Direction.DESC) Pageable pageable
    ) throws Exception {
        BaseResponse baseResponse;

        // 조회용 dto set
        BookListRequestDTO dto = BookListRequestDTO.builder()
                .title(ObjectUtils.isEmpty(title) ? null : title)
                .author(ObjectUtils.isEmpty(author) ? null : author)
                .categoryName(ObjectUtils.isEmpty(categoryName) ? null : categoryName)
                .build();

        // 도서 목록 조회 (페이징 처리)
        Page<BookResponseDTO> resultPaging = bookService.getBookList(dto, pageable);

        // 데이터 결과값
        List<BookResponseDTO> resultList = resultPaging.getContent();

        // response set
        baseResponse = !ObjectUtils.isEmpty(resultList)
                ? BaseResponse.getBaseResponseBuilder(HttpStatus.OK.value(), ResponseMessageConst.SELECT_SUCCESS, resultList.size(), resultList, new Pagination(resultPaging))
                : BaseResponse.getBaseResponseBuilder(HttpStatus.NO_CONTENT.value(), ResponseMessageConst.NO_CONTENT, 0, new ArrayList<>());

        return baseResponse;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "204", description = "수정 실패", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @Operation(summary = "도서 수정", description = "도서 수정 API " +
                                            "<br>  - 상태 변경 가능 (AVAILABLE: 대여가능, DAMAGED: 훼손됨, LOST: 분실됨)" +
                                            "<br>  - 제목, 지은이 수정 가능" +
                                            "<br>  - 카테고리 수정 가능")
    @PatchMapping("/{bookSn}")
    public BaseResponse updateBook(
            @PathVariable("bookSn") Long bookSn,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "json", content = @Content(
                    examples = {
                            @ExampleObject(name = "상태 변경 예제 (AVAILABLE: 대여가능, DAMAGED: 훼손됨, LOST: 분실됨)", value = """
                                      {
                                          "status": "LOST"
                                      }
                            """),
                            @ExampleObject(name = "카테고리 변경 예제", value = """
                                      {
                                          "categories": [
                                              "인문학",
                                              "IT"
                                            ]
                                      }
                            """),
                            @ExampleObject(name = "기타 수정 예제", value = """
                                      {
                                          "title": "너에게 해주지 못한 말들 수정",
                                          "author": "권태영",
                                          "status": "DAMAGED",
                                          "categories": [
                                              "인문학",
                                              "IT"
                                            ]
                                      }
                            """)
                    }
            ))
            @Valid @RequestBody BookUpdateRequestDTO dto
    ) throws Exception {
        BaseResponse baseResponse;

        // 도서 수정
        boolean result = bookServiceTx.updateBook(bookSn, dto);

        // response set
        baseResponse = result
                ? BaseResponse.getBaseResponseBuilder(HttpStatus.OK.value(), ResponseMessageConst.UPDATE_SUCCESS, 1 , true)
                : BaseResponse.getBaseResponseBuilder(HttpStatus.BAD_REQUEST.value(), ResponseMessageConst.UPDATE_FAIL, 0, false);

        return baseResponse;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "204", description = "삭제 실패", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @Operation(summary = "도서 삭제", description = "도서 삭제 API")
    @DeleteMapping("/{bookSn}")
    public BaseResponse deleteBook(
            @PathVariable("bookSn") Long bookSn
    ) throws Exception {
        BaseResponse baseResponse;

        // 도서 삭제
        boolean result = bookServiceTx.deleteBook(bookSn);

        baseResponse = result
                ? BaseResponse.getBaseResponseBuilder(HttpStatus.OK.value(), ResponseMessageConst.DELETE_SUCCESS, 1, true)
                : BaseResponse.getBaseResponseBuilder(HttpStatus.BAD_REQUEST.value(), ResponseMessageConst.DELETE_FAIL, 0, false);

        return baseResponse;
    }

}
