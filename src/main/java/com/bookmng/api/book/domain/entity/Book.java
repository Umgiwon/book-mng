package com.bookmng.api.book.domain.entity;

import com.bookmng.global.domain.entity.BaseEntity;
import com.bookmng.global.enums.book.BookStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "TB_BOOK")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOOK_SN", updatable = false, nullable = false)
    @Comment("도서 순번")
    private Long bookSn;

    @Column(name = "TITLE", length = 100, nullable = false)
    @Comment("제목")
    private String title;

    @Column(name = "AUTHOR", length = 20, nullable = false)
    @Comment("지은이")
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    @Comment("상태")
    @Builder.Default
    private BookStatus status = BookStatus.AVAILABLE;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("카테고리 목록")
    @Builder.Default
    private List<BookCategory> categories = new ArrayList<>();

    // 도서에 카테고리 추가
    public void addCategory(Category category) {
        BookCategory bookCategory = new BookCategory(this, category);
        categories.add(bookCategory);
    }
}
