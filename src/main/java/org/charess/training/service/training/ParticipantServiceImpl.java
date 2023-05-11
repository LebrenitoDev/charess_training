package org.charess.training.service.training;

import org.charess.training.domain.security.Audit;
import org.charess.training.domain.security.Person;
import org.charess.training.domain.security.Place;
import org.charess.training.domain.security.Status;
import org.charess.training.domain.training.Participant;
import org.charess.training.domain.training.PartnerTrainingParticipants;
import org.charess.training.domain.training.Training;
import org.charess.training.repository.security.PersonRepository;
import org.charess.training.repository.training.ParticipantRepository;
import org.charess.training.service.security.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service("participantService")
public class ParticipantServiceImpl implements ParticipantService {

    private ParticipantRepository participantRepository;
    private PersonRepository personRepository;
    private UserService userService;
    private final Logger log = LoggerFactory.getLogger(ParticipantServiceImpl.class);

    @Autowired
    public ParticipantServiceImpl(ParticipantRepository participantRepository, UserService userService, PersonRepository personRepository) {
        this.participantRepository = participantRepository;
        this.userService = userService;
        this.personRepository = personRepository;
    }

    public String declare(PartnerTrainingParticipants partnerTrainingParticipants) {
        Training training = partnerTrainingParticipants.getTraining();
        if(training.getStatus().equals(Status.TRAINING_REJECTED.toString()) || training.getStatus().equals(Status.TRAINING_COMPLETED.toString()) ||
            training.getStatus().equals(Status.TRAINING_UNCOMPLETED.toString()))
            return "";

        Place partner = partnerTrainingParticipants.getPartner();
        String editorName = userService.getUserById(userService.getCurrentUser().getId()).getPerson().getFullname();
        Audit audit = new Audit(LocalDateTime.now(), userService.getCurrentUser().getId(),editorName);
        List<Participant> participants = participantRepository.findByTrainingAndPartner(training, partner);

        if (training == null || partner == null)
            return null;

        if(participants.size() > 0) {
            participantRepository.deleteAll(participants);
            participantRepository.flush();
            participants = new ArrayList<>();
        }

        for(Person person: partnerTrainingParticipants.getParticipants()){
            personRepository.save(person);
            participants.add(new Participant(person, training, partner, audit));
        }

        participantRepository.saveAll(participants);

        return new String();
    }

}
