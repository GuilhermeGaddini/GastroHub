package com.fiap.GastroHub.shared.infra.crypto;

public interface Crypto {
    String encrypt(String plainText);
    String decrypt(String cipherText);
}
