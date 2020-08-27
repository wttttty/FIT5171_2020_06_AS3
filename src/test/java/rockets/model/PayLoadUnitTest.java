package rockets.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PayLoadUnitTest {

    @BeforeEach
    public void setUp() {
    }


    @DisplayName("should return true when pass the valid parameters for constructor")
    @Test
    public void shouldReturnTrueWhenPassValidInputToConstructor() {
        String type = "satellite";
        String name = "sputnik 1";
        LaunchServiceProvider manufacturer = new LaunchServiceProvider("OKB", 1957, "Russia");
        PayLoad payload = new PayLoad(type, name, manufacturer);
        assertNotNull(payload);
    }

    @DisplayName("should throw exception when given null manufacturer to constructor")
    @Test
    public void shouldThrowExceptionWhenNoManufacturerGiven() {
        String type = "satellite";
        String name = "sputnik 1";
        assertThrows(NullPointerException.class, () -> new PayLoad(type, name, null));
    }


    @DisplayName("should throw exception when pass the invalid parameters for constructor")
    @Test
    public void shouldThrowExceptionWhenPassInvalidInputToConstructor() {
        LaunchServiceProvider manufacturer = new LaunchServiceProvider("SpaceX", 2002, "USA");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()
                -> new PayLoad("111", "Vanguard 1", manufacturer));
        assertEquals("Type contain only alphabetic, Name under 30 characters & contain only alphanumeric", exception.getMessage());

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, ()
                -> new PayLoad("Satellite", "Vanguard 1!!!", manufacturer));
        assertEquals("Type contain only alphabetic, Name under 30 characters & contain only alphanumeric", exception.getMessage());
}

    @DisplayName("should throw exception when pass empty weight and unit to setMass function")
    @Test
    public void shouldThrowExceptionWhenSetWeightAndUnitToNull() {
        String type = "satellite";
        String name = "sputnik 1";
        LaunchServiceProvider  manufacturer = new LaunchServiceProvider("OKB", 1957, "Russia");
        PayLoad payload = new PayLoad(type,name,manufacturer);
        NullPointerException exception = assertThrows(NullPointerException.class, () -> payload.setMass(null,null));
        assertEquals("weight and unit cannot be null or empty", exception.getMessage());

        NullPointerException exception1 = assertThrows(NullPointerException.class, () -> payload.setMass("1.2",null));
        assertEquals("weight and unit cannot be null or empty", exception.getMessage());

        NullPointerException exception2 = assertThrows(NullPointerException.class, () -> payload.setMass(null,"kg"));
        assertEquals("weight and unit cannot be null or empty", exception.getMessage());
    }



    @DisplayName("should throw exception when pass invalid weight and unit to setMass function")
    @Test
    public void shouldThrowExceptionWhenPassInvalidWeightAndUnitToSetMass(){
        String type = "satellite";
        String name = "sputnik 1";
        LaunchServiceProvider  manufacturer = new LaunchServiceProvider("OKB", 1957, "Russia");
        PayLoad payload = new PayLoad(type,name,manufacturer);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> payload.setMass("aaa", "kg"));
        assertEquals("Weight must be Integer or Double, unit contain only characters", exception.getMessage());

        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> payload.setMass("1.2","!!!"));
        assertEquals("Weight must be Integer or Double, unit contain only characters", exception1.getMessage());

        payload.setMass("1.2", "kg");
        assertEquals("1.2 kg",payload.getMass());
    }

}
