package com.alwx.backend.utils;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

@Component
public class LockProvider {
    private final ReentrantLock lock = new ReentrantLock();
    
    public ReentrantLock getReentranLock() {
        return lock;
    }
}
