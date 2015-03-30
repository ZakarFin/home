package fi.zakar.lights.hue;

/**
 * Created by zakar on 27/12/14.
 */
public class ExtendedColorLight extends HueDevice {

    public ExtendedColorLight(HueCommander exec) {
        // Extended
        super(exec);
        addCapability(Capability.COLOR);
        addCapability(Capability.DISCO);
    }
}
