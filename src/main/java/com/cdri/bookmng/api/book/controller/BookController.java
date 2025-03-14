package com.cdri.bookmng.api.book.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book API", description = "카테고리 별로 분류되는 도서 시스템 API 설명")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/book/")
public class BookController {


}
