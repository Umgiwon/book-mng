package com.cdri.bookmng.api.book.repository;

import com.cdri.bookmng.api.book.domain.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
