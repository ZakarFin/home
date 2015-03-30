package fi.zakar.lights.domain;

import fi.zakar.lights.LightsWorker;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by zakar on 26/12/14.
 */
public abstract class LightDevice {

    public enum Capability {
        DIMMABLE(1),
        COLOR(2),
        DISCO(3),
        QUERYABLE(4);

        private int cap;
        private Capability(int cap) {
            this.cap = cap;
        }
    }

    private Map<Capability, Object> values = new HashMap<Capability, Object>(Capability.values().length);

    private long id = -1;
    private String name;
    private Collection<Capability> caps = new HashSet<Capability>();

    private boolean powerOn = true;

    protected void addCapability(Capability cap) {
        caps.add(cap);
    }

    public abstract LightsWorker getExecutor();

    public void setPower(boolean on) {
        powerOn = on;
    }

    public boolean isPower() {
        return powerOn;
    }

    public void setDimming(int dim) {
        values.put(Capability.DIMMABLE, dim);
    }

    public int getDimming() {
        if(values.containsKey(Capability.DIMMABLE)) {
            return (Integer) values.get(Capability.DIMMABLE);
        }
        return -1;
    }

    public int getColor() {
        return -1;
    }

    public void setColor() {
//        this.color = color;
    }

    public boolean isDisco() {
        if(values.containsKey(Capability.DISCO)) {
            return (Boolean) values.get(Capability.DISCO);
        }
        return false;
    }

    public void setDisco(boolean disco) {
        values.put(Capability.DISCO, disco);
    }

    public Map<Capability, Object> getValues() {
        return values;
    }

    public void resetValues() {
        getValues().clear();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void commit() {
        getExecutor().addOperation(this);
    }
}
