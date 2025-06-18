package crop;

import org.bytedeco.opencv.global.opencv_imgcodecs;

import org.bytedeco.opencv.opencv_core.*;


import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class BlueHandwritingCrop {

    public static void main(String[] args) {
        String inputPath = "src/main/resources/input/test.jpg";
        String outputPath = "src/main/resources/output/cropped.jpg";

        Mat image = opencv_imgcodecs.imread(inputPath);
        if (image.empty()) {
            System.err.println("❌ Изображение не загружено.");
            return;
        }

        Mat cropped = cropBlueHandwriting(image);
        if (cropped == null) {
            System.out.println("❗ Рукописный текст не найден.");
            return;
        }

        opencv_imgcodecs.imwrite(outputPath, cropped);
        System.out.println("✅ Сохранено: " + outputPath);
    }

    public static Mat cropBlueHandwriting(Mat image) {
        Mat hsv = new Mat();
        cvtColor(image, hsv, COLOR_BGR2HSV);

        Mat mask = createBlueMask(hsv);
        mask = applyMorphology(mask);

        MatVector contours = new MatVector();
        findContours(mask.clone(), contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
        if (contours.size() == 0) return null;

        Rect boundingRect = getMergedBoundingRect(contours);
        Rect extendedRect = extendToBlue(mask, boundingRect);

        return new Mat(image, extendedRect);
    }

    private static Mat createBlueMask(Mat hsv) {
        Mat lower = new Mat(1, 1, CV_8UC3, new Scalar(90, 50, 50, 0));
        Mat upper = new Mat(1, 1, CV_8UC3, new Scalar(140, 255, 255, 0));
        Mat mask = new Mat();
        inRange(hsv, lower, upper, mask);
        return mask;
    }

    private static Mat applyMorphology(Mat mask) {
        Mat kernel = getStructuringElement(MORPH_RECT, new Size(5, 5));
        morphologyEx(mask, mask, MORPH_CLOSE, kernel);
        return mask;
    }

    private static Rect getMergedBoundingRect(MatVector contours) {
        Rect rect = boundingRect(contours.get(0));
        for (int i = 1; i < contours.size(); i++) {
            Rect r = boundingRect(contours.get(i));
            int x1 = Math.min(rect.x(), r.x());
            int y1 = Math.min(rect.y(), r.y());
            int x2 = Math.max(rect.x() + rect.width(), r.x() + r.width());
            int y2 = Math.max(rect.y() + rect.height(), r.y() + r.height());
            rect = new Rect(x1, y1, x2 - x1, y2 - y1);
        }
        return rect;
    }

    private static Rect extendToBlue(Mat mask, Rect rect) {
        int top = rect.y(), bottom = top + rect.height();
        int left = rect.x(), right = left + rect.width();
        boolean changed;

        do {
            changed = false;
            if (top > 0 && countNonZero(mask.row(top - 1)) > 0) { top--; changed = true; }
            if (bottom < mask.rows() - 1 && countNonZero(mask.row(bottom + 1)) > 0) { bottom++; changed = true; }
            if (left > 0 && countNonZero(mask.col(left - 1)) > 0) { left--; changed = true; }
            if (right < mask.cols() - 1 && countNonZero(mask.col(right + 1)) > 0) { right++; changed = true; }
        } while (changed);

        return new Rect(left, top, right - left, bottom - top);
    }
}