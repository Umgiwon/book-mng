package com.bookmng.api.book.controller;

import com.bookmng.api.book.domain.dto.request.BookListRequestDTO;
import com.bookmng.api.book.domain.dto.request.BookSaveRequestDTO;
import com.bookmng.api.book.domain.dto.request.BookUpdateRequestDTO;
import com.bookmng.api.book.domain.dto.response.BookResponseDTO;
import com.bookmng.api.book.service.BookService;
import com.bookmng.api.book.service.BookServiceTx;
import com.bookmng.global.domain.dto.BaseResponse;
import com.bookmng.global.domain.dto.BaseResponseFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Book Management API", description = "카테고리 별로 분류되는 도서 관리 시스템 API")
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
    public BaseResponse<BookResponseDTO> saveBook(
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
    ) throws Exception {return BaseResponseFactory.success(bookServiceTx.saveBook(dto));}

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = BookResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "내용 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @Operation(summary = "도서 목록 조회", description = "도서 목록 조회 API " +
                                                "<br>  - 카테고리별 검색 가능" +
                                                "<br>  - 지은이, 제목으로 검색 가능" +
                                                "<br>  - 전체 조건으로 검색 가능" +
                                                "<br>  - 정렬순서 default : 도서 순번")
    @GetMapping("")
    public BaseResponse<List<BookResponseDTO>> getBookList(
            @ParameterObject BookListRequestDTO dto,
            @PageableDefault(page = 0, size = 10, sort = "bookSn", direction = Sort.Direction.DESC) Pageable pageable
    ) throws Exception {return BaseResponseFactory.successWithPagination(bookService.getBookList(dto, pageable));}

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "204", description = "수정 실패", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @Operation(summary = "도서 수정", description = "도서 수정 API " +
                                            "<br>  - 상태 변경 가능 (AVAILABLE: 대여가능, DAMAGED: 훼손됨, LOST: 분실됨)" +
                                            "<br>  - 제목, 지은이 수정 가능" +
                                            "<br>  - 카테고리 수정 가능")
    @PatchMapping("/{bookSn}")
    public BaseResponse<BookResponseDTO> updateBook(
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
    ) throws Exception {return BaseResponseFactory.success(bookServiceTx.updateBook(bookSn, dto));}

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "204", description = "삭제 실패", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @Operation(summary = "도서 삭제", description = "도서 삭제 API")
    @DeleteMapping("/{bookSn}")
    public BaseResponse<BookResponseDTO> deleteBook(
            @PathVariable("bookSn") Long bookSn
    ) throws Exception {return BaseResponseFactory.success(bookServiceTx.deleteBook(bookSn));}
}
