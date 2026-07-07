package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import com.accenture.externalapis.demo.mapper.BookMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import java.util.Arrays;
import java.util.List;

@Component
public class BookRestClientImpl implements BookRestClient {

    private RestClient restClient;
    private final BookMapper bookMapper;

    public BookRestClientImpl(RestClient.Builder builder, ExternalServiceProperties properties) {
        this.restClient = builder.baseUrl(properties.baseUrl()).build();
        // mapper
        this.bookMapper = new BookMapper();
        //
        // Optional/bonus: this service doesn't require auth, but in a real API you would
        // often also add builder.defaultHeader("Authorization", "Bearer " + token) here.
    }

    @Override
    public BookDto getBook(Long id) {
        try {
            BookApiResponse response = restClient.get()
                    .uri("/books/{id}", id)
                    .retrieve()
                    .body(BookApiResponse.class);
            if (response == null) {
                throw new ClientException("Book response was empty.");
            }
            return bookMapper.toDto(response);
        } catch (ClientException e) {
            throw e;
        } catch (ResourceAccessException e) {
            throw new ClientException("Failed to connect to external service.", e);
        } catch (Exception e) {
            throw new ClientException("Unexpected error from book service.", e);
        }
    }

    @Override
    public List<BookDto> getAllBooks() {
        try {
            BookApiResponse[] response = restClient.get()
                    .uri("/books")
                    .retrieve()
                    .body(BookApiResponse[].class);
            if (response == null) {
                return List.of();
            }
            return Arrays.stream(response)
                    .map(bookMapper::toDto)
                    .toList();

        } catch (ClientException e) {
            throw e;
        } catch (ResourceAccessException e) {
            throw new ClientException("Failed to connect to external service.", e);
        } catch (Exception e) {
            // Sanitize unexpected errors
            throw new ClientException("Unexpected error from book service.", e);
        }
    }

}
