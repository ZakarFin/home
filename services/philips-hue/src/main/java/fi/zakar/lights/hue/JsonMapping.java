package fi.zakar.lights.hue;

/**
 * Created by zakar on 29/12/14.
 *
 */
/*

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
 */
public class JsonMapping {
    public class State {
        public boolean on;
        public int bri;
        public int hue;
        public int sat;
		public int ct;
        public double[] xy;
        public String alert;
        public String effect;
        public String colormode;
        public boolean reachable;
    }
    public State state;
    public String type;
    public String name;
    public String modelid;
    public String uniqueid;
    public String swversion;

}
