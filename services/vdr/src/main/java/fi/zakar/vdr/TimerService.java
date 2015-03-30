package fi.zakar.vdr;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.DELT;
import org.hampelratte.svdrp.commands.LSTT;
import org.hampelratte.svdrp.commands.MODT;
import org.hampelratte.svdrp.commands.NEWT;
import org.hampelratte.svdrp.parsers.TimerParser;
import org.hampelratte.svdrp.responses.highlevel.Timer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zakar on 16/11/14.
 */
public class TimerService extends VDRService {

    public TimerService(VDR vdr) {
        setVdr(vdr);
    }

    /**
     * Requests a list of all defined timers.
     *
     * @return A list of all defined timers.
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    public List<Timer> getTimers() throws UnknownHostException, IOException {
        return getTimers(new LSTT());
    }

    /**
     * Request a single timer.
     *
     * @param number
     *            The number of the timer to erturn.
     * @return A Timer object, which represents the timer settings.
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    public Timer getTimer(int number) throws UnknownHostException, IOException {
        List<Timer> timers = getTimers(new LSTT(number));
        if (timers.size() > 0) {
            return timers.get(0);
        } else {
            throw new RuntimeException("Timer " + number + " is not defined");
        }
    }

    private List<Timer> getTimers(LSTT lstt) throws UnknownHostException, IOException {
        List<Timer> timers = null;
        Response res = send(lstt);

        if (res != null) {
            if (res.getCode() == 250) {
                timers = TimerParser.parse(res.getMessage());
            } else if (res != null && (res.getCode() == 550 || res.getCode() == 501)) {
                // no timers defined, return an empty list
                timers = new ArrayList<Timer>();
            } else {
                // something went wrong
                throw new RuntimeException(res.getMessage());
            }
        } else {
            throw new RuntimeException("Response object is null");
        }

        return timers;
    }

    /**
     * Modifies an existing timer.
     *
     * @param number
     *            The number of the timer to modify.
     * @param timer
     *            A Timer, which represents the new timer settings.
     * @return The response from VDR.
     *
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    public Response modifyTimer(int number, Timer timer) throws UnknownHostException, IOException {
        return send(new MODT(number, timer));
    }

    /**
     * Deletes an existing timer.
     *
     * @param number
     *            The number of the timer to delete.
     * @return The response from VDR.
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    public Response deleteTimer(int number) throws UnknownHostException, IOException {
        return send(new DELT(number));
    }

    /**
     * Creates a new timer.
     *
     * @param timer
     *            A {@link org.hampelratte.svdrp.responses.highlevel.Timer}, which represents the timer settings.
     * @return The response from VDR.
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    public Response newTimer(Timer timer) throws UnknownHostException, IOException {
        return send(new NEWT(timer));
    }

}
