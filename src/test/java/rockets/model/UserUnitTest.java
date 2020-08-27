package rockets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UserUnitTest {
    private User target;

    @BeforeEach
    public void setUp() {
        target = new User();
    }


    @DisplayName("should throw exception when pass a empty email address to setEmail function")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetEmailToEmpty(String email) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setEmail(email));
        assertEquals("email cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when pass a empty first name to setFirstName function")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetFirstNameToEmpty(String fn) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setFirstName(fn));
        assertEquals("first name cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when pass a empty last name to setLastName function")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetLastNameToEmpty(String ln) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setLastName(ln));
        assertEquals("last name cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when pass null to setEmail function")
    @Test
    public void shouldThrowExceptionWhenSetEmailToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setEmail(null));
        assertEquals("email cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exceptions when pass a null password to setPassword function")
    @Test
    public void shouldThrowExceptionWhenSetPasswordToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setPassword(null));
        assertEquals("password cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exceptions when pass a null first name to setFirstName function")
    @Test
    public void shouldThrowExceptionWhenSetFirstNameToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setFirstName(null));
        assertEquals("first name cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exceptions when pass a null last name to setFirstName function")
    @Test
    public void shouldThrowExceptionWhenSetLastNameToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setLastName(null));
        assertEquals("last name cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should return true when two users have the same email")
    @Test
    public void shouldReturnTrueWhenUsersHaveSameEmail() {
        String email = "abc@example.com";
        target.setEmail(email);
        User anotherUser = new User();
        anotherUser.setEmail(email);
        assertTrue(target.equals(anotherUser));
    }


    @DisplayName("should return false when two users have different emails")
    @Test
    public void shouldReturnFalseWhenUsersHaveDifferentEmails() {
        target.setEmail("abc@example.com");
        User anotherUser = new User();
        anotherUser.setEmail("def@example.com");
        assertFalse(target.equals(anotherUser));
    }

    @DisplayName("should throw exception when set invalid input to setFirstName function")
    @Test
    public void shouldThrowExceptionWhenSetInvalidInputToSetFirstName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setFirstName("111"));
        assertEquals("First name should under 30 characters and contains only characters", exception.getMessage());

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> target.setFirstName("!!!"));
        assertEquals("First name should under 30 characters and contains only characters", exception.getMessage());

        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, () -> target.setFirstName("nam1"));
        assertEquals("First name should under 30 characters and contains only characters", exception.getMessage());

        IllegalArgumentException exception4 = assertThrows(IllegalArgumentException.class, () -> target.setFirstName("nam!"));
        assertEquals("First name should under 30 characters and contains only characters", exception.getMessage());
    }

    @DisplayName("should throw exception when set invalid input to setLastName function")
    @Test
    public void shouldThrowExceptionWhenSetInvalidInputToSetLastName() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setLastName("111"));
        assertEquals("Last name should under 30 characters and contains only characters", exception.getMessage());

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> target.setLastName("!!!"));
        assertEquals("Last name should under 30 characters and contains only characters", exception.getMessage());

        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, () -> target.setLastName("nam1"));
        assertEquals("Last name should under 30 characters and contains only characters", exception.getMessage());

        IllegalArgumentException exception4 = assertThrows(IllegalArgumentException.class, () -> target.setLastName("nam!"));
        assertEquals("Last name should under 30 characters and contains only characters", exception.getMessage());
    }
}