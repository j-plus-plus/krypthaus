package service;

import exception.EndOfKeyException;
import lombok.Setter;
import service.MessagingClient;
import util.EncryptionUtil;

import java.util.HashMap;
import java.util.Map;

@Setter
public class Encrypter {
    private final MessagingClient messagingClient;
    private Map<Character, Integer> charToCode = new HashMap<>();
    private Map<Integer, Character> codeToChar = new HashMap<>();
    private int codeRange;
    private String key;
    private int keyCaret;

    public Encrypter(MessagingClient messagingClient) {
        this(messagingClient, "");
    }

    public Encrypter(MessagingClient messagingClient, String key) {
        this(messagingClient, key, 0);
    }

    public Encrypter(MessagingClient messagingClient, String key, int start) {
        this(messagingClient, key, start, new char[]{
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
                'ü', 'õ', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'ö', 'ä', '+', '<', 'z', 'x', 'c', 'v', 'b',
                'n', 'm', ',', '.', '-', 'Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P', 'Ü', 'Õ', 'A', 'S', 'D',
                'F', 'G', 'H', 'J', 'K', 'L', 'Ö', 'Ä', '*', '>', 'Z', 'X', 'C', 'V', 'B', 'N', 'M', ';', ':', '_',
                '§', '^', '½', '|', '~', '!', '"', '#', '¤', '%', '&', '/', '(', ')', '=', '?', '`', '@', '£', '$',
                '€', '{', '[', ']', '}', '\\', '\n', '\t', ' ', 'ˇ', '\''}
        );
    }

    public Encrypter(MessagingClient messagingClient, String key, int start, char[] acceptedChars) {
        this.messagingClient = messagingClient;
        this.codeRange = acceptedChars.length;
        initCharCodeMaps(acceptedChars);
        this.key = key;
        this.keyCaret = start;
    }

    private void initCharCodeMaps(char[] chars) {
        for (int i = 0; i < chars.length; i++) {
            charToCode.put(chars[i], i);
            codeToChar.put(i, chars[i]);
        }
    }

    public String encrypt(String plaintext) throws EndOfKeyException {
        int keyStartLoc = keyCaret;

        if (plaintext.isEmpty()) {
            return "";
        }

        try {
            if (plaintext.length() > key.length() - keyCaret) {
                throw new EndOfKeyException("Encountered end of key while encrypting.");
            }

            StringBuilder ciphertext = new StringBuilder();

            for (int i = 0; i < plaintext.length(); i++, keyCaret++) {
                int textCharCode = charToCode.get(plaintext.charAt(i));
                int keyCharCode = charToCode.get(key.charAt(keyCaret));

                int encCharCode = (textCharCode + keyCharCode) % codeRange;
                char encChar = codeToChar.get(encCharCode);
                ciphertext.append(encChar);
            }

            return ciphertext.toString();
        } finally {
            EncryptionUtil.overwriteFileContent(
                    messagingClient.getEncryptionFilePath(),
                    keyStartLoc,
                    keyCaret - keyStartLoc
            );
        }
    }

    public String decrypt(String ciphertext) throws EndOfKeyException {
        int keyStartLoc = keyCaret;

        if (ciphertext.isEmpty()) {
            return "";
        }

        try {
            if (ciphertext.length() > key.length() - keyCaret) {
                throw new EndOfKeyException("Encountered end of key while decrypting.");
            }

            StringBuilder plaintext = new StringBuilder();

            for (int i = 0; i < ciphertext.length(); i++, keyCaret++) {
                int encCharCode = charToCode.get(ciphertext.charAt(i));
                int keyCharCode = charToCode.get(key.charAt(keyCaret));
                int textCharCode = encCharCode - keyCharCode;

                if (textCharCode < 0) {
                    textCharCode += codeRange;
                    assert textCharCode >= 0;
                }
                assert textCharCode < codeRange;

                char textChar = codeToChar.get(textCharCode);
                plaintext.append(textChar);
            }

            return plaintext.toString();
        } finally {
            EncryptionUtil.overwriteFileContent(
                    messagingClient.getEncryptionFilePath(),
                    keyStartLoc,
                    keyCaret - keyStartLoc
            );
        }
    }
}