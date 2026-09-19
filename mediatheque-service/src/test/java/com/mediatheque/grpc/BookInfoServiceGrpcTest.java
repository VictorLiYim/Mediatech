package com.mediatheque.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mediatheque.models.Author;
import com.mediatheque.models.Book;
import com.mediatheque.models.BookType;
import com.mediatheque.services.AuthorService;
import com.mediatheque.services.BookService;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 * Vérifie que le serveur gRPC BookInfoService répond vraiment sur le réseau
 * (comme UserInfoServiceGrpcTest côté user-service) : un vrai client gRPC se connecte à localhost:9090.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class BookInfoServiceGrpcTest {

    private static ManagedChannel channel;
    private static BookInfoServiceGrpc.BookInfoServiceBlockingStub stub;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @BeforeAll
    static void setUpChannel() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        stub = BookInfoServiceGrpc.newBlockingStub(channel);
    }

    @AfterAll
    static void tearDownChannel() {
        channel.shutdownNow();
    }

    @Test
    void getBook_existingBook_returnsTitleAndAuthor() {
        Author author = authorService.createAuthor("Amara", "Autrice de fantasy");
        Book book = bookService.createBook(
                "Cats and Dragons", Set.of(author.getId()), "978-2-1234-5680-0", BookType.ROMAN,
                "Une histoire de chats et de dragons", Set.of(), 3
        );

        BookReply reply = stub.getBook(BookRequest.newBuilder()
                .setBookId(String.valueOf(book.getId()))
                .build());

        assertThat(reply.getTitle()).isEqualTo("Cats and Dragons");
        assertThat(reply.getAuthor()).isEqualTo("Amara");
    }

    @Test
    void getBook_unknownId_failsWithNotFound() {
        assertThatThrownBy(() -> stub.getBook(BookRequest.newBuilder().setBookId("999999").build()))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }
}
