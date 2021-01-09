package pl.jg.eas.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.jg.eas.dao.CommentRepository;
import pl.jg.eas.dao.EventRepository;
import pl.jg.eas.dao.RoleRepository;
import pl.jg.eas.dao.UserRepository;
import pl.jg.eas.dtos.*;
import pl.jg.eas.entities.Comment;
import pl.jg.eas.entities.Event;
import pl.jg.eas.entities.Role;
import pl.jg.eas.entities.User;
import pl.jg.eas.exceptions.EventDoesntExistException;
import pl.jg.eas.exceptions.RoleDoesntExistException;
import pl.jg.eas.exceptions.UserDoesntExistException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventService {

    //dodatkowo, bo pododaniu tego eventu uzytkownik otrzumuje nowa role ROLE_EVENT_MANAGER albo cos w podobie

    private final UserContextService userContextService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RoleRepository roleRepository;
    private final CommentRepository commentRepository;

    public EventService(UserContextService userContextService, UserRepository userRepository, EventRepository eventRepository, RoleRepository roleRepository, CommentRepository commentRepository) {
        this.userContextService = userContextService;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.roleRepository = roleRepository;
        this.commentRepository = commentRepository;
    }

    private final LocalDate now = LocalDate.now();

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
        return eventRepository.findByEndDateAfter(now, Sort.by("startDate").ascending())
                .stream()
                .map(event -> new EventShortInfoDto(
                        event.getId(),
                        event.getTitle(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getDescription()
                ))
                .collect(Collectors.toList());
    }

    public List<EventShortInfoDto> getEventsContaining(String title, String time) {
        List<EventShortInfoDto> eventsList = new ArrayList<>();

        if (time.equals("future")) {
            eventsList.addAll(eventRepository.findByTitleContainingAndStartDateAfter(title, now, Sort.by("startDate").ascending())
                    .stream()
                    .map(event -> new EventShortInfoDto(
                            event.getId(),
                            event.getTitle(),
                            event.getStartDate(),
                            event.getEndDate(),
                            event.getDescription()
                    ))
                    .collect(Collectors.toList()));
        } else if (time.equals("currentAndFuture")) {
            eventsList.addAll(eventRepository.findByTitleContainingAndEndDateAfter(title, now, Sort.by("startDate").ascending())
                    .stream()
                    .map(event -> new EventShortInfoDto(
                            event.getId(),
                            event.getTitle(),
                            event.getStartDate(),
                            event.getEndDate(),
                            event.getDescription()
                    ))
                    .collect(Collectors.toList()));
        } else {
            eventsList.addAll(eventRepository.findByTitleContaining(title, Sort.by("startDate").ascending())
                    .stream()
                    .map(event -> new EventShortInfoDto(
                            event.getId(),
                            event.getTitle(),
                            event.getStartDate(),
                            event.getEndDate(),
                            event.getDescription()
                    ))
                    .collect(Collectors.toList()));
        }
        return eventsList;
    }

    public Optional<EventInfoDto> getSingleEventInfo(Long eventId) {
        final Optional<EventInfoDto> eventInfoDtoOptional = eventRepository.findById(eventId)
                .map(event -> new EventInfoDto(event.getId(),
                        event.getTitle(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getDescription()));

        eventInfoDtoOptional.ifPresent(eventInfoDto -> {
            final List<CommentDto> commentDtos = commentRepository
                    .findByEventId(eventId, Sort.by("added").descending())
                    .stream()
                    .map(comment -> new CommentDto(comment.getCommentatorEmail(),
                            comment.getAdded(),
                            comment.getCommentText()))
                    .collect(Collectors.toList());

            eventInfoDto.setComments(commentDtos);
        });

        return eventInfoDtoOptional;
    }

    public void editEvent(EditEventForm editEventForm, Long eventId) {

        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventDoesntExistException(eventId));

        event.setTitle(editEventForm.getTitle());
        event.setStartDate(editEventForm.getStartDate());
        event.setEndDate(editEventForm.getEndDate());
        event.setDescription(editEventForm.getDescription());

        eventRepository.save(event);
    }

    public boolean isOwnerOrAdmin(Long eventId, String currentlyLoggedUser) {
        boolean isOwnerOrAdmin = false;

        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventDoesntExistException(eventId));

        if (currentlyLoggedUser != null) {
            final Set<Role> roles = userRepository.findUserByEmail(currentlyLoggedUser)
                    .orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUser))
                    .getRoles();

            for (Role role : roles) {
                if (role.getRoleName().equals("ROLE_ADMIN")) {
                    isOwnerOrAdmin = true;
                    break;
                }
            }
        }

        if (event.getUser().getEmail().equals(currentlyLoggedUser)) {
            isOwnerOrAdmin = true;
        }

        return isOwnerOrAdmin;
    }

    public void addNewComment(Long eventId, NewCommentForm newCommentForm, String currentlyLoggedUser) {
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventDoesntExistException(eventId));

        final Comment comment = new Comment();
        comment.setCommentatorEmail(currentlyLoggedUser);
        comment.setCommentText(newCommentForm.getComment());
        comment.setEvent(event);

        commentRepository.save(comment);
    }

    public void signUp(Long eventId, String currentlyLoggedUser) {
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventDoesntExistException(eventId));
        final User user = userRepository.findUserByEmail(currentlyLoggedUser).orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUser));

        user.signUp(event);

        userRepository.save(user);
    }

    public boolean isSignedUp(Long eventId, String currentlyLoggedUser) {
        boolean isSignedUp = false;

        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventDoesntExistException(eventId));

        if (currentlyLoggedUser != null) {
            final List<Event> events = userRepository.findUserByEmail(currentlyLoggedUser)
                    .orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUser))
                    .getSignedUpForEvents();

            for (Event e : events) {
                if (e.getId() == eventId) {
                    isSignedUp = true;
                    break;
                }
            }
        }

        return isSignedUp;
    }
}
