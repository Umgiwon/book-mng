package com.cdri.bookmng;

import com.cdri.bookmng.api.book.domain.dto.request.BookSaveRequestDTO;
import com.cdri.bookmng.api.book.domain.dto.request.BookUpdateRequestDTO;
import com.cdri.bookmng.api.book.domain.entity.Book;
import com.cdri.bookmng.api.book.domain.entity.Category;
import com.cdri.bookmng.api.book.repository.BookRepository;
import com.cdri.bookmng.api.book.repository.CategoryRepository;
import com.cdri.bookmng.global.enums.book.BookStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class BookMngApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {

        // 테스트 실행 전 데이터 초기화
        bookRepository.deleteAll();
        categoryRepository.deleteAll();

        // 테스트 초기 데이터 생성
        List<Book> books = List.of(
                createBook("너에게 해주지 못한 말들", "권태영", List.of("문학", "경제경영"))
                , createBook("단순하게 배부르게", "현영서", List.of("문학"))
                , createBook("게으른 사랑", "권태영", List.of("문학"))
               , createBook("트랜드 코리아 2322", "권태영", List.of("경제경영"))
//               , createBook("초격자 투자", "장동혁", List.of("경제경영"))
//               , createBook("파이어족 강환국의 하면 되지 안흔다! 퀀트투자", "홍길동", List.of("경제경영"))
//               , createBook("진심보다 밥", "이서연", List.of("인문학"))
//               , createBook("실패에 대하여 생각하지 마라", "위성원", List.of("인문학"))
//               , createBook("실리콘밸리 리더십 쉽다", "지승열", List.of("IT"))
//               , createBook("데이터분석을 위한 A 프로그래밍", "지승열", List.of("IT"))
//               , createBook("인공지능1-12", "장동혁", List.of("IT"))
//               , createBook("-1년차 게임 개발", "위성원", List.of("IT"))
//               , createBook("Skye가 알려주는 피부 채색의 비결", "권태영", List.of("IT"))
//               , createBook("자연의 발전", "장지명", List.of("과학"))
//               , createBook("코스모스 필 무렵", "이승열", List.of("과학"))
        );
        bookRepository.saveAll(books);
    }

    /**
     * 도서 초기화 객체 생성
     * @param title
     * @param author
     * @param categoryNames
     * @return
     */
    private Book createBook(String title, String author, List<String> categoryNames) {
        Book book = Book.builder()
                .title(title)
                .author(author)
                .build();

        // 카테고리  추가
        for(String categoryName : categoryNames) {

            // 기존 카테고리 데이터 있는지 확인 후 없을 경우 새로 저장
            Category category = categoryRepository.findByCategoryName(categoryName)
                    .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
            book.addCategory(category);
        }

        return book;
    }

    @Test
    @DisplayName("도서 저장 API 테스트")
    void saveBookTest() throws Exception {

        // given : 저장할 도서 요청 DTO 생성
        BookSaveRequestDTO saveDto = BookSaveRequestDTO.builder()
                .title("초격자 투자")
                .author("장동혁")
                .categories(List.of("문학", "경제경영"))
                .build();

        // when : API 호출 (도서 저장)
        mockMvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto)))
                .andExpect(status().isOk())
                .andDo(print());

        // then : 저장된 데이터 검증
        // 도서 검증
        Book savedBook = bookRepository.findByTitle(saveDto.getTitle()).orElseThrow();
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo(saveDto.getTitle());
        assertThat(savedBook.getAuthor()).isEqualTo(saveDto.getAuthor());
        assertThat(savedBook.getCategories().size()).isEqualTo(saveDto.getCategories().size());

        // 카테고리 검증
        Category savedCategory;
        for(String categoryName : saveDto.getCategories()) {
            savedCategory = categoryRepository.findByCategoryName(categoryName).orElseThrow();
            assertThat(savedCategory).isNotNull();
            assertThat(savedCategory.getCategoryName()).isEqualTo(categoryName);
        }
    }

    @Test
    @DisplayName("도서 수정 (전체 데이터 수정) API 테스트")
    void updateBookTest() throws Exception {

        // given
        // 수정할 entity 조회 (초기화 데이터중 첫번쨰)
        Book updateBook = bookRepository.findAll().get(0);

        // 수정할 도서 요청 DTO 생성
        BookUpdateRequestDTO updateDto = BookUpdateRequestDTO.builder()
                .title("너에게 해주지 못한 말들 - 제목 수정")
                .author("권태영 - 지은이 수정")
                .status(BookStatus.DAMAGED)
                .categories(List.of("IT", "과학"))
                .build();

        // when : API 호출 (도서 수정)
        mockMvc.perform(patch("/api/book/{bookSn}", updateBook.getBookSn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andDo(print());

        // then : 수정된 데이터 검증
        // 수정된 도서 검증
        Book updatedBook = bookRepository.findAll().get(0);
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getTitle()).isEqualTo(updateDto.getTitle());
        assertThat(updatedBook.getAuthor()).isEqualTo(updateDto.getAuthor());
        assertThat(updatedBook.getStatus()).isEqualTo(updateDto.getStatus());
        assertThat(updatedBook.getCategories().size()).isEqualTo(updateDto.getCategories().size());

        // 수정된 카테고리 검증
        Category updatedCategory;
        for(String categoryName : updateDto.getCategories()) {
            updatedCategory = categoryRepository.findByCategoryName(categoryName).orElseThrow();
            assertThat(updatedCategory).isNotNull();
            assertThat(updatedCategory.getCategoryName()).isEqualTo(categoryName);
        }
    }

    @Test
    @DisplayName("도서 수정 (상태 변경) API 테스트")
    void updateBookStatusTest() throws Exception {

        // given
        // 수정할 entity 조회 (초기화 데이터중 첫번쨰)
        Book updateBook = bookRepository.findAll().get(0);

        // 수정할 도서 요청 DTO 생성
        BookUpdateRequestDTO updateDto = BookUpdateRequestDTO.builder()
                .status(BookStatus.LOST)
                .build();

        // when : API 호출 (도서 수정)
        mockMvc.perform(patch("/api/book/{bookSn}", updateBook.getBookSn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andDo(print());

        // then : 수정된 데이터 검증
        // 수정된 도서 상태 검증
        Book updatedBook = bookRepository.findAll().get(0);
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getStatus()).isEqualTo(updateDto.getStatus());
    }

    @Test
    @DisplayName("도서 수정 (카테고리 수정) API 테스트")
    void updateBookCategoryTest() throws Exception {

        // given
        // 수정할 entity 조회 (초기화 데이터중 첫번쨰)
        Book updateBook = bookRepository.findAll().get(0);

        // 수정할 도서 요청 DTO 생성
        BookUpdateRequestDTO updateDto = BookUpdateRequestDTO.builder()
                .categories(List.of("IT", "과학"))
                .build();

        // when : API 호출 (도서 수정)
        mockMvc.perform(patch("/api/book/{bookSn}", updateBook.getBookSn())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andDo(print());

        // then : 수정된 데이터 검증
        // 수정된 도서 검증
        Book updatedBook = bookRepository.findAll().get(0);
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getCategories().size()).isEqualTo(updateDto.getCategories().size());

        // 수정된 카테고리 검증
        Category updatedCategory;
        for(String categoryName : updateDto.getCategories()) {
            updatedCategory = categoryRepository.findByCategoryName(categoryName).orElseThrow();
            assertThat(updatedCategory).isNotNull();
            assertThat(updatedCategory.getCategoryName()).isEqualTo(categoryName);
        }
    }

    @Test
    @DisplayName("도서 검색 (전체 목록 조회) API 테스트")
    void getBookListTest() throws Exception{

        // when & then : API 호출 및 검증 (도서 전체 목록 조회)
        mockMvc.perform(get("/api/book")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(4))) // 초기화 데이터 검증
                .andExpect(jsonPath("$.data[*].title", containsInAnyOrder("너에게 해주지 못한 말들", "단순하게 배부르게", "게으른 사랑", "트랜드 코리아 2322"))) // 정렬 순서 상관없이 초기 데이터 제목 목록 검증
                .andDo(print());
    }

    @Test
    @DisplayName("도서 검색 (카테고리 별로 검색) API 테스트")
    void getBookCategoryListTest() throws Exception{

        // when & then : API 호출 및 검증 (도서 카테고리별 목록 조회)
        mockMvc.perform(get("/api/book")
                        .param("categoryName", "경제경영")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2))) // 초기화 데이터 검증
                .andExpect(jsonPath("$.data[*].title", containsInAnyOrder("너에게 해주지 못한 말들", "트랜드 코리아 2322"))) // 정렬 순서 상관없이 초기 데이터 제목 목록 검증
                .andDo(print());
    }

    @Test
    @DisplayName("도서 검색 (지은이와 제목으로 검색) API 테스트")
    void getBookTitleAuthorListTest() throws Exception{

        // when & then : API 호출 및 검증 (도서 제목, 지은이로 목록 조회)
        mockMvc.perform(get("/api/book")
                        .param("title", "게으른 사랑")
                        .param("author", "권태영")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1))) // 초기화 데이터로 검증
                .andExpect(jsonPath("$.data[0].title").value("게으른 사랑"))
                .andExpect(jsonPath("$.data[0].author").value("권태영"))
                .andDo(print());
    }
}
