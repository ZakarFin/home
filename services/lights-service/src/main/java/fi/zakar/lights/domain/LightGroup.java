package fi.zakar.lights.domain;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by zakar on 26/12/14.
 */
public class LightGroup {

    private long id;
    private String name;
    private Collection<LightDevice> lights = new HashSet<LightDevice>();

    public Collection<LightDevice> getLights() {
        return lights;
    }
}
