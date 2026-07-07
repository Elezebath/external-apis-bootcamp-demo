# External APIs Bootcamp Demo - RestClient & WebClient

A Spring Boot application demonstrating integration with an external Book API using both **blocking RestClient** and **non-blocking WebClient**.

The application fetches book data from an external service and exposes REST endpoints for synchronous and reactive API communication.

---

## Technologies

- Java 21
- Spring Boot
- RestClient
- WebClient
- Project Reactor (Mono / Flux)
- Maven

---

## Features Implemented

### RestClient (Blocking)

Implemented `BookRestClientImpl` using `RestClient.Builder`.

Features:
- Fetch a single book by ID.
- Fetch all books.
- Map external API responses (`BookApiResponse`) into application DTOs (`BookDto`).
- Handle external API errors and wrap them into custom `ClientException`.
- External service URL loaded from configuration.

---

### WebClient (Non-blocking)

Implemented `BookWebClientImpl` using `WebClient.Builder`.

Features:
- Fetch a single book asynchronously using `Mono`.
- Fetch all books asynchronously using `Flux`.
- Fetch two books in parallel using `Mono.zip()`.
- Handle response and connection errors using custom `ClientException`.
- Reactive implementation without using `.block()`.

---
