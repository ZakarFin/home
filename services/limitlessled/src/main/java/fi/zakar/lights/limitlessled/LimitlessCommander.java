package fi.zakar.lights.limitlessled;

import fi.zakar.lights.LightsWorker;
import fi.zakar.lights.domain.LightDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by zakar on 27/12/14.
 */
@Service
public class LimitlessCommander extends LightsWorker<LLDevice> {

    private static Logger log = LoggerFactory.getLogger(LimitlessCommander.class);

    public static int DEFAULT_PORT = 8899;

    @Value("${lights.limitless.ip}")
    private String ip;

    @Value("${lights.limitless.port:8899}")
    private int port = DEFAULT_PORT;

    @Override
    public void connect(String address, int port, String... params) {
        this.ip = address;
        this.port = port;
    }

    public Collection<LightDevice> getDevices() {
        Collection<LightDevice> list = new HashSet<LightDevice>();
        for(LLDevice.Group group : LLDevice.Group.values()) {
            LLDevice dev = new LLDevice(group, this);
            dev.setName("LL " + group.toString());
            list.add(dev);
        }
        return list;
    }

    private DatagramSocket clientSocket = null;
    private InetAddress IPAddress = null;

    public void processDevice(LLDevice dev) {
        log.debug("Processing ", dev);

        if(!dev.isPower()) {
            log.debug("Turning off device");
            off(dev);
            return;
        }
        // switch to device
        log.debug("Turning on/switching to device ", dev.getGroup());
        on(dev);
        // setup additional props
        Map<LightDevice.Capability, Object> values = dev.getValues();
        for(Map.Entry<LightDevice.Capability, Object> entry : values.entrySet()) {
            halt(getDelay());
            switch (entry.getKey()) {
                case DIMMABLE:
                    if(dev.getDimming() == 100) {
                        sendCommand(Command.BRIGHTNESS_MAX);
                    }
                    else {
                        sendCommand(Command.BRIGHTNESS, dev.getDimming());
                    }
                    break;
                case DISCO:
                    sendCommand(Command.DISCO_ON);
                    break;
                // TODO: color etc
            }
        }
        dev.resetValues();
        log.debug("Processed device");
    }


    public void on(LLDevice dev) {
        switch (dev.getGroup()) {
            case ALL:
                sendCommand(Command.MASTER_ON);
                break;
            case G1:
                sendCommand(Command.GROUP_1_ON);
                break;
            case G2:
                sendCommand(Command.GROUP_2_ON);
                break;
            case G3:
                sendCommand(Command.GROUP_3_ON);
                break;
            case G4:
                sendCommand(Command.GROUP_4_ON);
                break;
        }
    }

    public void off(LLDevice dev) {
        switch (dev.getGroup()) {
            case ALL:
                sendCommand(Command.MASTER_OFF);
                break;
            case G1:
                sendCommand(Command.GROUP_1_OFF);
                break;
            case G2:
                sendCommand(Command.GROUP_2_OFF);
                break;
            case G3:
                sendCommand(Command.GROUP_3_OFF);
                break;
            case G4:
                sendCommand(Command.GROUP_4_OFF);
                break;
        }
    }

    public void sendCommand(Command cmd){
        sendCommand(cmd, null);
    }

    public void sendCommand(Command cmd, Integer byteValue) {
        final byte[] bytes = cmd.getBytes(byteValue);

        log.debug("Sending command: ", bytes);
        final DatagramSocket client = getClient();
        try {
            client.send(new DatagramPacket(bytes, bytes.length, getAddress(), port));
        } catch (Exception ex) {
            log.error("Couldn't send command to LLDevice: " + ex.getMessage(), ex);
        }
    }

    private DatagramSocket getClient() {
        if(clientSocket != null) {
            return clientSocket;
        }
        try {
            clientSocket = new DatagramSocket();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return clientSocket;
    }

    private InetAddress getAddress() {
        if(IPAddress != null) {
            return IPAddress;
        }
        try {
            IPAddress = InetAddress.getByName(ip);
            if(!IPAddress.isReachable(2000)) {
                System.out.println("ERROR! Configured IP is unreachable:" + ip);
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return IPAddress;
    }

    public void finalize() throws Throwable {
        if(clientSocket != null) {
            // maybe try/catch?
            clientSocket.close();
        }
        super.finalize();
    }
}
