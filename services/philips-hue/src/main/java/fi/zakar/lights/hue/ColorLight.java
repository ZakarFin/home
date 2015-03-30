package fi.zakar.lights.hue;

/**
 * Created by zakar on 27/12/14.
 */
public class ColorLight extends HueDevice {

    public ColorLight(HueCommander exec) {
        // Extended
        super(exec);
        addCapability(Capability.COLOR);
    }
}
