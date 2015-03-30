package fi.zakar.lights.hue;

import fi.zakar.lights.LightsWorker;
import fi.zakar.lights.domain.LightDevice;

/**
 * Created by zakar on 27/12/14.
 */
public class HueDevice extends LightDevice {

    private HueCommander executor;
    private long hueId;

    protected HueDevice() {
        addCapability(Capability.DIMMABLE);
    }

    public HueDevice(HueCommander exec) {
        executor = exec;
    }

    @Override
    public LightsWorker getExecutor() {
        return executor;
    }

    public long getHueId() {
        return hueId;
    }

    public void setHueId(long hueId) {
        this.hueId = hueId;
    }
}
