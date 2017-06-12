package util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Slf4j
public class EncryptionUtil {

    public static String parseToBase64(String text, Charset encoding) {
        byte[] bytes = text.getBytes(encoding);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String parseFromBase64(String base64String, Charset encoding) {
        Base64.Decoder decoder = Base64.getDecoder();
        return new String(decoder.decode(base64String), encoding);
    }

    public static synchronized void overwriteFileContent(String filePath, int startPos, int length) {
        overwriteFileContent(filePath, startPos, length, "#");
    }

    public static synchronized void overwriteFileContent(String filePath, int startPos, int length, String overwriter) {
        try (RandomAccessFile file = new RandomAccessFile(filePath, "rwd")) {
            file.seek(startPos);
            String placeholder = new String(new char[length]).replace("\0", overwriter);
            file.write(placeholder.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int countOccurrencesFromStart(String string, String pattern) {
        int count = 0;

        for (int i = 0; i < string.length(); i += pattern.length()) {
            if (string.substring(i, i + pattern.length()).equals(pattern)) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public static String readFile(String path, Charset encoding) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), encoding);
    }
}
