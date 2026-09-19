package com.events.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;

@Component
public class AuthorVerificationClientImpl implements AuthorVerificationClient {

    private static final Logger log = LoggerFactory.getLogger(AuthorVerificationClientImpl.class);

    private final AuthorInfoServiceGrpc.AuthorInfoServiceBlockingStub stub;

    public AuthorVerificationClientImpl(GrpcChannelFactory channels) {
        this.stub = AuthorInfoServiceGrpc.newBlockingStub(channels.createChannel("mediatheque-service"));
    }

    @Override
    public boolean authorExists(Long authorId) {
        try {
            stub.getAuthor(AuthorRequest.newBuilder().setAuthorId(String.valueOf(authorId)).build());
            return true;
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                log.debug("gRPC mediatheque-service getAuthor authorId={} -> NOT_FOUND", authorId);
                return false;
            }
            throw e;
        }
    }
}
