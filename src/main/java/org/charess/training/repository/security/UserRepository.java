package org.charess.training.repository.security;

import org.charess.training.domain.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    @Query("select u from User u where u.person.id <> 1 and u.retired <> 1 order by u.person.id desc ")
    List<User> find();

    @Query("select u from User u where u.person.email =?1 ")
    User findByPersonEmail(String email);

    @Modifying
    @Query("update User u set u.retired = ?1 , u.retiredBy =?3 where u.id =?2")
    void deleteUser(boolean b,Integer id, Integer currentUser);
}
