package rockets.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import org.neo4j.ogm.annotation.CompositeIndex;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.Validate.*;
import static org.neo4j.ogm.annotation.Relationship.OUTGOING;
import static rockets.model.Launch.LaunchOutcome.SUCCESSFUL;

@NodeEntity
@CompositeIndex(properties = {"name", "yearFounded", "country"}, unique = true)
public class LaunchServiceProvider extends Entity {
    @Property(name = "name")
    private String name;

    @Property(name = "yearFounded")
    private int yearFounded;

    @Property(name = "country")
    private String country;

    @Property(name = "headquarters")
    private String headquarters;

    @Relationship(type = "MANUFACTURES", direction= OUTGOING)
    @JsonIgnore
    private Set<Rocket> rockets;

    private BigDecimal totalRevenue;
    private int percentage;
    private int Dominant;

    public boolean onlyCharacter(String input) {
        return input.matches("[a-zA-Z\\s]+");
    }

    public boolean isInteger(String input){
        return input.matches("[0-9]+");
    }

    public boolean isInRange(String input, int i, int j) {return input.length()>i && input.length()<j;}

    public boolean validYear(int year) {
        Date date = new Date();
        String strDateFormat = "yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        //System.out.println(sdf);
        if(year>1900 && year<Integer.valueOf(sdf.format(date))) return true;
        else return false;
    }

    public LaunchServiceProvider(String name, int yearFounded, String country) {

        notNull(name);
        notNull(country);
        notNull(yearFounded);

        if(onlyCharacter(name)
                && onlyCharacter(country)
                && isInteger(String.valueOf(yearFounded))
                && validYear(yearFounded)
                && isInRange(name,0,30)
                && isInRange(country, 0,30)){
            this.name = name;
            this.yearFounded = yearFounded;
            this.country = country;

            rockets = Sets.newLinkedHashSet();
        }
        else {throw new IllegalArgumentException("Year must be an integer, name and country must only contain character, less than 30 in length");}

    }

    public String getName() {
        return name;
    }

    public int getYearFounded() {
        return yearFounded;
    }

    public String getCountry() {
        return country;
    }

    public String getHeadquarters() {
        return headquarters;
    }


    public Set<Rocket> getRockets() { return rockets; }



    public void setHeadquarters(String headquarters) {
        notBlank(headquarters, "headquarter cannot be null or empty" );
        if (isInRange(headquarters,0, 30)) {
            this.headquarters = headquarters;
        }
        else throw new IllegalArgumentException("headquarter must be under 30 characters");

    }

    public void setRockets(Set<Rocket> rockets) {
        notNull(rockets, "rockets cannot be null or empty" );
        this.rockets = rockets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LaunchServiceProvider that = (LaunchServiceProvider) o;
        return yearFounded == that.yearFounded &&
                Objects.equals(name, that.name) &&
                Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, yearFounded, country);
    }

    public BigDecimal getTotalRevenue(int year) {
        BigDecimal totalRevenue = BigDecimal.valueOf(  0.00);
        Set<Rocket> set = getRockets();
        for (Rocket ro :set){
              Set<Launch> set1 =ro.getLaunches();
            for (Launch st : set1) {
                if (st.getLaunchDate().getYear() == year) {
                    if (st.getPrice().compareTo(BigDecimal.ZERO) > 0)
                    {
                    totalRevenue =totalRevenue.add(st.getPrice());
                }}
            }
        }

        return totalRevenue;
    }

    public int getPercentage() {
        int succ=0;
        int size =0;
        Set<Rocket> set = getRockets();
        for (Rocket ro :set)
        {
            Set<Launch> set1 = ro.getLaunches();
            size+=set1.size();
            for (Launch st : set1)
            {
                if (st.getLaunchOutcome() == SUCCESSFUL)
                {
                    succ += 1;
                }
            }
        }

        if (size>0)
        {
            percentage = succ / size;
        }
        return percentage;
    }



    public int getDominant(String orbit) {
        Set<Rocket> set = getRockets();
        int Dominant =0;
        for (Rocket ro :set)
        {
            Set<Launch> set1 = ro.getLaunches();

            for (Launch st : set1) {
                //System.out.println(st.getOrbit());
                if (st.getOrbit() == orbit) {
                    Dominant += 1;
                }
            }
        }
        System.out.println(Dominant);
        return Dominant;
    }


}
