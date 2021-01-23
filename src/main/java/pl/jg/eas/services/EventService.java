package pl.jg.eas.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.jg.eas.api.EventApiInfoDto;
import pl.jg.eas.dao.CommentRepository;
import pl.jg.eas.dao.EventRepository;
import pl.jg.eas.dao.UserRepository;
import pl.jg.eas.dtos.*;
import pl.jg.eas.entities.Comment;
import pl.jg.eas.entities.Event;
import pl.jg.eas.entities.User;
import pl.jg.eas.enums.PeriodCriteria;
import pl.jg.eas.enums.Roles;
import pl.jg.eas.exceptions.EventDoesntExistException;
import pl.jg.eas.exceptions.NotOwnerException;
import pl.jg.eas.exceptions.UserDoesntExistException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private static final Sort START_DATE = Sort.by("startDate").ascending();
    private static final Sort ADDED = Sort.by("added").descending();

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

    @Transactional
    public List<EventShortInfoDto> getCurrentAndFutureEvents() {
        return eventRepository.findByEndDateGreaterThanEqual(LocalDate.now(), START_DATE)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<EventShortInfoDto> getEventsContaining(String title, PeriodCriteria periodCriteria) {

        if (periodCriteria == PeriodCriteria.FUTURE) {
            return eventRepository.findByTitleContainingAndStartDateAfter(
                    title, LocalDate.now(), START_DATE)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else if (periodCriteria == PeriodCriteria.CURRENT_AND_FUTURE) {
            return eventRepository.findByTitleContainingAndEndDateGreaterThanEqual(
                    title, LocalDate.now(), START_DATE)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } else {
            return eventRepository.findByTitleContaining(title, START_DATE)
                    .stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
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
    @Transactional
    public EventInfoDto getSingleEventInfo(Long eventId) {
        final EventInfoDto eventInfoDto = eventRepository.findById(eventId)
                .map(event -> new EventInfoDto(event.getId(),
                        event.getTitle(),
                        event.getStartDate(),
                        event.getEndDate(),
                        event.getDescription()))
                .orElseThrow(() -> new EventDoesntExistException(eventId));

        final List<CommentDto> comments = commentRepository.findByEventId(eventId, ADDED)
                .stream()
                .map(comment -> new CommentDto(
                        comment.getAdded(),
                        comment.getCommentText(),
                        comment.getUser().getNickname()))
                .collect(Collectors.toList());

        eventInfoDto.setComments(comments);

        return eventInfoDto;
    }

    public boolean isOwnerOrAdmin(Long eventId, String currentlyLoggedUser) {
        if (currentlyLoggedUser == null) {
            return false;
        }

        return userRepository.existsByEmailAndRolesRoleName(currentlyLoggedUser, Roles.ADMIN.getRoleName()) ||
                eventRepository.existsByIdAndUserEmail(eventId, currentlyLoggedUser);
    }

    @Transactional
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

    @Transactional
    public void addNewComment(Long eventId, NewCommentForm newCommentForm, String currentlyLoggedUser) {
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventDoesntExistException(eventId));

        final User user = userRepository.findUserByEmail(currentlyLoggedUser)
                .orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUser));

        final Comment comment = new Comment();
        comment.setUser(user);
        comment.setCommentText(newCommentForm.getComment());
        comment.setEvent(event);

        commentRepository.save(comment);
    }

    @Transactional
    public void signUpForEvent(Long eventId, String currentlyLoggedUser) {
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventDoesntExistException(eventId));
        final User user = userRepository.findUserByEmail(currentlyLoggedUser)
                .orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUser));

        event.signUp(user);
    }

    @Transactional
    public void signOffFromEvent(Long eventId, String currentlyLoggedUser) {
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventDoesntExistException(eventId));
        final User user = userRepository.findUserByEmail(currentlyLoggedUser)
                .orElseThrow(() -> new UserDoesntExistException(currentlyLoggedUser));

        event.signOff(user);

        eventRepository.save(event);
    }

    public boolean isSignedUp(Long eventId, String currentlyLoggedUser) {
        return eventRepository.existsByIdAndSignedUpForEventsEmail(eventId, currentlyLoggedUser);

    }

    @Transactional
    public List<SignedUpForEventDto> getSignedUpForEventUsers(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventDoesntExistException(eventId))
                .getSignedUpForEvents()
                .stream()
                .map(user -> new SignedUpForEventDto(
                        user.getNickname()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<EventShortInfoDto> getUserOwnerEvents(String currentlyLoggedUserEmail) {
        return eventRepository.findByUserEmail(currentlyLoggedUserEmail, START_DATE)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<EventApiInfoDto> getFutureEvents(boolean dateFilter, String after, String before) {
        if (dateFilter) {
            return eventRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqual(
                    LocalDate.parse(after), LocalDate.parse(before), START_DATE)
                    .stream()
                    .map(event -> new EventApiInfoDto(
                            event.getTitle(),
                            event.getStartDate(),
                            event.getEndDate(),
                            event.getDescription()
                    ))
                    .collect(Collectors.toList());
        } else {
            return eventRepository.findByStartDateGreaterThanEqual(LocalDate.now(), START_DATE)
                    .stream()
                    .map(event -> new EventApiInfoDto(
                            event.getTitle(),
                            event.getStartDate(),
                            event.getEndDate(),
                            event.getDescription()
                    ))
                    .collect(Collectors.toList());
        }
    }
}
