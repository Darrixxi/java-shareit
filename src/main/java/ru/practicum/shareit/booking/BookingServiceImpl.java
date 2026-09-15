package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto create(Long bookerId, BookingCreateDto bookingDto) {
        User booker = getUserById(bookerId);

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + bookingDto.getItemId() + " не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Некорректные даты бронирования");
        }

        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь с id=" + userId + " не является владельцем вещи");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        getUserById(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не найдено"));

        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();

        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException("Бронирование с id=" + bookingId + " не найдено");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getAllByBooker(Long bookerId, String state) {
        getUserById(bookerId);

        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId);
        return filterAndMapBookings(bookings, state);
    }

    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, String state) {
        getUserById(ownerId);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
        return filterAndMapBookings(bookings, state);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
    }

    private List<BookingDto> filterAndMapBookings(List<Booking> bookings, String state) {
        State stateEnum = parseState(state);
        LocalDateTime now = LocalDateTime.now();

        return bookings.stream()
                .filter(booking -> matchesState(booking, stateEnum, now))
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private State parseState(String state) {
        try {
            return State.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }

    private boolean matchesState(Booking booking, State state, LocalDateTime now) {
        switch (state) {
            case CURRENT:
                return booking.getStatus() == BookingStatus.APPROVED
                        && booking.getStart().isBefore(now)
                        && booking.getEnd().isAfter(now);
            case PAST:
                return booking.getEnd().isBefore(now);
            case FUTURE:
                return booking.getStart().isAfter(now);
            case WAITING:
                return booking.getStatus() == BookingStatus.WAITING;
            case REJECTED:
                return booking.getStatus() == BookingStatus.REJECTED;
            case ALL:
            default:
                return true;
        }
    }
}