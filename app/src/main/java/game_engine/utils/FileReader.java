package game_engine.utils;






import java.io.*;

public class FileReader {
    private static FileReader inst = null;
    private FileReader() {

    }

    private static FileReader instance() {
        if (inst == null) {
            inst = new FileReader();
        }
        return inst;
    }

    public static InputStream getFileFromResourceAsStream(String fileName) {
        // The class loader that loaded the class
        ClassLoader classLoader = instance().getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return inputStream;
        } 
    }

    public static byte[] readBytes(String fileName) {
        InputStream stream = getFileFromResourceAsStream(fileName);
        try {
            return stream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    public static String readString(String fileName) {
        InputStream stream = getFileFromResourceAsStream(fileName);
        try {
            return new String(stream.readAllBytes());
        } catch (IOException e) {
            return null;
        }
    }
}
