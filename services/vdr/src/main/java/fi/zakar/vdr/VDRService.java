package fi.zakar.vdr;

import org.hampelratte.svdrp.Command;
import org.hampelratte.svdrp.Response;

/**
 * Created by zakar on 16/11/14.
 */
public class VDRService {

    private VDR vdr;

    public void setVdr(VDR vdr) {
        this.vdr = vdr;
    }

    public Response send(Command cmd) {
        try {
            return vdr.send(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
