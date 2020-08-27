package rockets.model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;


class LaunchServiceProviderUnitTest {
    private LaunchServiceProvider target;


    @BeforeEach
    public void setUp() {
        String name = "AAA";
        int yearFound = 1942;
        String country = "Australia";
        target = new LaunchServiceProvider(name, yearFound, country);
    }

    @DisplayName("should return true when input the valid parameters for constructor")
    @Test
    public void shouldCreateLaunchServiceProviderFromColonSeperated() {
        String name = "SpaceX";
        int yearFound = 2002;
        String country = "America";
        target = new LaunchServiceProvider(name, yearFound, country);
        assertTrue(name.equals(target.getName()));
        assertTrue(String.valueOf(yearFound).equals(String.valueOf(target.getYearFounded())));
        assertTrue(country.matches(target.getCountry()));
    }

    @DisplayName("should throw exception when pass the invalid parameters for constructor")
    @Test
    public void shouldThrowExceptionWhenPassInvalidInputToConstructor(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()
                -> new LaunchServiceProvider("SpaceXSpaceXSpaceXSpaceXSpaceX",2002,"America"));
        assertEquals("Year must be an integer, name and country must only contain character, less than 30 in length", exception.getMessage());

        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, ()
                -> new LaunchServiceProvider("",2002,"America"));
        assertEquals("Year must be an integer, name and country must only contain character, less than 30 in length", exception.getMessage());

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, ()
                -> new LaunchServiceProvider("SpaceX",2020,"America"));
        assertEquals("Year must be an integer, name and country must only contain character, less than 30 in length", exception.getMessage());

        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, ()
                -> new LaunchServiceProvider("SpaceX",2002,"AmericaAmericaAmericaAmericaAmericaAmerica"));
        assertEquals("Year must be an integer, name and country must only contain character, less than 30 in length", exception.getMessage());
    }

    @DisplayName("should accept when set valid input to setHeadQuarter function")
    @Test
    public void shouldAcceptWhenSetValidInputToSetHeadQuarter(){
        target.setHeadquarters("SpaceX");
        assertEquals("SpaceX", target.getHeadquarters());
    }

    @DisplayName("should throw exception when set invalid input to setHeadQuarter function")
    @Test
    public void shouldThrowExceptionWhenSetInvalidInputToSetHeadQuarter() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setHeadquarters("SpaceXSpaceXSpaceXSpaceXSpaceX"));
        assertEquals("headquarter must be under 30 characters", exception.getMessage());
    }

    @DisplayName("should throw exception when pass an empty headquarter to setHeadquarters function")
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    public void shouldThrowExceptionWhenSetHeadQuaterToEmpty(String headquarters){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> target.setHeadquarters(headquarters));
        assertEquals("headquarter cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when pass null to setHeadquarters function")
    @Test
    public void shouldThrowExceptionWhenSetHeadquartersToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setHeadquarters(null));
        assertEquals("headquarter cannot be null or empty", exception.getMessage());
    }

    @DisplayName("should throw exception when pass null to setRockets function")
    @Test
    public void shouldThrowExceptionWhenSetRocketsToNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> target.setRockets(null));
        assertEquals("rockets cannot be null or empty", exception.getMessage());
    }
}
