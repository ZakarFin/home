package fi.zakar.lights.hue;

/**
 * Created by zakar on 01/01/15.
 */
public class HueColorConverter {

    // based on http://stackoverflow.com/questions/22564187/rgb-to-philips-hue-hsb
    // https://github.com/PhilipsHue/PhilipsHueSDK-iOS-OSX/blob/master/ApplicationDesignNotes/RGB%20to%20xy%20Color%20conversion.md
    public static double[] getRGBtoXY(int red, int green, int blue) {

        // transform values from 0-255 to 0-1
        double redd = (getValidRange(red, 0, 255) / 255);
        double greend  = (getValidRange(green, 0, 255) / 255);
        double blued  = (getValidRange(blue, 0, 255) / 255);

        return getNormalizedRGBtoXY(redd, greend, blued);
    }

    private static int getValidRange(int value, int min, int max) {
        if(value < min) return min;
        if(value > max) return max;
        return value;
    }

    private static double[] getNormalizedRGBtoXY(double red, double green, double blue) {

        // For the hue bulb the corners of the triangle are:
        // -Red: 0.675, 0.322
        // -Green: 0.4091, 0.518
        // -Blue: 0.167, 0.04

        // Make red more vivid
        if (red > 0.04045) {
            red = (float) Math.pow(
                    (red + 0.055f) / (1.055f), 2.4f);
        } else {
            red = red / 12.92f;
        }

        // Make green more vivid
        if (green > 0.04045) {
            green = (float) Math.pow((green + 0.055f)
                    / (1.055f), 2.4f);
        } else {
            green = green / 12.92f;
        }

        // Make blue more vivid
        if (blue > 0.04045) {
            blue = (float) Math.pow((blue + 0.055f)
                    / (1.055f), 2.4f);
        } else {
            blue = blue / 12.92f;
        }

        float X = (float) (red * 0.649926 + green * 0.103455 + blue * 0.197109);
        float Y = (float) (red * 0.234327 + green * 0.743075 + blue * 0.022598);
        float Z = (float) (red * 0.0000000 + green * 0.053077 + blue * 1.035763);

        float x = X / (X + Y + Z);
        float y = Y / (X + Y + Z);

        return new double[] {x, y};
    }

    public static int[] getRGBFromXy(float x, float y, float brightness) {
        //Calculate XYZ values Convert using the following formulas:

        float z = 1.0f - x - y;
        // The given brightness value float X = (Y / y) * x;
        float Y = brightness;
        float Z = (Y / y) * z;

        //Convert to RGB using Wide RGB D65 conversion
        float r = x * 3.2406f - Y * 1.5372f - Z * 0.4986f;
        float g = -x * 0.9689f + Y * 1.8758f + Z * 0.0415f;
        float b = x * 0.0557f - Y * 0.2040f + Z * 1.0570f;

        // Apply reverse gamma correction
        if(r <= 0.0031308f) {
            r = 12.92f * r;
        }
        else {
            r = (1.0f + 0.055f) * (float)Math.pow(r, (1.0d / 2.4d)) - 0.055f;
        }

        if(g <= 0.0031308f) {
            g = 12.92f * g;
        }
        else {
            g = (1.0f + 0.055f) * (float)Math.pow(g, (1.0d / 2.4d)) - 0.055f;
        }

        if(b <= 0.0031308f) {
            b = 12.92f * b;
        }
        else {
            b = (1.0f + 0.055f) * (float)Math.pow(b, (1.0d / 2.4d)) - 0.055f;
        }
        // The rgb values from the above formulas are between 0.0 and 1.0.
        // make it 0-255
        int[] values = {
                Math.round(r * 255f),
                Math.round(g * 255f),
                Math.round(b * 255f)
        };
        return values;
    }
}
