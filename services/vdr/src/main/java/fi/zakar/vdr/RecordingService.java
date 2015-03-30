package fi.zakar.vdr;

import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.DELR;
import org.hampelratte.svdrp.commands.LSTR;
import org.hampelratte.svdrp.parsers.RecordingListParser;
import org.hampelratte.svdrp.parsers.RecordingParser;
import org.hampelratte.svdrp.responses.highlevel.Recording;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zakar on 16/11/14.
 */
public class RecordingService extends VDRService {

    public RecordingService(VDR vdr) {
        setVdr(vdr);
    }

    /**
     * Requests a list of all recordings.
     *
     * @return A list of all recordings.
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    public List<Recording> getRecordings() throws UnknownHostException, IOException {
        List<Recording> recordings = null;
        Response res = send(new LSTR());

        if (res != null) {
            if (res.getCode() == 250) {
                recordings = RecordingListParser.parse(res.getMessage());
            } else if (res.getCode() == 550) {
                // no recordings, return an empty list
                recordings = new ArrayList<Recording>();
            } else {
                // something went wrong
                throw new RuntimeException(res.getMessage());
            }
        } else {
            throw new RuntimeException("Response object is null");
        }

        return recordings;
    }

    /**
     * This method returns the details of one recording. You first have to obtain a Recording by calling {@link #getRecordings()} and then pass this object as
     * parameter.
     *
     * @param rec
     *            The recording to load the details for.
     * @return The same object with the details loaded.
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     * @throws java.text.ParseException
     */
    public Recording getRecordingDetails(Recording rec) throws UnknownHostException, IOException, ParseException {
        Response res = send(new LSTR(rec.getNumber()));
        if (res != null) {
            if (res.getCode() == 215) {
                new RecordingParser().parseRecording(rec, res.getMessage());
            } else {
                // something went wrong
                throw new RuntimeException(res.getMessage());
            }
        } else {
            throw new RuntimeException("Response object is null");
        }

        return rec;
    }

    /**
     * Deletes a recording. You first have to obtain a Recording by calling {@link #getRecordings()} and then pass this object as parameter.
     *
     * @param rec
     *            The recording to delete.
     * @return The response from VDR.
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    public Response deleteRecording(Recording rec) throws UnknownHostException, IOException {
        return send(new DELR(rec.getNumber()));
    }
}
