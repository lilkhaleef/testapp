package com.example.testapp.utilities;


import android.widget.EditText;

import java.security.SecureRandom;

public class AES {



    // current round index
    private int actual;

    // number of chars (32 bit)
    private static int Nb = 4; // AES block size in 32-bit words
    private int Nk = 8; // Key length in 32-bit words

    // number of rounds for current AES
    private int Nr = 14;

    // State matrix
    private byte[][] state;

    // key stuff
     byte[][] w; //Expanded key

     //User selected AES 256 bits key

    // Initialization vector for CBC
    private static byte[] iv;


    // necessary matrix for AES (sBox + inverted one & rCon)
    private static int[] sBox = new int[]{
            //0     1    2      3     4    5     6     7      8    9     A      B    C     D     E     F
            0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
            0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
            0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
            0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
            0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
            0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
            0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
            0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
            0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
            0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
            0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
            0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
            0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
            0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
            0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
            0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16};

    private static int[] rsBox = new int[]{
            0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb,
            0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb,
            0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
            0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25,
            0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92,
            0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84,
            0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
            0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b,
            0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73,
            0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e,
            0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b,
            0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
            0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f,
            0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef,
            0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61,
            0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d};

    private static int[] rCon = new int[]{
            0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a,
            0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39,
            0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a,
            0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8,
            0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef,
            0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc,
            0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80, 0x1b,
            0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3,
            0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94,
            0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04, 0x08, 0x10, 0x20,
            0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63, 0xc6, 0x97, 0x35,
            0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd, 0x61, 0xc2, 0x9f,
            0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d, 0x01, 0x02, 0x04,
            0x08, 0x10, 0x20, 0x40, 0x80, 0x1b, 0x36, 0x6c, 0xd8, 0xab, 0x4d, 0x9a, 0x2f, 0x5e, 0xbc, 0x63,
            0xc6, 0x97, 0x35, 0x6a, 0xd4, 0xb3, 0x7d, 0xfa, 0xef, 0xc5, 0x91, 0x39, 0x72, 0xe4, 0xd3, 0xbd,
            0x61, 0xc2, 0x9f, 0x25, 0x4a, 0x94, 0x33, 0x66, 0xcc, 0x83, 0x1d, 0x3a, 0x74, 0xe8, 0xcb, 0x8d};



    public AES(byte[] encryptkey) {
        this(encryptkey, null);
    }

    public AES(byte[] encryptkey, byte[] iv) {
        if (encryptkey.length != 32) {
            throw new IllegalArgumentException("Only 256-bit keys are supported");
        }
        this.w = new byte[Nb * (Nr + 1)][4];
        expandKey(encryptkey);
        if (iv == null) {
            this.iv = generateIV();
        } else {
            this.iv = iv;
        }
    }

    private byte[] generateIV() {
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;


    }

    // Key expansion

    private byte[][] expandKey(byte[] encryptkey) {
        byte[][] w = new byte[Nb * (Nr + 1)][4];
        byte[][] temp = new byte[4][4];

        int i = 0;
        while (i < Nk) {
            for (int j = 0; j < 4; j++) {
                w[i][j] = encryptkey[4 * i + j];
            }
            i++;
        }
        i = Nk;

        while (i < Nb * (Nr + 1)) {
            for (int j = 0; j < 4; j++) {
                temp[j][0] = w[i - 1][j];
            }


            if (i % Nk == 0) {
                temp = subWord(rotWord(temp));
                for (int j = 0; j < 4; j++) {
                    //int rConValue = rCon[i/Nk][j];
                    int rConValue = rCon[i/Nk];
                    byte rConByte = (byte) rConValue;
                    temp[j][0] = (byte) (temp[j][0] ^ rConByte);
                }

            } else if (Nk > 6 && i % Nk == 4) {
                temp = subWord(temp);
            }

            for (int j = 0; j < 4; j++) {
                w[i][j] = (byte) (w[i - Nk][j] ^ temp[j][0]);
            }
            i++;
        }

        return w;
    }



    // the addition of the key per round
    private void addRoundKey(int round) {
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < Nb; c++) {
                state[r][c] = (byte) (state[r][c] ^ w[round * Nb + c][r]);
            }
        }
    }


    // Algorithm's general methods


    private void subBytes() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                state[i][j] = (byte) sBox[(state[i][j] & 0x000000FF)];
            }
        }
    }


    private void invSubBytes() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Nb; j++) {
                state[i][j] = (byte) rsBox[(state[i][j] & 0x000000FF)];
            }
        }
    }


    private void shiftRows() {
        byte temp;

        // Shift second row
        temp = (byte) state[1][0];
        for (int i = 0; i < 3; i++) {
            state[1][i] = state[1][i + 1];
        }
        state[1][3] = temp;

        // Shift third row
        byte temp1 = state[2][0];
        byte temp2 = state[2][1];
        state[2][0] = state[2][2];
        state[2][1] = state[2][3];
        state[2][2] = temp1;
        state[2][3] = temp2;

        // Shift fourth row
        temp = state[3][3];
        for (int i = 3; i > 0; i--) {
            state[3][i] = state[3][i - 1];
        }
        state[3][0] = temp;
    }

    private void invShiftRows() {
        byte temp;

        // Inverse shift second row
        temp = state[1][3];
        for (int i = 3; i > 0; i--) {
            state[1][i] = state[1][i - 1];
        }
        state[1][0] = temp;

        // Inverse shift third row
        byte temp1 = state[2][0];
        byte temp2 = state[2][1];
        state[2][0] = state[2][2];
        state[2][1] = state[2][3];
        state[2][2] = temp1;
        state[2][3] = temp2;

        // Inverse shift fourth row
        temp = state[3][0];
        for (int i = 0; i < 3; i++) {
            state[3][i] = state[3][i + 1];
        }
        state[3][3] = temp;
    }


    private void mixColumns() {
        byte[] tmp = new byte[4];
        for (int i = 0; i < 4; i++) {
            tmp[0] = (byte) (gMul((byte) 0x02, state[0][i]) ^ gMul((byte) 0x03, state[1][i]) ^ state[2][i] ^ state[3][i]);
            tmp[1] = (byte) (state[0][i] ^ gMul((byte) 0x02, state[1][i]) ^ gMul((byte) 0x03, state[2][i]) ^ state[3][i]);
            tmp[2] = (byte) (state[0][i] ^ state[1][i] ^ gMul((byte) 0x02, state[2][i]) ^ gMul((byte) 0x03, state[3][i]));
            tmp[3] = (byte) (gMul((byte) 0x03, state[0][i]) ^ state[1][i] ^ state[2][i] ^ gMul((byte) 0x02, state[3][i]));
            for (int j = 0; j < 4; j++) {
                state[j][i] = tmp[j];
            }
        }
    }


    private byte gMul(byte a, byte b) {
        // Galois Field multiplication for mixColumns
        byte p = 0;
        byte hiBitSet;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) {
                p ^= a;
            }
            hiBitSet = (byte) (a & 0x80);
            a <<= 1;
            if (hiBitSet != 0) {
                a ^= 0x1b;
            }
            b >>= 1;
        }
        return p;
    }


    private void invMixColumns() {
        byte[] tmp = new byte[4];
        for (int i = 0; i < 4; i++) {
            tmp[0] = (byte) (gMul((byte) 0x0e, state[0][i]) ^ gMul((byte) 0x0b, state[1][i]) ^ gMul((byte) 0x0d, state[2][i]) ^ gMul((byte) 0x09, state[3][i]));
            tmp[1] = (byte) (gMul((byte) 0x09, state[0][i]) ^ gMul((byte) 0x0e, state[1][i]) ^ gMul((byte) 0x0b, state[2][i]) ^ gMul((byte) 0x0d, state[3][i]));
            tmp[2] = (byte) (gMul((byte) 0x0d, state[0][i]) ^ gMul((byte) 0x09, state[1][i]) ^ gMul((byte) 0x0e, state[2][i]) ^ gMul((byte) 0x0b, state[3][i]));
            tmp[3] = (byte) (gMul((byte) 0x0b, state[0][i]) ^ gMul((byte) 0x0d, state[1][i]) ^ gMul((byte) 0x09, state[2][i]) ^ gMul((byte) 0x0e, state[3][i]));
            for (int j = 0; j < 4; j++) {
                state[j][i] = tmp[j];
            }
        }
    }


    private byte[][] rotWord(byte[][] word) {
        byte[] tmp = new byte[4];
        for (int i = 0; i < 4; i++) {
            tmp[i] = word[(i + 1) % 4][0];
        }
        byte[][] result = new byte[4][1];
        for (int i = 0; i < 4; i++) {
            result[i][0] = tmp[i];
        }
        return result;
    }

    private byte[][] subWord(byte[][] word) {
        for (int i = 0; i < 4; i++) {
            word[i][0] = (byte) sBox[(word[i][0] & 0x000000FF)];
        }
        return word;
    }


    private byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    // Cipher method

    private byte[] cipher(byte[] input) {

        // converting input byte to state matrix
        state = new byte[4][Nb];
        for (int i = 0; i < 4 * Nb; i++) {
            state[i / 4][i % 4] = input[i];
        }

        addRoundKey(0);

        for (int round = 1; round < Nr; round++) {
            subBytes();
            shiftRows();
            mixColumns();
            addRoundKey(round);
        }

        subBytes();
        shiftRows();
        addRoundKey(Nr);

        // Convert state matrix to output byte[]
        byte[] output = new byte[4 * Nb];
        for (int i = 0; i < 4 * Nb; i++) {
            output[i] = state[i / 4][i % 4];
        }

        return output;
    }

    //Decipher Method
    private byte[] decipher(byte[] input) {

        // Convert input byte[] to state matrix
        state = new byte[4][Nb];
        for (int i = 0; i < 4 * Nb; i++) {
            state[i / 4][i % 4] = input[i];
        }

        addRoundKey(Nr);

        for (int round = Nr - 1; round > 0; round--) {
            invShiftRows();
            invSubBytes();
            addRoundKey(round);
            invMixColumns();
        }

        invShiftRows();
        invSubBytes();
        addRoundKey(0);


        // Convert state matrix to output byte[]
        byte[] output = new byte[4 * Nb];
        for (int i = 0; i < 4 * Nb; i++) {
            output[i] = state[i / 4][i % 4];
        }

        return output;
    }

    // Public methods


    public byte[] AESencrypt(byte[] input) {
        byte[] encryptedMessage = new byte[input.length];
        byte[] block = new byte[16];

        // Apply CBC mode
        byte[] previousBlock = iv.clone(); // Copy the IV to avoid modification

        for (int i = 0; i < input.length; i += 16) {
            System.arraycopy(input, i, block, 0, 16);
            block = xor(block, previousBlock);
            block = cipher(block);
            System.arraycopy(block, 0, encryptedMessage, i, 16);
            previousBlock = block.clone(); // Copy the block to use as the next previous block
        }
        return encryptedMessage;
    }

    public byte[] AESdecrypt(byte[] input) {
        byte[] decryptedMessage = new byte[input.length];
        byte[] block = new byte[16];

        // Apply CBC mode
        byte[] previousBlock = iv;

        for (int i = 0; i < input.length; i += 16) {
            System.arraycopy(input, i, block, 0, 16);
            byte[] decryptedBlock = decipher(block);
            decryptedBlock = xor(decryptedBlock, previousBlock);
            System.arraycopy(decryptedBlock, 0, decryptedMessage, i, 16);
            previousBlock = block;
        }
        return decryptedMessage;
    }
}
