package com.cdri.bookmng.api.book.service;

import com.cdri.bookmng.api.book.repository.BookRepository;
import com.cdri.bookmng.api.book.repository.BookRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final BookRepositoryCustom bookRepositoryCustom;


}
