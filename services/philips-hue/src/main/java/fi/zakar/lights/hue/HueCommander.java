package fi.zakar.lights.hue;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import fi.zakar.lights.LightsWorker;
import fi.zakar.lights.domain.LightDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by zakar on 27/12/14.
 */

@Service
public class HueCommander extends LightsWorker<HueDevice> {

	private static Logger log = LoggerFactory.getLogger(HueCommander.class);

    @Value("${lights.hue.ip}")
	private String ip;

    @Value("${lights.hue.user}")
    private String user;

    private String address;

    @PostConstruct
    private void init() {
        this.address = "http://" + ip + "/api/" + user + "/lights/";
        log.info(this.address);
    }

    @Override
    public void connect(String address, int port, String... params) {
        this.address = "http://" + address + "/api/" + params[0] + "/lights/";
        log.info(this.address);
    }
/*
{
	"6": {
		"state": {
			"on": true,
			"bri": 254,
			"hue": 14922,
			"sat": 144,
			"xy": [0.4595, 0.4105],
			"ct": 369,
			"alert": "none",
			"effect": "none",
			"colormode": "ct",
			"reachable": false
		},
		"type": "Extended color light",
		"name": "Hue Spot 6",
		"modelid": "LCT003",
		"uniqueid": "00:17:88:01:00:f0:72:d1-0b",
		"swversion": "66010732"
	},
	"7": {
		"state": {
			"on": true,
			"bri": 254,
			"hue": 65500,
			"sat": 253,
			"xy": [0.7024, 0.2962],
			"alert": "none",
			"effect": "none",
			"colormode": "xy",
			"reachable": true
		},
		"type": "Color light",
		"name": "LightStrips 1",
		"modelid": "LST001",
		"uniqueid": "00:17:88:01:00:cf:e7:08-0b",
		"swversion": "66010400"
	}
}
 */
    public Collection<LightDevice> getDevices() {
		final Collection<LightDevice> result = new HashSet<LightDevice>();
		// https://github.com/kevinsawicki/http-request
        final HttpRequest request =  HttpRequest.get(address);
        final String json = request.body();

		log.info("Got JSON: " + json);

		ObjectMapper mapper = new ObjectMapper(); // create once, reuse
		// to prevent exception when encountering unknown property:
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// to allow coercion of JSON empty String ("") to null Object value:
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		try {
			Map<String, Object> value = mapper.readValue(json, Map.class);
			for(Map.Entry<String, Object> entry : value.entrySet()) {
				JsonMapping light = mapper.convertValue(entry.getValue(), JsonMapping.class);
				// TODO: setup device-class based on json
				HueDevice dev = null;
				if("Color light".equalsIgnoreCase(light.type)) {
					dev = new ColorLight(this);
				}
				else if("Extended color light".equalsIgnoreCase(light.type)) {
					dev = new ExtendedColorLight(this);
				}
				else {
					// TODO: notify unknown device type?
					continue;
				}
				dev.setHueId(Long.parseLong(entry.getKey()));
				dev.setName(light.name);
				result.add(dev);
			}
		} catch (Exception ex) {
			log.error("Error parsing json to object", ex);
		}

		return result;
    }

    public void processDevice(HueDevice dev) {
		log.info("Processing hue device!");
        if(!dev.isPower()) {
            off(dev);
            return;
        }
        // create JSON with Jackson and call
        // http://ip/api/username/light?/ + dev.getId()
		on(dev);
		log.info("Processed hue device!");

    }

	/*
Address	http://<bridge ip address>/api/newdeveloper/lights/1/state
Body	{"on":false}
Method	PUT
     */
	private void off(HueDevice dev) {
		final HttpRequest request =  HttpRequest.put(address + dev.getHueId() + "/state");
		int responseCode = request.send("{\"on\":false}").code();
		log.info("Turned Hue(" + dev.getHueId() + ") off, responsecode " + responseCode +
				". Body is " + request.body());
	}

	/*
Address	http://<bridge ip address>/api/newdeveloper/lights/1/state
Body	{"on":false}
Method	PUT
     */
	private void on(HueDevice dev) {
		final HttpRequest request =  HttpRequest.put(address + dev.getHueId() + "/state");
		int responseCode = request.send("{\"on\":true}").code();
		log.info("Turned Hue(" + dev.getHueId() + ") on, responsecode " + responseCode +
				". Body is " + request.body());
	}
/*

errors (url had /lights//7/state):
[
	{
		"error": {
			"type": 4,
			"address": "/lights",
			"description": "method, PUT, not available for resource, /lights"
		}
	}
]
--- (tried to put /lights/7/state/on with payload "true" instead of json)
[
	{
		"error": {
			"type": 2,
			"address": "/lights/7/state/on",
			"description": "body contains invalid json"
		}
	}
]

success:
[
	{
		"success": {
			"/lights/7/state/on": true
		}
	}
]
 */
}
