package com.fiap.GastroHub.shared.infra.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AesCryptoImp implements Crypto {
    private static String key = "Bar12345Bar12345";

    private Key aesKey;
    private Cipher cipher;

    public AesCryptoImp(){
        this.aesKey = new SecretKeySpec(key.getBytes(), "AES");
        try {
            this.cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encrypt(String plainText) {
        byte[] encrypted;

        try {
            this.cipher.init(Cipher.ENCRYPT_MODE, this.aesKey);
            encrypted = cipher.doFinal(plainText.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new String(encrypted);
    }

    @Override
    public String decrypt(String cipherText) {
        try {
            this.cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(cipherText.getBytes()));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}
