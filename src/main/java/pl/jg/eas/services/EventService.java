package pl.jg.eas.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.jg.eas.dao.CommentRepository;
import pl.jg.eas.dao.EventRepository;
import pl.jg.eas.dao.UserRepository;
import pl.jg.eas.dtos.*;
import pl.jg.eas.entities.Comment;
import pl.jg.eas.entities.Event;
import pl.jg.eas.entities.Role;
import pl.jg.eas.entities.User;
import pl.jg.eas.exceptions.EventDoesntExistException;
import pl.jg.eas.exceptions.NotOwnerException;
import pl.jg.eas.exceptions.UserDoesntExistException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventService {

    private static final Sort START_DATE = Sort.by("startDate").ascending();

    private final UserContextService userContextService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    public EventService(UserContextService userContextService,
                        UserRepository userRepository,
                        EventRepository eventRepository,
                        CommentRepository commentRepository) {
        this.userContextService = userContextService;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void addEvent(NewEventForm newEventForm) {
        final String currentlyLoggedUserEmail = userContextService.getCurrentlyLoggedUserEmail();

        final User user = userRepository.findUserByEmail(currentlyLoggedUserEmail)
                .orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUserEmail));

        final Event event = new Event();

        event.setTitle(newEventForm.getTitle());
        event.setStartDate(newEventForm.getStartDate());
        event.setEndDate(newEventForm.getEndDate());
        event.setDescription(newEventForm.getDescription());
        event.setUser(user);

        eventRepository.save(event);
    }

    public List<EventShortInfoDto> getCurrentAndFutureEvents() {
        return eventRepository.findByEndDateGreaterThanEqual(LocalDate.now(), START_DATE)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<EventShortInfoDto> getEventsContaining(String title, String time) {
        List<EventShortInfoDto> eventsList = new ArrayList<>();

        if ("future".equals(time)) {
            eventsList.addAll(eventRepository.findByTitleContainingAndStartDateAfter(title, LocalDate.now(), START_DATE)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
        } else if (time.equals("currentAndFuture")) {
            eventsList.addAll(eventRepository.findByTitleContainingAndEndDateGreaterThanEqual(title, LocalDate.now(), START_DATE)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
        } else {
            eventsList.addAll(eventRepository.findByTitleContaining(title, START_DATE)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList()));
        }
        return eventsList;
    }

    private EventShortInfoDto convertToDto(Event event) {
        return new EventShortInfoDto(
                event.getId(),
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                event.getDescription()
        );
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

    public void editEvent(EditEventForm editEventForm, Long eventId) {

        final String currentlyLoggedUserEmail = userContextService.getCurrentlyLoggedUserEmail();
        if (!isOwnerOrAdmin(eventId, currentlyLoggedUserEmail)) {
            throw new NotOwnerException(currentlyLoggedUserEmail);
        }

        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventDoesntExistException(eventId));

        event.setTitle(editEventForm.getTitle());
        event.setStartDate(editEventForm.getStartDate());
        event.setEndDate(editEventForm.getEndDate());
        event.setDescription(editEventForm.getDescription());

        eventRepository.save(event);
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

        event.signUp(user);

        eventRepository.save(event);
    }

    @Transactional
    public void signOff(Long eventId, String currentlyLoggedUser) {
        final Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventDoesntExistException(eventId));
        final User user = userRepository.findUserByEmail(currentlyLoggedUser).orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUser));

        event.signOff(user);

        eventRepository.save(event);
    }

    public boolean isSignedUp(Long eventId, String currentlyLoggedUser) {
        boolean isSignedUp = false;

        if (currentlyLoggedUser != null) {
            final Set<User> signedUpForEvents = eventRepository.findById(eventId)
                    .orElseThrow(() -> new EventDoesntExistException(eventId))
                    .getSignedUpForEvents();

            for (User user : signedUpForEvents) {
                if (user.getEmail().equals(currentlyLoggedUser)) {
                    isSignedUp = true;
                    break;
                }
            }
        }

        return isSignedUp;
    }

    public Set<User> getSignedUpUsers(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventDoesntExistException(eventId))
                .getSignedUpForEvents();
    }

    public List<EventShortInfoDto> getUserOwnerEvents(String currentlyLoggedUserEmail) {
        return eventRepository.findByUserEmail(currentlyLoggedUserEmail, START_DATE)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}
