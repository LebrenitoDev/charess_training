package org.charess.training.controller.security;

import org.charess.training.domain.security.Person;
import org.charess.training.domain.security.Profile;
import org.charess.training.domain.security.Status;
import org.charess.training.domain.security.User;
import org.charess.training.service.security.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/email", method = RequestMethod.GET)
    public Person getPersonByEmail(@RequestParam("email") String email) {
        return userService.getPersonByEmail(email);
    }


    @RequestMapping(value = "/nif", method = RequestMethod.GET)
    public Person getPersonByIdentifier(@RequestParam("nif") String nif) {
        log.info("====={}====", nif);
        return userService.getPersonByIdentifier(nif);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestBody User user) {
        HttpHeaders textPlainHeaders = new HttpHeaders();
        textPlainHeaders.setContentType(MediaType.TEXT_PLAIN);
        try {
            if(user == null) {
                return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
            }
            if(user.getId() == null){
                if(userService.findByUsername(user.getUsername()) != null) {
                    return new ResponseEntity<>("username", textPlainHeaders, HttpStatus.CONFLICT);
                }
                if(userService.getPersonByEmail(user.getPerson().getEmail()) != null){
                    return new ResponseEntity<>("email", textPlainHeaders, HttpStatus.CONFLICT);
                }
            }
            return new ResponseEntity(userService.register(user, user.getId()==null), HttpStatus.OK);
        } catch(UsernameNotFoundException ufe){
            return new ResponseEntity(ufe.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public List<Profile> getProfiles() {
        return userService.getProfiles();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<String> update(@RequestBody List<User> users) {
        try {
            userService.update(users);
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value = "/delete/{id}", method= RequestMethod.DELETE)
    public void delete(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
    }

    @RequestMapping(value="/password/reset", method = RequestMethod.POST)
    public ResponseEntity<?> resetPassword(@RequestBody User user){
        try {
            User current = userService.getUserById(user.getId());
            current.setPassword(user.getPassword());
            current.setStatus(Status.USER_ACTIVE.toString());
            return new ResponseEntity<>(userService.register(current, true), HttpStatus.OK);
        } catch(UsernameNotFoundException ufe){
            return new ResponseEntity<>(ufe.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping(value="/password/forgot", method = RequestMethod.POST)
    public ResponseEntity<?> forgotPassword(@RequestBody String email){
        try {
            User usr = userService.forgotPassword(email);
            return new ResponseEntity(usr, usr==null?HttpStatus.NOT_FOUND: HttpStatus.OK);
        } catch(UsernameNotFoundException ufe){
            return new ResponseEntity(ufe.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

}