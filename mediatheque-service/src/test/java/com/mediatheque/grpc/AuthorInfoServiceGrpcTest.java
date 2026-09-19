package com.mediatheque.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mediatheque.models.Author;
import com.mediatheque.services.AuthorService;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/** Même principe que BookInfoServiceGrpcTest : vrai client gRPC contre localhost:9090. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AuthorInfoServiceGrpcTest {

    private static ManagedChannel channel;
    private static AuthorInfoServiceGrpc.AuthorInfoServiceBlockingStub stub;

    @Autowired
    private AuthorService authorService;

    @BeforeAll
    static void setUpChannel() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        stub = AuthorInfoServiceGrpc.newBlockingStub(channel);
    }

    @AfterAll
    static void tearDownChannel() {
        channel.shutdownNow();
    }

    @Test
    void getAuthor_existingAuthor_returnsNameAndBio() {
        Author author = authorService.createAuthor("Amara", "Autrice de fantasy");

        AuthorReply reply = stub.getAuthor(AuthorRequest.newBuilder()
                .setAuthorId(String.valueOf(author.getId()))
                .build());

        assertThat(reply.getName()).isEqualTo("Amara");
        assertThat(reply.getBio()).isEqualTo("Autrice de fantasy");
    }

    @Test
    void getAuthor_unknownId_failsWithNotFound() {
        assertThatThrownBy(() -> stub.getAuthor(AuthorRequest.newBuilder().setAuthorId("999999").build()))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }
}
