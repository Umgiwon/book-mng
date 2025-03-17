package com.cdri.bookmng.api.book.service;

import com.cdri.bookmng.api.book.domain.dto.request.BookSaveRequestDTO;
import com.cdri.bookmng.api.book.domain.dto.request.BookUpdateRequestDTO;
import com.cdri.bookmng.api.book.domain.entity.Book;
import com.cdri.bookmng.api.book.domain.entity.Category;
import com.cdri.bookmng.api.book.repository.BookRepository;
import com.cdri.bookmng.api.book.repository.BookRepositoryCustom;
import com.cdri.bookmng.api.book.repository.CategoryRepository;
import com.cdri.bookmng.global.enums.common.ApiReturnCode;
import com.cdri.bookmng.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceTx {

    private final BookRepository bookRepository;
    private final BookRepositoryCustom bookRepositoryCustom;
    private final CategoryRepository categoryRepository;

    /**
     * 도서 저장
     * @param dto
     * @return
     */
    public boolean saveBook(BookSaveRequestDTO dto) {

        // 저장할 도서 entity 생성
        Book saveBook = createBookEntity(dto);

        // 도서 저장
        bookRepository.save(saveBook);

        // 카테고리 목록 추가
        for(String categoryName : dto.getCategories()) {

            // 기존 카테고리 데이터 있는지 확인 후 없을 경우 새로 저장
            Category category = categoryRepository.findByCategoryName(categoryName)
                    .orElseGet(() -> categoryRepository.save(new Category(categoryName)));

            // book 영속성 컨텍스트에 category 추가
            saveBook.addCategory(category);
        }

        return true;
    }

    /**
     * 도서 entity 생성
     * @param dto
     * @return
     */
    private Book createBookEntity(BookSaveRequestDTO dto) {

        validateBook(dto);

        return Book.builder()
                .title(dto.getTitle())
                .author(dto.getAuthor())
                .build();
    }

    /**
     * 도서 validate
     * @param dto
     */
    private void validateBook(BookSaveRequestDTO dto) {

        // 카테고리 여부 체크
        if(ObjectUtils.isEmpty(dto.getCategories())) {
            throw new BusinessException(ApiReturnCode.BOOK_CATEGORY_ESSENTIAL_ERROR);
        }
    }

    /**
     * 도서 수정
     * @param dto
     * @return
     */
    public boolean updateBook(Long bookSn, BookUpdateRequestDTO dto) {

        // 수정할 도서 entity 조회
        Book updateBook = bookRepository.findById(bookSn)
                .orElseThrow(() -> new BusinessException(ApiReturnCode.NO_DATA_ERROR));

        // entity 영속성 컨텍스트 수정
        updateBook(updateBook, dto);

        return true;
    }

    /**
     * 도서 수정 (수정할 값이 있는 데이터만 수정)
     * @param book
     * @param dto
     */
    private void updateBook(Book book, BookUpdateRequestDTO dto) {

        Optional.ofNullable(dto.getTitle()).ifPresent(book::setTitle); // 제목
        Optional.ofNullable(dto.getAuthor()).ifPresent(book::setAuthor); // 지은이
        Optional.ofNullable(dto.getStatus()).ifPresent(book::setStatus); // 상태

        // 카테고리
        if(!ObjectUtils.isEmpty(dto.getCategories())) {

            // 카테고리 초기화
            book.getCategories().clear();

            // 기존 카테고리 데이터 있는지 확인 후 없을 경우 새로 저장
            for(String categoryName : dto.getCategories()) {
                Category category = categoryRepository.findByCategoryName(categoryName)
                        .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
                book.addCategory(category);
            }
        }
    }

    /**
     * 도서 삭제
     * @param bookSn
     * @return
     */
    public boolean deleteBook(Long bookSn) {

        // 삭제할 entity 조회
        Book updateBook = bookRepository.findById(bookSn)
                .orElseThrow(() -> new BusinessException(ApiReturnCode.NO_DATA_ERROR));

        // 삭제
        bookRepository.delete(updateBook);

        return true;
    }
}
