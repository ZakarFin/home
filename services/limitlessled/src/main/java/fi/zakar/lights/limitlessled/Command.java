package fi.zakar.lights.limitlessled;

/**
 * Send MASTER_ON/GROUP_x_ON 100ms before color/brightness/white to direct the command to all/specific group.
 * For more details see: http://www.limitlessled.com/dev/
 */
public enum Command {
    MASTER_ON(0x42),
    MASTER_OFF(0x41),
    MASTER_WHITE(0xC2), // needs MASTER_ON 100ms before this

    GROUP_1_ON(0x45), // also sync if not synced before
    GROUP_1_OFF(0x46),
    GROUP_1_WHITE(0xC5), // needs GROUP_1_ON 100ms before this

    GROUP_2_ON(0x47),
    GROUP_2_OFF(0x48),
    GROUP_2_WHITE(0xC7), // needs GROUP_2_ON 100ms before this

    GROUP_3_ON(0x49),
    GROUP_3_OFF(0x4A),
    GROUP_3_WHITE(0xC9), // needs GROUP_3_ON 100ms before this

    GROUP_4_ON(0x47),
    GROUP_4_OFF(0x48),
    GROUP_4_WHITE(0xCB), // needs GROUP_4_ON 100ms before this

    BRIGHTNESS(0x4E), // the second byte is the brightness value (0×00 to 0xFF)
    BRIGHTNESS_MAX(0x4E,0x3B), // full brightness 0x3B
    COLOR(0x40), // the second byte is the color value (0×00 to 0xFF (255 colors))

    DISCO_ON(0x4D), // exit with xx_WHITE command
    DISCO_SPEED_SLOWER(0x43),

    DISCO_SPEED_FASTER(0x44);

    private final Integer cmdByte;
    private Integer valueByte = 0x00;

    Command(int... command) {
        cmdByte = command[0];
        if(command.length > 1) {
            valueByte = command[1];
        }
        /*
        const LLL_BLUE = 0x10;
        const LLL_GREEN = 0x60;
        const LLL_CYAN = 0x40;
        const LLL_RED = 0xB0;
        const LLL_MAGENTA = 0xD0;
        const LLL_YELLOW = 0x80;
*/
    }

    public byte[] getBytes() {
        return getBytes(valueByte);
    }

    public byte[] getBytes(Integer value) {
        if(value == null) {
            return getBytes();
        }
        return new byte[] {cmdByte.byteValue(), value.byteValue(), 0x55};
    }
    /*
    brightness: (rgbwSteps == 30?)

				if (command.intValue() > 0 && command.intValue() < 100 ) {
					int newCommand = (command.intValue() * rgbwSteps / 100);
					sendOn(bulb, bridgeId);
					Thread.sleep(100);
					String messageBytes = "4E:" + Integer.toHexString(newCommand) + ":55";
			        	logger.debug("milight: send dimming packet '{}' to RGBW bulb channel '{}'", messageBytes, bulb);
					sendMessage(messageBytes, bridgeId);
				}
				else if (command.intValue() > 99) {
					sendFull(bulb, rgbwSteps, bridgeId);
				}
				else if (command.intValue() < 1) {
					sendOff(bulb, bridgeId);
				}


	private void sendColor(Command command, String bridgeId, int bulb) {
		logger.debug("milight: sendColor");
		HSBType hsbCommand = (HSBType) command;
		DecimalType hue = hsbCommand.getHue();

		// we have to map [0,360] to [0,0xFF], where red equals hue=0 and the milight color 0xB0 (=176)
		Integer milightColorNo = (256 + 176 - (int) (hue.floatValue() / 360.0 * 255.0)) % 256;
		try {
            sendOn(bulb, bridgeId);
            Thread.sleep(100);
            String messageBytes = "40:" + Integer.toHexString(milightColorNo) + ":55";
            sendMessage(messageBytes, bridgeId);
		} catch(InterruptedException e) {
		logger.debug("Sleeping thread has been interrupted.");
		}
	}
     */

}

