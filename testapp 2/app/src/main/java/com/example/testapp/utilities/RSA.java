package com.example.testapp.utilities;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class RSA {
    public class RSAAESEncryption {


        public static byte[] encryptAESKeyWithRSA(PublicKey rsaPublicKey, SecretKey aesKey) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            return cipher.doFinal(aesKey.getEncoded());
        }

        public static SecretKey decryptAESKeyWithRSA(PrivateKey rsaPrivateKey, byte[] encryptedAESKey) throws Exception {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            byte[] decryptedAESKeyBytes = cipher.doFinal(encryptedAESKey);
            return new SecretKeySpec(decryptedAESKeyBytes, "AES");
        }

        public static void main(String[] args) throws Exception {
            // Generate RSA key pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Key size
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // Generate AES key (replace with Android Keystore)

            byte[] encryptKey = new byte[]{};
            SecretKey aesKey = new SecretKeySpec(encryptKey, "AES");

            // Encrypt AES key with RSA public key
            byte[] encryptedAESKey = encryptAESKeyWithRSA(publicKey, aesKey);

            // Decrypt AES key with RSA private key
            SecretKey decryptedAESKey = decryptAESKeyWithRSA(privateKey, encryptedAESKey);

        }
    }
}
