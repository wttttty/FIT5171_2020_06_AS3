package rockets.model;

import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

public class RocketFamily extends Entity{
    private String name;

    private Set<Rocket> rockets;

    public RocketFamily(String name) {

        if (onlyCharacter(name) && isInRange(name,0,20) ) {
            this.name = name;
            rockets = Sets.newLinkedHashSet();
        }
        else {
            throw new IllegalArgumentException("Rocket family name should only contains alphabetic characters and  under 20 characters");
        }
    }

    public boolean isInRange(String input, int i, int j) {return input.length()>i && input.length()<j;}


    public boolean onlyCharacter(String name) {
        return name.matches("[a-zA-Z]+");
    }

    public String getName() {
        return name;
    }

    public Set<Rocket> getRockets() {
        return rockets;
    }

    public void setRockets(Set<Rocket> rockets) {
        notNull(rockets, "rockets cannot be null or empty");
        this.rockets = rockets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RocketFamily that = (RocketFamily) o;
        return
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }
}
