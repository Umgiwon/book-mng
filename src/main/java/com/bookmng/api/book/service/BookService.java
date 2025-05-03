package com.bookmng.api.book.service;

import com.bookmng.api.book.domain.dto.request.BookListRequestDTO;
import com.bookmng.api.book.domain.dto.response.BookResponseDTO;
import com.bookmng.api.book.repository.BookRepository;
import com.bookmng.api.book.repository.BookRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookRepositoryCustom bookRepositoryCustom;

    /**
     * 도서 목록 조회
     * @param dto
     * @param pageable
     * @return
     */
    public Page<BookResponseDTO> getBookList(BookListRequestDTO dto, Pageable pageable) {

        // 조건별 도서 목록 전체 조회 (페이징 처리)
        Page<BookResponseDTO> resultPage = bookRepositoryCustom.getBookList(dto, pageable);

        // 도서 순번별로 데이터 groupBy (순서 보존을 위해 LinkedHashMap 사용)
        Map<Long, List<BookResponseDTO>> resultMap = resultPage.getContent().stream()
                .collect(Collectors.groupingBy(BookResponseDTO::getBookSn, LinkedHashMap::new, Collectors.toList()));

        // 각 도서별 데이터 및 카테고리 설정
        List<BookResponseDTO> resultList = resultMap.values().stream()
                .map(books -> {
                    BookResponseDTO first = books.get(0);

                    return BookResponseDTO.builder()
                            .bookSn(first.getBookSn())
                            .title(first.getTitle())
                            .author(first.getAuthor())
                            .status(first.getStatus())
                            .categories(books.stream()
                                    .map(BookResponseDTO::getCategoryName)
                                    .distinct()
                                    .collect(Collectors.toList()))
                            .build();
                })
                .collect(Collectors.toList());

        // 도서별 데이터, 페이징 정보 반환
        return new PageImpl<>(resultList, pageable, resultMap.size());
    }
}
