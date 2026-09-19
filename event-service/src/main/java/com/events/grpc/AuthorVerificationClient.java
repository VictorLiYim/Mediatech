package com.events.grpc;

/** Vérifie l'existence d'un auteur auprès de mediatheque-service, via gRPC. */
public interface AuthorVerificationClient {

    boolean authorExists(Long authorId);
}
