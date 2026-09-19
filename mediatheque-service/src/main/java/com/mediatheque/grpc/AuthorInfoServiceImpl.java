package com.mediatheque.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mediatheque.exceptions.AuthorNotFoundException;
import com.mediatheque.models.Author;
import com.mediatheque.services.AuthorService;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

/**
 * Serveur gRPC : donne le nom/bio d'un auteur à event-service (pour vérifier un authorId
 * avant de créer une dédicace). Généré à partir de author.proto.
 */
@Service
public class AuthorInfoServiceImpl extends AuthorInfoServiceGrpc.AuthorInfoServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(AuthorInfoServiceImpl.class);

    private final AuthorService authorService;

    public AuthorInfoServiceImpl(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Override
    public void getAuthor(AuthorRequest request, StreamObserver<AuthorReply> responseObserver) {
        log.info("gRPC getAuthor authorId={}", request.getAuthorId());

        try {
            Long id = Long.parseLong(request.getAuthorId());
            Author author = authorService.getById(id); // lève AuthorNotFoundException si absent

            AuthorReply reply = AuthorReply.newBuilder()
                    .setName(author.getName())
                    .setBio(author.getBio() == null ? "" : author.getBio())
                    .build();

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        } catch (NumberFormatException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("authorId must be numeric: " + request.getAuthorId())
                    .asRuntimeException());
        } catch (AuthorNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }
}
