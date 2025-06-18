package org.example;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_imgproc.*;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class BlueHandwritingCrop {
    public static void main(String[] args) {
        String inputPath = "input/test.jpg";
        String outputPath = "output/cropped.jpg";

        // Загружаем изображение
        Mat image = opencv_imgcodecs.imread(inputPath);
        if (image.empty()) {
            System.err.println("❌ Изображение не загружено.");
            return;
        }

        // Переводим в HSV
        Mat hsv = new Mat();
        cvtColor(image, hsv, COLOR_BGR2HSV);

        // Фильтр по синему цвету
        Mat lower = new Mat(1, 1, CV_8UC3, new Scalar(90, 50, 50, 0));
        Mat upper = new Mat(1, 1, CV_8UC3, new Scalar(140, 255, 255, 0));
        Mat mask = new Mat();
        inRange(hsv, lower, upper, mask);

        // Морфология (объединяем разрозненные элементы)
        Mat kernel = getStructuringElement(MORPH_RECT, new Size(5, 5));
        morphologyEx(mask, mask, MORPH_CLOSE, kernel);

        // Поиск контуров
        MatVector contours = new MatVector();
        findContours(mask.clone(), contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);

        if (contours.size() == 0) {
            System.out.println("❗ Рукописный текст не найден.");
            return;
        }

        // Объединяем все прямоугольники контуров
        Rect boundingRect = boundingRect(contours.get(0));
        for (int i = 1; i < contours.size(); i++) {
            Rect r = boundingRect(contours.get(i));
            int x1 = Math.min(boundingRect.x(), r.x());
            int y1 = Math.min(boundingRect.y(), r.y());
            int x2 = Math.max(boundingRect.x() + boundingRect.width(), r.x() + r.width());
            int y2 = Math.max(boundingRect.y() + boundingRect.height(), r.y() + r.height());
            boundingRect = new Rect(x1, y1, x2 - x1, y2 - y1);
        }


        int top = boundingRect.y();
        int bottom = top + boundingRect.height();
        int left = boundingRect.x();
        int right = left + boundingRect.width();

        // Расширение, пока встречаются синие пиксели
        boolean changed;
        do {
            changed = false;

            if (top > 0 && countNonZero(mask.row(top - 1)) > 0) {
                top--;
                changed = true;
            }

            if (bottom < mask.rows() - 1 && countNonZero(mask.row(bottom + 1)) > 0) {
                bottom++;
                changed = true;
            }

            if (left > 0 && countNonZero(mask.col(left - 1)) > 0) {
                left--;
                changed = true;
            }

            if (right < mask.cols() - 1 && countNonZero(mask.col(right + 1)) > 0) {
                right++;
                changed = true;
            }

        } while (changed);

        // Кадрирование и сохранение
        Rect resultRect = new Rect(left, top, right - left, bottom - top);
        Mat cropped = new Mat(image, resultRect);
        opencv_imgcodecs.imwrite(outputPath, cropped);

        System.out.println("✅ Сохранено: " + outputPath);
    }
}
