package fi.zakar.handlers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zakar on 28/03/15.
 */
@Controller
public class PageHandler {

    @ModelAttribute("navi")
    public List<Map<String, Object>> getNavi(HttpServletRequest request) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(3);
        list.add(createPageMap("Valot", "/", request.getRequestURI()));
        list.add(createPageMap("Bussit", "/bus.html", request.getRequestURI()));
        list.add(createPageMap("Digibox", "/digibox.html", request.getRequestURI()));
        return list;
    }

    private Map<String, Object> createPageMap(final String name, final String path, final String currentPath) {
        Map<String, Object> page = new HashMap<String, Object>();
        page.put("name", name);
        page.put("path", path);
        page.put("active", path.equalsIgnoreCase(currentPath));

        return page;
    }

    @RequestMapping(value = "/",
            method = RequestMethod.GET,
            produces = {MediaType.TEXT_HTML_VALUE})
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/bus.html",
            method = RequestMethod.GET,
            produces = {MediaType.TEXT_HTML_VALUE})
    public String bus() {
        return "bus";
    }

    @RequestMapping(value = "/digibox.html",
            method = RequestMethod.GET,
            produces = {MediaType.TEXT_HTML_VALUE})
    public String digibox() {
        return "digibox";
    }
}
