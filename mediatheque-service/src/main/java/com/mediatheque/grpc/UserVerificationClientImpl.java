package com.mediatheque.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

@Component
public class UserVerificationClientImpl implements UserVerificationClient {

    private static final Logger log = LoggerFactory.getLogger(UserVerificationClientImpl.class);

    private final UserInfoServiceGrpc.UserInfoServiceBlockingStub stub;

    public UserVerificationClientImpl(GrpcChannelFactory channels) {
        this.stub = UserInfoServiceGrpc.newBlockingStub(channels.createChannel("user-service"));
    }

    @Override
    public boolean userExists(Long userId) {
        UserReply reply = stub.getUser(UserRequest.newBuilder()
                .setUserId(String.valueOf(userId))
                .build());
        log.debug("gRPC user-service getUser userId={} exists={}", userId, reply.getExists());
        return reply.getExists();
    }
}
