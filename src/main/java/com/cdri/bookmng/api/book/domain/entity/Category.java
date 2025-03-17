package com.cdri.bookmng.api.book.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_CATEGORY")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_SN", updatable = false, nullable = false)
    @Comment("카테고리 순번")
    private Long categorySn;

    @Column(name = "CATEGORY_NAME", length = 50, nullable = false, unique = true)
    @Comment("제목")
    private String categoryName;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BookCategory> books = new ArrayList<>();

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
