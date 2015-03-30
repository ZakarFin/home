package fi.zakar.lights;

import fi.zakar.lights.domain.LightDevice;
import fi.zakar.lights.domain.LightGroup;
import fi.zakar.lights.LightsWorker;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by zakar on 26/12/14.
 */
public class LightsService {

    private long deviceIdSeq = 1000;

    private Collection<LightsWorker> workers = new HashSet<LightsWorker>();
    private Collection<LightDevice> devices = new HashSet<LightDevice>();
    private Collection<LightGroup> groups = new HashSet<LightGroup>();

    public void registerWorker(LightsWorker worker) {
        workers.add(worker);
        worker.start();
    }

    public void registerDevice(LightDevice device) {
        if(device.getId() == -1) {
            // maybe check that there is no conflicting id?
            device.setId(++deviceIdSeq);
        }
        devices.add(device);
    }

    public Collection<LightDevice> getDevices() {
        return devices;
    }
    public LightDevice getDeviceById(long id) {
        for(LightDevice dev : devices) {
            if(dev.getId() == id) return dev;
        }
        return null;
    }

    public void registerGroup(LightGroup group) {
        groups.add(group);
    }

    public Collection<LightGroup> getGroups() {
        return groups;
    }

    public Collection<LightsWorker> getWorkers() {
        return workers;
    }

    @Override
    public void finalize() throws Throwable {
        for(LightsWorker w : getWorkers()) {
            w.finalize();
        }
        super.finalize();
    }
}
