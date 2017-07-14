package me.esca.utils.security.cryptography;


import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 * Created by Me on 14/07/2017.
 */

public class Encryption {

    private final static String key = "Not Really a key";
    private final static String salt = "feacbc02a3a697b0";

    public static String encrypt(String textToEncrypt){
        TextEncryptor encryptor = Encryptors.text(key, salt);
        String encryptedText = encryptor.encrypt(textToEncrypt);
        return encryptedText;
    }

    public static String decrypt(String textToDecrypt){
        TextEncryptor decryptor = Encryptors.text(key, salt);
        String decryptedText = decryptor.decrypt(textToDecrypt);
        return decryptedText;
    }
}
