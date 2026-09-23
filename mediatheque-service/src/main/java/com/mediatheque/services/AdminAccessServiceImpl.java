package com.mediatheque.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mediatheque.exceptions.AdminRightsRequiredException;
import com.mediatheque.grpc.UserVerificationClient;

@Service
public class AdminAccessServiceImpl implements AdminAccessService {

    private static final Logger log = LoggerFactory.getLogger(AdminAccessServiceImpl.class);

    private final UserVerificationClient userVerificationClient;

    public AdminAccessServiceImpl(UserVerificationClient userVerificationClient) {
        this.userVerificationClient = userVerificationClient;
    }

    @Override
    public void requireAdmin(Long userId) {
        if (!userVerificationClient.isAdmin(userId)) {
            log.warn("Stock management refused for user {}", userId);
            throw new AdminRightsRequiredException(userId);
        }
    }
}
