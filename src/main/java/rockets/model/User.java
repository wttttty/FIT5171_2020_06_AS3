package rockets.model;

import org.neo4j.ogm.annotation.NodeEntity;

import java.util.Objects;
import java.util.regex.Pattern;


import static org.apache.commons.lang3.Validate.notBlank;

@NodeEntity
public class User extends Entity {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        notBlank(firstName, "first name cannot be null or empty");

        if (NameIsValid(firstName)== false){
            throw new IllegalArgumentException("First name should under 30 characters and contains only characters");
        }
        else this.firstName = firstName;

    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        notBlank(lastName, "last name cannot be null or empty");

        if ((NameIsValid(lastName)== false)){
            throw new IllegalArgumentException("Last name should under 30 characters and contains only characters");
        }
        else
        {this.lastName = lastName;}
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {

        notBlank(email, "email cannot be null or empty");
        if ((EmailIsValid(email) == false)) {
            throw new IllegalArgumentException("Email should be in a validated format");
        }
        else this.email = email;
    }
    public boolean NameIsValid(String name) {

        int a = name.matches(".*[0-9].*") ? 1 : 0;

        int b = name.matches("[a-zA-Z]+") ? 1 : 0;

        int l = name.length();

        if (a==1 || b==0 || l >= 30 ) {
            return false;
        }
        return true;

    }
    public boolean PasswordIsValid(String password) {


        int a = password.matches(".*[a-zA-z].*")? 1:0;
        int b = password.matches(".*[0-9].*")? 1:0;

        int l = password.length();
        if (a ==0||b==0|| l < 6 || l > 15 ) {
            return false;
        }
        else {
            return true;
        }
    }

    public static boolean EmailIsValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,}$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {

        notBlank(password, "password cannot be null or empty");
        if ((PasswordIsValid(password) == false)) {
            throw new IllegalArgumentException("Password should be in a validated format");
        }
        else{
            this.password = password;
        }
    }

    // match the given password against user's password and return the result
    public boolean isPasswordMatch(String password) {
        return this.password.equals(password.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
