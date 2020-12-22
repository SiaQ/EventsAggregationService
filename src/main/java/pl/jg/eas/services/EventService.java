package pl.jg.eas.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.jg.eas.dao.EventRepository;
import pl.jg.eas.dao.RoleRepository;
import pl.jg.eas.dao.UserRepository;
import pl.jg.eas.dtos.EventShortInfoDto;
import pl.jg.eas.dtos.NewEventForm;
import pl.jg.eas.entities.Event;
import pl.jg.eas.entities.Role;
import pl.jg.eas.entities.User;
import pl.jg.eas.exceptions.RoleDoesntExistException;
import pl.jg.eas.exceptions.UserDoesntExistException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    //dodatkowo, bo pododaniu tego eventu uzytkownik otrzumuje nowa role ROLE_EVENT_MANAGER albo cos w podobie

    private final UserContextService userContextService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RoleRepository roleRepository;

    public EventService(UserContextService userContextService, UserRepository userRepository, EventRepository eventRepository, RoleRepository roleRepository) {
        this.userContextService = userContextService;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.roleRepository = roleRepository;
    }

    public void addEvent(NewEventForm newEventForm) {
        final String currentlyLoggedUserEmail = userContextService.getCurrentlyLoggedUserEmail();

        final User user = userRepository.findUserByEmail(currentlyLoggedUserEmail)
                .orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUserEmail));

        final String roleName = "ROLE_EVENT_MANAGER";
        final Role role = roleRepository.findRoleByRoleName(roleName)
                .orElseThrow(() -> new RoleDoesntExistException(roleName));

        final Event event = new Event();

        event.setTitle(newEventForm.getTitle());
        event.setStartDate(newEventForm.getStartDate());
        event.setEndDate(newEventForm.getEndDate());
        event.setDescription(newEventForm.getDescription());
        event.setUser(user);

        if (!user.getRoles().contains(role)) {
            user.addRole(role);
            userRepository.save(user);
        }
        eventRepository.save(event);
    }

    public List<EventShortInfoDto> getCurrentAndFutureEvents() {
        final LocalDate now = LocalDate.now();
        return eventRepository.findAllByEndDateAfter(now, Sort.by("startDate").ascending())
                .stream()
                .map(event -> new EventShortInfoDto(
                        event.getTitle(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getDescription()
                ))
                .collect(Collectors.toList());
    }
}
