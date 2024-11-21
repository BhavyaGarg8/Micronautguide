package example.micronaut;

import io.micronaut.data.annotation.EntityRepresentation;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Id;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable
@MappedEntity(value = "users")
public class User {

    @Id
//    @GeneratedValue
    private String id;

    private String name;
    private String email;


    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
