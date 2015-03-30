package fi.zakar.lights;

import fi.zakar.lights.domain.LightDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by zakar on 27/12/14.
 */
public abstract class LightsWorker<T extends LightDevice> extends Thread {

    private static Logger log = LoggerFactory.getLogger(LightsWorker.class);

    private Queue<T> list = new ConcurrentLinkedQueue<T>();
    private boolean running = true;
    private long lastCmd = 0;

    public void connect(final String address) {
        connect(address, -1, null);
    }

    public abstract void connect(final String address, final int port, String... params);

    public abstract Collection<LightDevice> getDevices();

    public int getDelay() {
        return 100;
    }

    public void addOperation(T op) {
        log.debug("Adding operation", op);
        synchronized(list) {
            list.add(op);
            list.notifyAll();
        }
        log.debug("Added operation");
    }

    @Override
    public void run() {
        log.debug("Running");
        while (running) {
            synchronized(list) {
                if(list.isEmpty()) {
                    // TODO: check for stop signal?
                    log.debug("Waiting for ops");
                    waitForOps();
                    log.debug("Got ops!");
                }
            }
            final long start = System.currentTimeMillis();
            if(start - lastCmd < getDelay()) {
                halt(start-lastCmd);
            }
            // safeguard for rapid calls before first go
            if(lastCmd == 0) {
                lastCmd = start;
            }
            log.debug("Processing queue");
            final T dev = list.poll();
            processDevice(dev);
            lastCmd = System.currentTimeMillis();
            log.debug("Processed op in queue took ", (lastCmd - start), "ms");
        }
        log.debug("Stopped");
    }

    public abstract void processDevice(T dev);

    private void waitForOps() {
        try {
            list.wait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void halt(long millis) {
        try {
            sleep(millis);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void shutdown() {
        running = false;
    }

    public void finalize() throws Throwable {
        shutdown();
        super.finalize();
    }
}
