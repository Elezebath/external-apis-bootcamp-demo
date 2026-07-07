package com.accenture.externalapis.demo.client;

import com.accenture.externalapis.demo.config.ExternalServiceProperties;
import com.accenture.externalapis.demo.dto.BookApiResponse;
import com.accenture.externalapis.demo.dto.BookDto;
import com.accenture.externalapis.demo.mapper.BookMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BookWebClientImpl implements BookWebClient {

    private final WebClient webClient;
    private static final String CLIENT_RESPONSE_EXCEPTION_MSG = "External service returned error: ";
    private static final String CLIENT_REQUEST_EXCEPTION_MSG = "External service is unreachable: ";
        private final BookMapper bookMapper;

    public BookWebClientImpl(WebClient.Builder builder, ExternalServiceProperties properties) {
        // Build the WebClient using builder.baseUrl(properties.baseUrl()).build()
        // and assign it to this.webClient
        this.webClient = builder.baseUrl(properties.baseUrl()).build();
        this.bookMapper = new BookMapper();

        // Optional/bonus: this service doesn't require auth, but in a real API you would
        // often also add builder.defaultHeader("Authorization", "Bearer " + token) here.
    }

    @Override
    public Mono<BookDto> getBookAsync(Long id) {
        return webClient.get()
                .uri("/books/{id}", id)
                .retrieve()
                .bodyToMono(BookApiResponse.class)
                .map(bookMapper::toDto)
                .onErrorResume(WebClientResponseException.class, ex -> Mono.error(new ClientException(CLIENT_RESPONSE_EXCEPTION_MSG + ex.getMessage(), ex)))
                .onErrorResume(WebClientRequestException.class, ex -> Mono.error(new ClientException(CLIENT_REQUEST_EXCEPTION_MSG + ex.getMessage(), ex)));
    }



    @Override
    public Flux<BookDto> getAllBooksAsync() {
        return webClient.get()
                .uri("/books")
                .retrieve()
                .bodyToFlux(BookApiResponse.class)
                .map(bookMapper::toDto)
                .onErrorResume(WebClientResponseException.class, ex -> Mono.error(new ClientException(CLIENT_RESPONSE_EXCEPTION_MSG + ex.getMessage(), ex)))
                .onErrorResume(WebClientRequestException.class, ex -> Mono.error(new ClientException(CLIENT_REQUEST_EXCEPTION_MSG + ex.getMessage(), ex)));
    }

    @Override
    public Mono<List<BookDto>> getBooksInParallel(Long id1, Long id2) {
        // Implementation for fetching books in parallel
        return Mono.zip(
                        getBookAsync(id1),
                        getBookAsync(id2)
                ).map(tuple -> List.of(tuple.getT1(), tuple.getT2()))
                .onErrorResume(WebClientResponseException.class, ex -> Mono.error(new ClientException(CLIENT_RESPONSE_EXCEPTION_MSG + ex.getMessage(), ex)))
                .onErrorResume(WebClientRequestException.class, ex -> Mono.error(new ClientException(CLIENT_REQUEST_EXCEPTION_MSG + ex.getMessage(), ex)));
    }


}
