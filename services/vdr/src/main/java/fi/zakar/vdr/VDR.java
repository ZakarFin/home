package fi.zakar.vdr;

import org.hampelratte.svdrp.Command;
import org.hampelratte.svdrp.Connection;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.*;
import org.hampelratte.svdrp.responses.highlevel.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by zakar on 26/10/14.
 */
public class VDR {

    private static transient Logger logger = LoggerFactory.getLogger(VDR.class);

    private final String host;
    private int port = 6419;
    private int connectTimeout = 2500;
    private Connection connection = null;
    // private ConnectionTester tester = null;
    private boolean vdrAvailable = false;

    /**
     * If set, the connection will be kept open for some time, so that consecutive request will be much faster
     */
    public static boolean persistentConnection;

    private static java.util.Timer timer;

    private static long lastTransmissionTime = 0;

    /**
     * The time in ms, the connection will be kept alive after the last request. {@link #persistentConnection} has to be set to true.
     */
    private static final int CONNECTION_KEEP_ALIVE = 15000;

    public VDR(String host, int port, int connectTimeout) {
        this.host = host;
        this.port = port;
        this.connectTimeout = connectTimeout;
        // tester = new ConnectionTester();
        // tester.start();
    }




    public Response send(Command cmd) throws IOException {
        Response res = null;

        if (connection == null) {
            logger.trace("New connection");
            connection = new Connection(host, port, connectTimeout);
        } else {
            logger.trace("old connection");
        }

        try {
            res = connection.send(cmd);
        } finally {
            lastTransmissionTime = System.currentTimeMillis();
            if (!persistentConnection) {
                connection.close();
                connection = null;
            } else {
                if (timer == null) {
                    logger.debug("Starting connection closer");
                    timer = new java.util.Timer("SVDRP connection closer");
                    timer.schedule(new ConnectionCloser(), 0, 100);
                }
            }
        }

        return res;
    }

    class ConnectionCloser extends TimerTask {
        @Override
        public void run() {
            if (connection != null && (System.currentTimeMillis() - lastTransmissionTime) > CONNECTION_KEEP_ALIVE) {
                logger.debug("Closing connection");
                try {
                    connection.close();
                    connection = null;
                    timer.cancel();
                    timer = null;
                } catch (IOException e) {
                    logger.error("Couldn't close connection", e);
                }
            }
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException, ParseException {
        VDR.persistentConnection = true;
        VDR vdr = new VDR("localhost", 2001, 5000);
        // VDR vdr = new VDR("vdr", 6419, 5000);

        Timer timer = new Timer();
        timer.setTitle("Testimer");
        Calendar startTime = Calendar.getInstance();
        startTime.set(2011, 10, 30, 20, 15);
        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.MINUTE, 90);
        timer.setStartTime(startTime);
        timer.setEndTime(endTime);
        timer.setChannelNumber(2);
        //vdr.newTimer(timer);

        VDR.timer.cancel();
        vdr.connection.close();
    }

    public boolean isAvailable() {
        return vdrAvailable;
    }

    public class ConnectionTester extends Thread {

        private boolean running = false;

        @Override
        public void run() {
            running = true;
            while (running) {
                try {
                    send(new STAT());
                    vdrAvailable = true;
                } catch (UnknownHostException e1) {
                    vdrAvailable = false;
                } catch (IOException e1) {
                    vdrAvailable = false;
                }

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(30));
                } catch (InterruptedException e) {
                    logger.warn("ConnectionTester interrupted while sleeping. Will stop now!");
                    running = false;
                }
            }
        }

        public boolean isRunning() {
            return running;
        }

        public void stopNow() {
            running = false;
            interrupt();
        }
    }
}
