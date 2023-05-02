package org.charess.training.service.security;

import org.charess.training.domain.security.Audit;
import org.charess.training.domain.security.Person;
import org.charess.training.domain.security.Profile;
import org.charess.training.domain.security.User;

import java.util.List;

public interface UserService {

    User getCurrentUser();
    User findByUsername(String username);
    User register(User user, boolean encodePassword);
    List<User> getUsers();
    void update(List<User> users);
    void deleteUser(Integer id);
    User getUserById(Integer id);
    User forgotPassword(String email);


    Audit inject(Audit audit);
    List<Profile> getProfiles();


    Person getPersonByEmail(String email);
    Person getPersonByIdentifier(String nif);

}
