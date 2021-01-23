package pl.jg.eas.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.jg.eas.services.EventService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EventRestController {

    private final EventService eventService;

    public EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<EventApiInfoDto> getFutureEvents(
            @RequestParam(name = "dateFilter", defaultValue = "false") boolean dateFilter,
            @RequestParam(name = "after", required = false) String after,
            @RequestParam(name = "before", required = false) String before
            ) {
        return eventService.getFutureEvents(dateFilter, after, before);
    }
}
