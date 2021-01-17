package pl.jg.eas.controllers.global;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GlobalController {

    @GetMapping("/not-allowed")
    public String showNotAllowedPage() {
        return "errors/notAllowed";
    }
}
