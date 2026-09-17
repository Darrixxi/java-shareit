package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserDto;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) return null;

        ItemShortDto itemDto = new ItemShortDto(
                booking.getItem().getId(),
                booking.getItem().getName()
        );

        UserDto bookerDto = new UserDto(
                booking.getBooker().getId(),
                booking.getBooker().getName(),
                booking.getBooker().getEmail()
        );

        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                itemDto,
                bookerDto,
                booking.getStatus()
        );
    }
}