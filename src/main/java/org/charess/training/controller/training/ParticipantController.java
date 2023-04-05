package org.charess.training.controller.training;

import org.charess.training.domain.training.PartnerTrainingParticipants;
import org.charess.training.domain.training.Topic;
import org.charess.training.service.training.ParticipantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/participant")
public class ParticipantController {

    private ParticipantService participantService;
    private final Logger log = LoggerFactory.getLogger(TopicController.class);


    @Autowired
    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> save(@RequestBody PartnerTrainingParticipants partnerTrainingParticipants){
        try {
            return participantService.declare(partnerTrainingParticipants)==null? new ResponseEntity<>("", HttpStatus.BAD_REQUEST):new ResponseEntity<>("", HttpStatus.OK);
        } catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }


}
