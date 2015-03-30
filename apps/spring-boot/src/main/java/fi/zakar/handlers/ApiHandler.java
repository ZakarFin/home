package fi.zakar.handlers;

import fi.zakar.lights.LightsService;
import fi.zakar.lights.LightsWorker;
import fi.zakar.lights.domain.LightDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by zakar on 16/03/15.
 */
@RequestMapping("/api")
@RestController
public class ApiHandler {

    private static Logger log = LoggerFactory.getLogger(ApiHandler.class);
    private LightsService lightsService = new LightsService();

    @Autowired
    private Set<LightsWorker> workers;

    @PostConstruct
    public void init() {
        for (LightsWorker w : workers) {
            lightsService.registerWorker(w);
            Collection<LightDevice> list = w.getDevices();
            for (LightDevice dev : list) {
                lightsService.registerDevice(dev);
            }
        }
    }
    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody Collection<Map<String, Object>> index() {
        Collection<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(LightDevice dev : lightsService.getDevices()) {

            Map<String, Object> value = new HashMap<String, Object>();
            value.put("id", dev.getId());
            value.put("name", dev.getName());
            //value.put("values", dev.getValues());
            // TODO: capabilities
            list.add(value);
        }
        return list;
    }

    @RequestMapping(value="/{id}/{cmd}", method = RequestMethod.POST)
    public void sendCommand(@PathVariable String cmd,
                            @PathVariable long id,
                            @RequestParam(required = false, defaultValue = "") String type) {
        log.debug("Action called! with cmd: " + cmd + " type: " + type);
        LightDevice dev = lightsService.getDeviceById(id);
        if (dev == null) {
            log.error("Couldn't find device by id " + id);
            return;
        }
        dev.setPower("on".equals(cmd));
        dev.commit();
    }
}
