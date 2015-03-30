package fi.zakar;

import fi.zakar.lights.domain.LightDevice;
import fi.zakar.lights.hue.HueCommander;
import fi.zakar.lights.hue.HueDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Hello world!
 * https://github.com/peter-murray/node-hue-api
 * https://github.com/Q42/Jue/blob/master/jue/src/nl/q42/jue/BridgeDiscovery.java
 * https://github.com/bsalinas/huejs/blob/master/huejs.js
 *
 * samsung tv :
 * https://openhab.googlecode.com/hg-history/1.3.0-kepler/bundles/binding/org.openhab.binding.samsungtv/?r=1.3.0-kepler
 * https://openhab.googlecode.com/hg-history/1.3.0-kepler/bundles/binding/org.openhab.binding.samsungtv/src/main/java/org/openhab/binding/samsungtv/internal/SamsungTvGenericBindingProvider.java
 * https://github.com/BrightcoveOS/Samsung-Smart-TV-Sample-App
 * https://github.com/instant-solutions/Java-Samsung-Remote-Library
 */
public class App 
{
    private static Logger log = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        HueCommander ce = new HueCommander();
        ce.connect("10.0.0.117", 80, "newdeveloper");
        Collection<LightDevice> list = ce.getDevices();
        System.out.println(list.isEmpty());
        log.debug("List", list);
        System.out.println("Hello World!");
    }
}
