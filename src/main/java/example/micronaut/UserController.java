package example.micronaut;


import io.micronaut.http.annotation.*;
import io.micronaut.serde.annotation.SerdeImport;

import java.util.List;
import java.util.Optional;

@SerdeImport(User.class)
@Controller("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Get("/{id}")
    public Optional<User> getUser(@PathVariable String id) {
        return userRepository.findById(id);
    }

    @Get("/")
    public List<User> listUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Post("/")
    public User createUser(@Body User user) {
        return userRepository.save(user);
    }

    @Put("/{id}")
    public User updateUser(@PathVariable String id, @Body User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setEmail(updatedUser.getEmail());
                    return userRepository.update(user);
                })
                .orElse(null);
    }

    @Delete("/{id}")
    public void deleteUser(@PathVariable String id) {
        userRepository.deleteById(id);
    }
}
