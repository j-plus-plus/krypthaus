package service;

import exception.EndOfKeyException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.EncryptionUtil;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;


@Data
@RequiredArgsConstructor
@Slf4j
public class MessagingClient {
    private final String login;
    private final String password;
    private final Encrypter crypter = new Encrypter(this);

    private Charset encoding = StandardCharsets.UTF_8;
    private String encryptionFilePath;
    private String nickname = "Anon";
    private boolean encryptionEnabled = false;


    public void send(String text, String recipient) {
        log.info("Sending message...");
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String messageText = dateFormat.format(LocalDateTime.now()) + " " + getNickname() + ": " + text;

            // Print message to OUTBOX
            System.out.println(messageText);

            if (isEncryptionEnabled()) {
                messageText = crypter.encrypt(messageText);
            }
            // Encode message to base64
            String b64MessageText = EncryptionUtil.parseToBase64(messageText, encoding);

            // Send message
            //TODO: send: variable b64MessageText
            throw new UnsupportedOperationException("Sending yet not implemented!");


        } catch (EndOfKeyException e) {
            log.error("Encountered end of encryption key. To continue encrypted communication specify new key.");
        }
    }

    public void recieve() {
        //TODO: get messages from somewhere somehow
        String[] newMessages = {};
        for (String message : newMessages) {
            try {
                String b64CorrMsgStr = message.substring(0, message.length() - 2);
                String corrMsgStr = EncryptionUtil.parseFromBase64(b64CorrMsgStr, encoding);

                System.out.println(isEncryptionEnabled() ? crypter.decrypt(corrMsgStr) : corrMsgStr);
                log.info("Message received.");
            } catch (EndOfKeyException e) {
                log.error("Please specify a new encryption key!");
                log.error("Encountered end of encryption key. To continue encrypted communication specify new key.");
            }
        }
    }

    private void setEncryptionKey(String path) throws IOException {
        String key = EncryptionUtil.readFile(path, StandardCharsets.UTF_8);
        // Compensate for used characters at the start of the key
        crypter.setKeyCaret(EncryptionUtil.countOccurrencesFromStart(key, "#"));
        crypter.setKey(key);
        encryptionFilePath = path;
        log.info("Encryption key set.");
    }
}
