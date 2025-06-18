package tests;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;
// Тут тест отрабатывает нормально.
public class OpenCVTest {
    public static void main(String[] args) {
        try {
            System.out.println("✅ OpenCV — старт...");

            // Загрузка изображения
            try (Mat image = opencv_imgcodecs.imread("src/main/resources/input/test.jpg")) {
                if (image.empty()) {
                    System.out.println("⚠️ Изображение не загружено. Проверь путь.");
                } else {
                    System.out.println("📐 Размер изображения: " + image.size().width() + " x " + image.size().height());
                }
            }

        } catch (UnsatisfiedLinkError e) {
            System.err.println("❌ Ошибка загрузки OpenCV: " + e.getMessage());
        }
    }
}
