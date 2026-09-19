package com.mediatheque.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mediatheque.exceptions.BookNotFoundException;
import com.mediatheque.models.Author;
import com.mediatheque.models.Book;
import com.mediatheque.services.BookService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * Serveur gRPC : donne le titre/auteur d'un livre à event-service (dédicaces).
 * Généré à partir de book.proto (BookInfoServiceGrpc.BookInfoServiceImplBase).
 */
@Service
public class BookInfoServiceImpl extends BookInfoServiceGrpc.BookInfoServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BookInfoServiceImpl.class);

    private final BookService bookService;

    public BookInfoServiceImpl(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void getBook(BookRequest request, StreamObserver<BookReply> responseObserver) {
        log.info("gRPC getBook bookId={}", request.getBookId());

        try {
            Long id = Long.parseLong(request.getBookId());
            Book book = bookService.getById(id); // lève BookNotFoundException si absent

            String authorNames = book.getAuthors().stream()
                    .map(Author::getName)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");

            BookReply reply = BookReply.newBuilder()
                    .setTitle(book.getTitle())
                    .setAuthor(authorNames)
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (NumberFormatException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("bookId must be numeric: " + request.getBookId())
                    .asRuntimeException());
        } catch (BookNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }
}
