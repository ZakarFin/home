package fi.zakar.vdr;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.LSTC;
import org.hampelratte.svdrp.commands.LSTE;
import org.hampelratte.svdrp.parsers.ChannelParser;
import org.hampelratte.svdrp.parsers.EPGParser;
import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.responses.highlevel.EPGEntry;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zakar on 16/11/14.
 */
public class ChannelService extends VDRService {

    public ChannelService(VDR vdr) {
        setVdr(vdr);
    }

    /**
     * Requests the list of all channels.
     *
     * @return A list of all channels.
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    public List<Channel> getChannels() throws IOException, ParseException {
        List<Channel> channels = null;
        Response res = send(new LSTC());

        if (res != null) {
            if (res.getCode() == 250) {
                channels = ChannelParser.parse(res.getMessage(), true);
            } else {
                // something went wrong
                throw new RuntimeException(res.getMessage());
            }
        } else {
            throw new RuntimeException("Response object is null");
        }

        return channels;
    }

    /**
     * Get the whole EPG. To get the epg of one channel, call {@link #getEpg(int)} or filter this list manually.
     *
     * @return A list of all epg entries of all channels.
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    public List<EPGEntry> getEpg() throws UnknownHostException, IOException {
        return getEpg(new LSTE());
    }

    /**
     * Request the EPG of a single channel.
     *
     * @param channelNumber
     *            The channel number of the channel.
     * @return A list of all EPG entries available for the given channel.
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    public List<EPGEntry> getEpg(int channelNumber) throws UnknownHostException, IOException {
        return getEpg(new LSTE(channelNumber));
    }

    private List<EPGEntry> getEpg(LSTE lste) throws UnknownHostException, IOException {
        List<EPGEntry> epg = null;
        Response res = send(lste);

        if (res != null) {
            if (res.getCode() == 215) {
                epg = new EPGParser().parse(res.getMessage());
            } else {
                // something went wrong
                throw new RuntimeException(res.getMessage());
            }
        } else {
            throw new RuntimeException("Response object is null");
        }

        return epg;
    }

    public List<EPGEntry> getCurrentEpg() throws UnknownHostException, IOException {
        List<EPGEntry> epg = new ArrayList<EPGEntry>();

        LSTE lste = new LSTE();
        lste.setTime("now");
        epg.addAll(parseEpgResponse(send(lste)));

        lste.setTime("next");
        epg.addAll(parseEpgResponse(send(lste)));

        return epg;
    }
    private List<EPGEntry> parseEpgResponse(Response res) {

        if (res != null) {
            if (res.getCode() == 215) {
                return new EPGParser().parse(res.getMessage());
            } else {
                // something went wrong
                throw new RuntimeException(res.getMessage());
            }
        } else {
            throw new RuntimeException("Response object is null");
        }

    }
}
