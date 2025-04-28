package com.bookmng.api.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "TB_BOOK_CATEGORY")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOOK_CATEGORY_SN", updatable = false, nullable = false)
    @Comment("도서별 카테고리 순번")
    private Long bookCategorySn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOK_SN", referencedColumnName = "BOOK_SN", updatable = false, nullable = false)
    @Comment("도서 순번")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_SN", referencedColumnName = "CATEGORY_SN", updatable = false, nullable = false)
    @Comment("카테고리 순번")
    private Category category;

    public BookCategory(Book book, Category category) {
        this.book = book;
        this.category = category;
    }
}
