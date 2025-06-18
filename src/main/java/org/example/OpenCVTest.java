package org.example;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

public class OpenCVTest {
    public static void main(String[] args) {
        try {
            System.out.println("✅ OpenCV — старт...");

            // Загрузка изображения
            Mat image = opencv_imgcodecs.imread("input/test.jpg");
            if (image.empty()) {
                System.out.println("⚠️ Изображение не загружено. Проверь путь.");
            } else {
                System.out.println("📐 Размер изображения: " + image.size().width() + " x " + image.size().height());
            }

        } catch (UnsatisfiedLinkError e) {
            System.err.println("❌ Ошибка загрузки OpenCV: " + e.getMessage());
        }
    }
}
