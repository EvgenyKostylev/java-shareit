package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingInDtoTest {
    private final JacksonTester<BookingInDto> tester;

    @Test
    public void testBookingInDto() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BookingInDto bookingInDto = new BookingInDto();

        bookingInDto.setStart(now);
        bookingInDto.setEnd(now);
        bookingInDto.setItemId(1L);

        JsonContent<BookingInDto> result = tester.write(bookingInDto);

        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(now.format(formatter));
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(now.format(formatter));
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
    }
}