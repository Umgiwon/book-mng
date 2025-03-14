package com.cdri.bookmng.api.book.domain.entity;

import com.cdri.bookmng.global.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;


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
    @Comment("책 순번")
    private Long bookSn;

    @Column(name = "TITLE", length = 50, nullable = false)
    @Comment("제목")
    private String title;

    @Column(name = "AUTHOR", length = 100, nullable = false)
    @Comment("지은이")
    private String author;

    // 상태

    // 카테고리스
}
