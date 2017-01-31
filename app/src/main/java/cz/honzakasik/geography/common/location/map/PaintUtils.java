package cz.honzakasik.geography.common.location.map;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

import java.util.Random;

public class PaintUtils {

    private static final Random RANDOM = new Random();

    public static Paint getPaint(int color) {
        Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
        paint.setColor(color);
        return paint;
    }

    public static int getRandomColor() {
        int red = RANDOM.nextInt(256);
        int green = RANDOM.nextInt(256);
        int blue = RANDOM.nextInt(256);
        return createColor(red, green, blue);
    }

    public static int createColor(int red, int green, int blue) {
        return createColor(120, red, green, blue);
    }

    public static int createColor(int opacity, int red, int green, int blue) {
        return AndroidGraphicFactory.INSTANCE.createColor(opacity, red, green, blue);
    }
}
