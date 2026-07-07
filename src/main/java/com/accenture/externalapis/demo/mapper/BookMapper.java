package com.accenture.externalapis.demo.mapper;

import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;

public class BookMapper {
    public BookDto toDto(BookApiResponse response) {
        // Implementation for mapping BookApiResponse to BookDto
        return new BookDto(
                response.title(),
                response.author(),
                response.genre(),
                response.price()
        );
    }
}
