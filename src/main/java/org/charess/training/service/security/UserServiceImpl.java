package org.charess.training.service.security;

import org.apache.commons.lang3.RandomStringUtils;
import org.charess.training.controller.security.UserController;
import org.charess.training.domain.security.*;
import org.charess.training.repository.security.PersonRepository;
import org.charess.training.repository.security.ProfileRepository;
import org.charess.training.repository.security.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service("userService")
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private UserRepository userRepository;
    private PersonRepository personRepository;
    private PasswordEncoder passwordEncoder;
    private ProfileRepository profileRepository;
    private JavaMailSender javaMailSender;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PersonRepository personRepository, PasswordEncoder passwordEncoder,
                           ProfileRepository profileRepository, JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileRepository = profileRepository;
        this.javaMailSender = javaMailSender;
    }

    public Audit inject(Audit audit){
        if(audit==null)
            return audit;
        if(audit.getId() == null){
            audit.setCreator(getCurrentUser().getId());
            audit.setCreated(LocalDateTime.now());
        } else {
            audit.setEditor(getCurrentUser().getId());
            audit.setEdited(LocalDateTime.now());
        }
        return audit;
    }

    public List<Profile> getProfiles(){
        return profileRepository.findAll().stream().filter(profile -> profile.getId()>1).collect(Collectors.toList());
    }


    /** user */

    public User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        User user = null;
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User){
                String username = ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
                user = userRepository.findByUsername(username);
            }
        }
        return user;
    }

    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public void deleteUser(Integer id){
        userRepository.deleteUser(true,id);;
    }

    public User register(User user, boolean encodePassword) {
        if (user.getPassword() == null && user.getId() != null) {
            userRepository.findById(user.getId()).ifPresent(u -> user.setPassword(u.getPassword()));
        }
        Person person = user.getPerson();
        if (person != null) {
            if (user.getPassword() == null) {
                user.setPassword(user.getUsername());
                user.setStatus(Status.USER_PENDING.toString());
            }
            person = personRepository.save(person);
        }
        if (encodePassword) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (getCurrentUser() != null && user.getId() == null)
            user.setStatus(Status.USER_PENDING.toString());
        if (getCurrentUser() != null && user.getActivatedBy() == null && user.getStatus().equals(Status.USER_ACTIVE.toString())) {
            user.setActivatedBy(getCurrentUser().getId());
            user.setActivatedDate(LocalDateTime.now());
        }
        if (user.getId() != null && getCurrentUser() != null && user.getStatus().equals(Status.USER_PENDING.toString())) {
            user.setPassword(passwordEncoder.encode(user.getUsername()));
        }
        user.setPerson(person);
        return userRepository.save(user);
    }

    public List<User> getUsers(){
        return userRepository.find();
    }

    public void update(List<User> users){
        for(User u: users){
            boolean encodePassword = u.getStatus().equals(Status.USER_PENDING.toString());
            u.setPassword(encodePassword?u.getUsername():u.getPassword());
            register(u, encodePassword);
        }
    }

    public User getUserById(Integer id){
        return userRepository.findById(id).get();
    }

    public User forgotPassword(String email){
        User usr = userRepository.findByPersonEmail(email);
        if (usr == null || !Status.USER_ACTIVE.toString().equals(usr.getStatus()))
            return null;
        usr.setStatus(Status.USER_PENDING.toString());
        String temporaryPassword = RandomStringUtils.randomAlphabetic(8);
        usr.setPassword(temporaryPassword);
        usr = register(usr, true);

        String subject = "CHARESS Training App - Changement de mot de passe";
        String support = "charess@charess.org";
        String body = "Salut " + usr.getPerson().getFirstName()+"," +
                "\n\nVotre mot de passe vient d'être changé en: " + temporaryPassword +
                "\n\n\nMerci de ne pas répondre à ce message." +
                "\n\nSupport Technique," +
                "\nCentre Haïtien pour le Renforcement des Systèmes de Santé - (CHARESS)" +
                "\n(+509) 3838-0119" +
                "\nformation@charess.org" +
                "\nwww.charess.org";

        final SimpleMailMessage smm = new SimpleMailMessage();
        smm.setSubject(subject);
        smm.setFrom(support);
        smm.setText(body);
        smm.setTo(email);
        javaMailSender.send(smm);

        return usr;
    }



    /** person */

    public Person getPersonByEmail(String email){
        return personRepository.findByEmail(email);
    }

    public Person getPersonByIdentifier(String nif){
        return personRepository.findByIdentifier(nif);
    }
}
