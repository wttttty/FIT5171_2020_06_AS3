package rockets.model;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.ParameterizedTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class RocketUnitTest {

    private Rocket bfr;


    @BeforeEach
    public void setUp() {
        String name = "BFR";
        String country = "USA";
        String variation ="ccc";
        LaunchServiceProvider  manufacturer = new LaunchServiceProvider("SpaceX", 2002, "USA");
        bfr = new Rocket(name,variation,country,manufacturer);
    }

    @AfterEach
    public void tearDown() {
    }

    @DisplayName("should create rocket successfully when given right parameters to constructor")
    @Test
    public void shouldConstructRocketObject() {
        assertNotNull(bfr);
    }

    @DisplayName("should throw exception when given null manufacturer to constructor")
    @Test
    public void shouldThrowExceptionWhenNoManufacturerGiven() {
        String name = "BFR";
        String country = "USA";
        String variation ="ccc";
        assertThrows(NullPointerException.class, () -> new Rocket(name,variation, country, null));
    }

    @DisplayName("should set rocket massToLEO value")
    @ValueSource(strings = {"10000", "15000"})
    @ParameterizedTest
    public void shouldSetMassToLEOWhenGivenCorrectValue(String massToLEO) {
        bfr.setMassToLEO(massToLEO);
        assertEquals(massToLEO, bfr.getMassToLEO());
    }

    @DisplayName("should throw exception when set massToLEO to null")
    @Test
    public void shouldThrowExceptionWhenSetMassToLEOToNull() {
        assertThrows(NullPointerException.class, () -> bfr.setMassToLEO(null));
    }

    @DisplayName("should throw exception when pass a invalid MassToLEO to setMassToLEO function")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetMassToLEOToInvalid(String massToLEO) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bfr.setMassToLEO("..//~"));
        assertEquals("MassToLEO should contain numbers", exception.getMessage());
        bfr.setMassToLEO("600 to");
        assertEquals("600 to",bfr.getMassToLEO());
    }

    @DisplayName("should throw exceptions when pass a null MassToGTO to setMassToGTO function")
    @Test
    public void shouldThrowExceptionWhenSetMassToGTOToNull() {
        assertThrows(NullPointerException.class, () -> bfr.setMassToGTO(null));
    }

    @DisplayName("should throw exception when pass a empty MassToGTO to setMassToLEO function")

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetMassToGTOToEmpty(String massToGTO) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bfr.setMassToGTO(massToGTO));
        assertEquals("MassToGTO cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when pass a invalid MassToGTO to setMassToGTO function")

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetMassToGTOToInvalid(String massToGTO) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bfr.setMassToGTO("..//~"));
        assertEquals("MassToGTO should contain numbers", exception.getMessage());

        bfr.setMassToGTO("600 to");
        assertEquals("600 to",bfr.getMassToGTO());
    }

    @DisplayName("should throw exceptions when pass a null MassToOther to setMassToOther function")
    @Test

    public void shouldThrowExceptionWhenSetMassToOtherToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> bfr.setMassToOther(null));
        assertEquals("MassToOther cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when pass a empty MassToOther to setMassToOther function")

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetMassToOtherToEmpty(String massToOther) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bfr.setMassToOther(massToOther));
        assertEquals("MassToOther cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when pass a invalidMassToOther to setMassToOther function")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetMassToOtherToInvalid(String massToOther) {
        bfr.setMassToOther("600 to");
        assertEquals("600 to",bfr.getMassToOther());

        bfr.setMassToOther("600.12");
        assertEquals("600.12",bfr.getMassToOther());
    }

    @DisplayName("should throw exception when pass null to setPayLoads function")
    @Test
    public void shouldThrowExceptionWhenSetPayLoadsToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> bfr.setPayloads(null));
        assertEquals("payloads cannot be null or empty", exception.getMessage());
    }

}