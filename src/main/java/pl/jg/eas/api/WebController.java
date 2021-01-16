package pl.jg.eas.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.jg.eas.dtos.EventShortInfoDto;
import pl.jg.eas.services.EventService;

import java.util.List;

@RestController
public class WebController {

    private final EventService eventService;

    public WebController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/future-events")
    public List<EventShortInfoDto> getFutureEvents(
            @RequestParam(name = "dateFilter", defaultValue = "false") boolean dateFilter,
            @RequestParam(name = "after", required = false) String after,
            @RequestParam(name = "before", required = false) String before
            ) {
        return eventService.getFutureEvents(dateFilter, after, before);
    }
}
