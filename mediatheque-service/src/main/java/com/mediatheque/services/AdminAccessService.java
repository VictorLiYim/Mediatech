package com.mediatheque.services;

public interface AdminAccessService {
    void requireAdmin(Long userId);
}
