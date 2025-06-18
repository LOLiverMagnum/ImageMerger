package org.example;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import java.io.File;

public class TesseractTest {
    public static void main(String[] args) {
        Tesseract tesseract = new Tesseract();

        // Укажи путь к папке tessdata (не к .exe, а именно к папке, где лежат .traineddata файлы)
        tesseract.setDatapath("C:\\Program Files\\Tesseract-OCR\\tessdata");

        // Укажи язык (eng для английского, rus для русского, можно "rus+eng")
        tesseract.setLanguage("rus");

        try {
            File imageFile = new File("src/main/resources/test.jpg"); // путь к изображению
            String result = tesseract.doOCR(imageFile);
            System.out.println("Результат распознавания:\n" + result);
        } catch (TesseractException e) {
            e.printStackTrace();
        }
    }
}
