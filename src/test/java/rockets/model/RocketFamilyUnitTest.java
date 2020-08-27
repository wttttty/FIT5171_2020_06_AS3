package rockets.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RocketFamilyUnitTest {

    private RocketFamily target;


    @BeforeEach
    public void setUp() {

        String name="Angara";
        target = new RocketFamily(name);
    }

    @DisplayName("should return true when input the valid parameters for constructor")
    @Test
    public void shouldReturnTrueWhenRocketFamilyHasSameName() {
        String name1 = "Angara";

        target = new RocketFamily(name1);
        assertTrue(name1.equals(target.getName()));
    }

    @DisplayName("should throw exception when input the invalid parameters for constructor")
    @Test public void testValidationInConstructor() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new RocketFamily("1234"));
        assertEquals("Rocket family name should only contains alphabetic characters and  under 20 characters", exception.getMessage());

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> new RocketFamily("..??//"));
        assertEquals("Rocket family name should only contains alphabetic characters and  under 20 characters", exception1.getMessage());


        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> new RocketFamily("aabbccddeeffgghhkkllaaammnnbbvvv"));
        assertEquals("Rocket family name should only contains alphabetic characters and  under 20 characters", exception2.getMessage());
    }

    @DisplayName("should throw exception when pass null to setRockets function")
    @Test
    public void shouldThrowExceptionWhenSetRocketsToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setRockets(null));
        assertEquals("rockets cannot be null or empty", exception.getMessage());
    }


}
