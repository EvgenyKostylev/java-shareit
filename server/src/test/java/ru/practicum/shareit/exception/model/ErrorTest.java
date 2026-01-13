package ru.practicum.shareit.exception.model;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ErrorTest {
    private final JacksonTester<Error> tester;

    @Test
    public void testError() throws Exception {
        Error error = new Error("error", "description");

        JsonContent<Error> result = tester.write(error);

        assertThat(result).extractingJsonPathStringValue("$.error").isEqualTo("error");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }
}