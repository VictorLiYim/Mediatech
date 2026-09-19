package com.users.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.users.models.User;
import com.users.services.UserService;

import io.grpc.stub.StreamObserver;


@Service
public class UserInfoServiceImpl extends UserInfoServiceGrpc.UserInfoServiceImplBase {

	private static final Logger log = LoggerFactory.getLogger(UserInfoServiceImpl.class);

	private final UserService userService;

	public UserInfoServiceImpl(UserService userService) {
		this.userService = userService;
	}

	@Override
	public void getUser(UserRequest request, StreamObserver<UserReply> responseObserver) {
		log.info("gRPC getUser userId={}", request.getUserId());

		UserReply reply;
		try {
			Long id = Long.parseLong(request.getUserId());
			if (userService.existsById(id)) {
				User user = userService.getById(id);
				reply = UserReply.newBuilder()
						.setExists(true)
						.setUserName(user.getUserName())
						.build();
			} else {
				reply = UserReply.newBuilder().setExists(false).build();
			}
		} catch (NumberFormatException e) {
			log.warn("gRPC getUser: userId non numérique reçu: {}", request.getUserId());
			reply = UserReply.newBuilder().setExists(false).build();
		}

		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
}
