package com.radilbe.rassword;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES"; // or any other algorithm
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding"; // example transformation

    public static byte[] decrypt(byte[] inputData, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(inputData);
    }

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // AES key size
        return keyGen.generateKey();
    }

    public static String encodeKey(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static void encryptFile(String inputData, String outputFilePath, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (ByteArrayInputStream inputDataStream = new ByteArrayInputStream(inputData.getBytes());
             FileOutputStream outputFile = new FileOutputStream(outputFilePath);
             CipherOutputStream cipherOutput = new CipherOutputStream(outputFile, cipher)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputDataStream.read(buffer)) != -1) {
                cipherOutput.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void decryptFile(String inputFilePath, String outputFilePath, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        try (FileInputStream inputFile = new FileInputStream(inputFilePath);
             CipherInputStream cipherInput = new CipherInputStream(inputFile, cipher);
             FileOutputStream outputFile = new FileOutputStream(outputFilePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = cipherInput.read(buffer)) != -1) {
                outputFile.write(buffer, 0, bytesRead);
            }
        }
    }
}
