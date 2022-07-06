package xyz.ctstudy.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

/**
 * 生成不同颜色的二维码
 */
public class PassPictUtil {

    /**
     * 生成通行码
     * @param size 图片大小
     * @param content 二维码扫出来的内容
     * @param color 二维码颜色
     * @param needCompress 是否压缩
     * @return
     */
    public static BufferedImage createImage(int size, String content,int color , boolean needCompress){
        // 绿紫黄红1234
        int[] colorArray = {0xFF00FF7F,0xffda70d6,0xffffff00,0xffff0000};
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, size, size, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? colorArray[color - 1]
                        : 0xFFFFFFFF);
            }
        }

        return image;

    }

}
