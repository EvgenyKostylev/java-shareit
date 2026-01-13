package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingOutDtoTest {
    private final JacksonTester<BookingOutDto> tester;

    @Test
    public void testBookingOutDto() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BookingOutDto bookingOutDto = new BookingOutDto();

        bookingOutDto.setId(1L);
        bookingOutDto.setStart(now);
        bookingOutDto.setEnd(now);
        bookingOutDto.setStatus(Status.WAITING);

        JsonContent<BookingOutDto> result = tester.write(bookingOutDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(now.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(now.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(Status.WAITING.toString());
    }
}