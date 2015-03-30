package fi.zakar.lights.limitlessled;
/**
 * Created with IntelliJ IDEA.
 * User: zakar
 * Date: 27/10/13
 * Time: 18:51
 *
 * Reset/unpair, kaukosäätimestä zone on nappia pohjassa kun lamppuun laittaa virran -> välkkyy ~10 kertaa -> nollaa lampun täysin
 * Pair, kaukosäätimestä/softasta zone on nappia kerran kun lamppuun laittaa virran -> välkkyy ~3 kertaa
 * Pair softasta == pair wlanin kautta -> tarvii tehdä vain kerran.
 *
 * http://www.limitlessled.com/dev/
 * http://ninjablocks.com/blogs/how-to/8801881-ninja-limitlessled
 */

public class App
{
    // http://www.limitlessled.com/dev/#limitleslleddiscovery

    private static final String LIMITLESS_CONTROLLER_IP = "10.0.0.116";

    public static void main(String args[]) throws Exception
    {
        final LimitlessCommander me = new LimitlessCommander();
        me.connect(LIMITLESS_CONTROLLER_IP);
        me.start();
        LLDevice masterDevice = new LLDevice(LLDevice.Group.ALL, me);
        masterDevice.setPower(true);
        masterDevice.setDimming(100);
        me.addOperation(masterDevice);
        me.shutdown();

            //me.sendCommand(Command.MASTER_ON);
            //me.sendCommand(Command.BRIGHTNESS_MAX);
            if(true) return;
            redBlueBlinkingLights(me);

        LLDevice group1 = new LLDevice(LLDevice.Group.G1, me);
        group1.setPower(true);
        // group1.setColor(255,255,255);
        me.addOperation(group1);
        /*
            me.sendCommand(Command.GROUP_1_ON);
            Thread.sleep(100);
            me.sendCommand(Command.GROUP_1_WHITE);
            Thread.sleep(100);
*/
            me.sendCommand(Command.GROUP_2_ON);
            Thread.sleep(100);
            me.sendCommand(Command.GROUP_2_WHITE);
/*
            for(int i = 0; i < 25; i++) {
                me.sendCommand(Command.BRIGHTNESS, i*10);
                Thread.sleep(500);
            }
            // max brightness
            me.sendCommand(Command.BRIGHTNESS, 0x3B);
            */

/*
            for(int i = 0; i < 255; i++) {
                me.sendCommand(Command.COLOR, i);
                Thread.sleep(500);

            }
            me.sendCommand(Command.COLOR, getColor(0,255,0));
*/

    }

    // FIXME: this doesn't work correctly
    // the lamp might have mapped colors instead of 8-bit colors
    private static int getColor(int red, int green, int blue) {
        //R & 0xE0) | ((G & 0xE0)>>3) | (B >> 6)
        // [(Red / 32) << 5] + [(Green / 32) << 2] + (Blue / 64)
        int r = red & 0xE0;
        int g = (green & 0xE0) >> 3;
        int b = blue >> 6;
        Integer result = r | g | b;
        System.out.println(red + "/" + green + "/" + blue + "=" + result);
        //System.out.println("Byte value: " + result.byteValue());
        System.out.println("Bitwise value: " + (result & 0xFF));
        return result;
    }

    private static void redBlueBlinkingLights(LimitlessCommander me) throws Exception {

        final int BLUE  = 0x10;
        final int RED = 0xB0;
        int[] color = {BLUE, RED, BLUE};

        for(int i = 0; i < 10; i++) {
            me.sendCommand(Command.GROUP_1_ON);
            Thread.sleep(100);
            me.sendCommand(Command.COLOR, color[i%2]);
            Thread.sleep(100);
            // send another color command since commands don't always register when spamming
            me.sendCommand(Command.COLOR, color[i%2]);
            Thread.sleep(200);

            me.sendCommand(Command.GROUP_2_ON);
            Thread.sleep(100);
            me.sendCommand(Command.COLOR, color[(i%2) + 1]);
            Thread.sleep(100);
            // send another color command since commands don't always register when spamming
            me.sendCommand(Command.COLOR, color[(i%2) + 1]);
            Thread.sleep(500);
        }
    }


}