package com.users.grpc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.users.services.UserService;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * Vérifie que le serveur gRPC UserInfoService répond vraiment sur le réseau
 * (pas juste que le code compile) : un vrai client gRPC se connecte à localhost:9091.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserInfoServiceGrpcTest {

	private static ManagedChannel channel;
	private static UserInfoServiceGrpc.UserInfoServiceBlockingStub stub;

	@Autowired
	private UserService userService;

	@BeforeAll
	static void setUpChannel() {
		channel = ManagedChannelBuilder.forAddress("localhost", 9091)
				.usePlaintext()
				.build();
		stub = UserInfoServiceGrpc.newBlockingStub(channel);
	}

	@AfterAll
	static void tearDownChannel() {
		channel.shutdownNow();
	}

	@Test
	void getUser_existingUser_returnsExistsTrue() {
		var created = userService.createUser("grpc_test_user", "grpc_test@example.com", "password123");

		UserReply reply = stub.getUser(UserRequest.newBuilder()
				.setUserId(String.valueOf(created.getId()))
				.build());

		assertThat(reply.getExists()).isTrue();
		assertThat(reply.getUserName()).isEqualTo("grpc_test_user");
	}

	@Test
	void getUser_unknownId_returnsExistsFalse() {
		UserReply reply = stub.getUser(UserRequest.newBuilder()
				.setUserId("999999")
				.build());

		assertThat(reply.getExists()).isFalse();
	}
}
