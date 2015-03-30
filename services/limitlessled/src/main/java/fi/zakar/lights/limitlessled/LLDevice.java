package fi.zakar.lights.limitlessled;

import fi.zakar.lights.LightsWorker;
import fi.zakar.lights.domain.LightDevice;

/**
 * Created by zakar on 27/12/14.
 */
public class LLDevice extends LightDevice {

    public enum Group {
        ALL, G1, G2, G3, G4
    }

    private Group group = Group.ALL;
    private LimitlessCommander executor;

    public LLDevice(Group grp, LimitlessCommander exec) {
        group = grp;
        executor = exec;
        addCapability(Capability.DIMMABLE);
        addCapability(Capability.COLOR);
        addCapability(Capability.DISCO);
    }

    public Group getGroup() {
        return group;
    }

    @Override
    public LightsWorker getExecutor() {
        return executor;
    }
}
