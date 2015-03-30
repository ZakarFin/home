package fi.zakar.handlers;

import fi.zakar.vdr.ChannelService;
import fi.zakar.vdr.RecordingService;
import fi.zakar.vdr.TimerService;
import fi.zakar.vdr.VDR;
import fi.zakar.lights.LightsService;
import fi.zakar.lights.LightsWorker;
import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.responses.highlevel.EPGEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by zakar on 16/03/15.
 */
@RequestMapping("/vdr")
@RestController
public class VdrHandler {

    @Value("${vdr.host}")
    private String host;
    @Value("${vdr.port:6419}")
    private int port;
    @Value("${vdr.timeout:2500}")
    private int connectTimeout;

    private VDR vdr;
    private ChannelService channels;
    private RecordingService recordings;
    private TimerService timers;

    private static Logger log = LoggerFactory.getLogger(VdrHandler.class);
    private LightsService lightsService = new LightsService();

    @Autowired
    private Set<LightsWorker> workers;

    @PostConstruct
    public void init() {
        vdr = new VDR(host, port, connectTimeout);
        channels = new ChannelService(vdr);
        recordings = new RecordingService(vdr);
        timers = new TimerService(vdr);
    }

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody List<Channel> index() throws Exception {
        return channels.getChannels();
    }

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public @ResponseBody List<EPGEntry> sendCommand(@PathVariable int id) throws Exception {
        log.debug("Action called! with id: " + id);
        return channels.getEpg(id);
    }
}
