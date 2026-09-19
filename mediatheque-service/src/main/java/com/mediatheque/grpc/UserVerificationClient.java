package com.mediatheque.grpc;

/** Vérifie l'existence d'un utilisateur auprès de user-service, via gRPC. */
public interface UserVerificationClient {

    boolean userExists(Long userId);
}
