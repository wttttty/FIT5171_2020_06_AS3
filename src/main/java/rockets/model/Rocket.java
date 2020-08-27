package rockets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.neo4j.ogm.annotation.Relationship;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;
import static org.neo4j.ogm.annotation.Relationship.OUTGOING;

public class Rocket extends Entity {


    private Set<PayLoad> payloads;
    private String name;
    private String variation;
    private String country;

    private LaunchServiceProvider manufacturer;

    private String massToLEO;

    private String massToGTO;

    private String massToOther;

    public Set<PayLoad> getPayloads() {
        return payloads;
    }

    public void setPayloads(Set<PayLoad> payloads) {

        notNull(payloads, "payloads cannot be null or empty");
        this.payloads = payloads;
    }

    @Relationship(type = "PROVIDES", direction = OUTGOING)
    @JsonIgnore
    private Set<Launch> launches;

    public Set<Launch> getLaunches() {
        return launches;
    }

    public void setLaunches(Set<Launch> launches) { this.launches = launches; }

    /**
     * All parameters shouldn't be null.

     * @param name
     * @param country
     * @param manufacturer
     */
    public Rocket(String name,String variation, String country,LaunchServiceProvider manufacturer) {

        notNull(name);
        notNull(country);
        notNull(manufacturer);
//        if(name == null) {
//            throw new NullPointerException("rocket name should not be null");
//        }
//
//        //notNull(country);
//        if(country == null) {
//            throw new NullPointerException("country should not be null");
//        }
//
//        //notNull(manufacturer);
//        if(manufacturer == null) {
//            throw new NullPointerException("manufacturer should not be null");
//        }

        this.name = name;
        this.country = country;
        this.manufacturer = manufacturer;
        if(
                haveCharacters(name)
                        &&onlyCharacter(country)
                        //&&onlyCharacter(manufacturer)
                        &&isInRange(name,0,30)
                        //&&onlyCharacter(variation)
                        &&isInRange(country,0,30)
            //&&isInRange(manufacturer,30)
        )
        {
            this.name = name;
            this.variation = variation;
            this.country = country;
            this.manufacturer = manufacturer;
        }
        else {
            throw new IllegalArgumentException("\"name should under 30 characters\\n and contains alphabetic characters\" +\n" +
                    "                    \" variation should under 30 characters\"+\n" +
                    "                       \" manufacturer should not be null \"+\n" +
                    "                    \" country should under 30 characters, contain only alphabetic characters \"+\n");
        }
        this.launches = new LinkedHashSet<>();
    }

    public boolean haveCharacters(String name) {
        return name.matches(".*[a-zA-z].*");

    }





    public boolean isInRange(String input, int i, int j) {return input.length()>i && input.length()<j;}

    public boolean onlyCharacter(String input) {
        return input.matches("[a-zA-Z]+");
    }


    public boolean isInteger(String input){
        return input.matches("[0-9]+");
    }

    public String getName() {
        return name;
    }

    public String getVariation() {
        return variation;
    }

    public String getCountry() {
        return country;
    }


    public LaunchServiceProvider getManufacturer() {

        return manufacturer;
    }

    public String getMassToLEO() {
        return massToLEO;
    }

    public String getMassToGTO() {
        return massToGTO;
    }

    public String getMassToOther() {
        return massToOther;
    }

    public boolean LEOGTOOTHERIsValid(String input) {

        //whether  contains
        return input.matches(".*[0-9].*") ;

    }


    // cici edited
    public void setMassToLEO(String massToLEO) {
        notBlank(massToLEO, "MassToLEO cannot be null or empty");
        if ((LEOGTOOTHERIsValid(massToLEO) == false)) {
            throw new IllegalArgumentException("MassToLEO should contain numbers");
        }
        else{
            this.massToLEO = massToLEO;
        }

    }

    // cici edited
    public void setMassToGTO(String massToGTO) {
        notBlank(massToGTO, "MassToGTO cannot be null or empty");
        if ((LEOGTOOTHERIsValid(massToGTO) == false)) {
            throw new IllegalArgumentException("MassToGTO should contain numbers");
        }
        else {
            this.massToGTO = massToGTO;
        }
    }

    // cici edited
    public void setMassToOther(String massToOther) {
        notBlank(massToOther, "MassToOther cannot be null or empty");
        if ((LEOGTOOTHERIsValid(massToOther) == false)) {
            throw new IllegalArgumentException("MassToOther should contain numbers");
        }
        else {
            this.massToOther = massToOther;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rocket rocket = (Rocket) o;
        return Objects.equals(name, rocket.name) &&
                Objects.equals(variation, rocket.variation) &&
                Objects.equals(country, rocket.country) &&
                Objects.equals(manufacturer, rocket.manufacturer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name,variation, country, manufacturer);
    }

    @Override
    public String toString() {
        return "Rocket{" +
                "name='" + name + '\'' +
                ", variation='" + variation + '\'' +
                ", country='" + country + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", massToLEO='" + massToLEO + '\'' +
                ", massToGTO='" + massToGTO + '\'' +
                ", massToOther='" + massToOther + '\'' +
                '}';
    }
}
