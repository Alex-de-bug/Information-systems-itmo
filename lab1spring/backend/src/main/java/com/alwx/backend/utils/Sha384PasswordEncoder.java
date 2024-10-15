package com.alwx.backend.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Реализация PasswordEncoder, которая использует алгоритм SHA-384 для хеширования паролей.
 */
public class Sha384PasswordEncoder implements PasswordEncoder {


    /**
     * Кодирует пароль с использованием алгоритма SHA-384.
     *
     * @param rawPassword исходный пароль
     * @return хешированный пароль
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return hashPassword(rawPassword.toString());
    }


    /**
     * Сравнивает исходный пароль с уже закодированным паролем.
     *
     * @param rawPassword исходный пароль
     * @param encodedPassword хешированный пароль
     * @return true, если пароли совпадают, иначе false
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String hashedRawPassword = hashPassword(rawPassword.toString());
        return hashedRawPassword.equals(encodedPassword);
    }


    /**
     * Хеширует пароль с использованием алгоритма SHA-384.
     *
     * @param password пароль для хеширования
     * @return хешированное значение пароля
     * @throws RuntimeException если не удается получить алгоритм SHA-384
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-384");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
