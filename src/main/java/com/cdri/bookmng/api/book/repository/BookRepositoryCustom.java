package com.cdri.bookmng.api.book.repository;

import com.cdri.bookmng.api.book.domain.dto.request.BookListRequestDTO;
import com.cdri.bookmng.api.book.domain.dto.response.BookResponseDTO;
import com.cdri.bookmng.api.book.domain.dto.response.QBookResponseDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.cdri.bookmng.api.book.domain.entity.QBook.book;
import static com.cdri.bookmng.api.book.domain.entity.QBookCategory.bookCategory;
import static com.cdri.bookmng.api.book.domain.entity.QCategory.category;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookRepositoryCustom {

     private final JPAQueryFactory jpaQueryFactory;
     private final JPAQueryFactory queryFactory;

     /**
      * 도서 목록 조회
      * @param dto
      * @param pageable
      * @return
      */
     public Page<BookResponseDTO> getBookList(BookListRequestDTO dto, Pageable pageable) {
          List<BookResponseDTO> resultList;

          resultList = queryFactory
                  .select(
                          new QBookResponseDTO(
                                  book.bookSn
                                  , book.title
                                  , book.author
                                  , book.status
                                  , category.categoryName
                          )
                  )
                  .from(book)
                  .join(book.categories, bookCategory)
                  .join(bookCategory.category, category)
                  .where(pagingCondition(dto))
                  .offset(pageable.getOffset())
                  .limit(pageable.getPageSize())
                  .orderBy(book.createdDate.desc())
                  .fetch();

          // 전체 데이터 카운트
          JPAQuery<Long> countQuery = queryFactory
                  .select(book.countDistinct())
                  .from(book)
                  .join(book.categories, bookCategory)
                  .join(bookCategory.category, category)
                  .where(pagingCondition(dto));

          return PageableExecutionUtils.getPage(resultList, pageable, countQuery::fetchOne);
     }

     /**
      * 페이징 처리시 조건절
      * @param dto
      * @return
      */
     private BooleanBuilder pagingCondition(BookListRequestDTO dto) {
          BooleanBuilder builder = new BooleanBuilder();

          if(dto.getTitle() != null) {
               builder.and(builder.and(eqBookTitle(dto.getTitle())));
          }

          if(dto.getAuthor() != null) {
               builder.and(builder.and(eqBookAuthor(dto.getAuthor())));
          }

          if(dto.getCategoryName() != null) {
               builder.and(builder.and(eqBookCategory(dto.getCategoryName())));
          }

          return builder;
     }

     /* ******************* 동적 쿼리를 위한 BooleanExpression *******************/

     /**
      * 도서 제목 조회 조건
      * @param bookTitle
      * @return
      */
     private BooleanExpression eqBookTitle(String bookTitle) {
          return (!StringUtils.isEmpty(bookTitle)) ? book.title.contains(bookTitle) : null;
     }

     /**
      * 도서 지은이 조회 조건
      * @param author
      * @return
      */
     private BooleanExpression eqBookAuthor(String author) {
          return (!StringUtils.isEmpty(author)) ? book.author.contains(author) : null;
     }

     /**
      * 도서 카테고리 조회 조건
      * @param categoryName
      * @return
      */
     private BooleanExpression eqBookCategory(String categoryName) {
          return (!StringUtils.isEmpty(categoryName)) ? category.categoryName.contains(categoryName) : null;
     }
}
