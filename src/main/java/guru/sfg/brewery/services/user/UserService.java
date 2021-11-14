package guru.sfg.brewery.services.user;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    User findUserById(Long id);
    List<User> saveUserList(List<User> userList);

}
