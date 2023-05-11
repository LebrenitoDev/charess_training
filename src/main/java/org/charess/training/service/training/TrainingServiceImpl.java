package org.charess.training.service.training;

import org.charess.training.domain.security.*;
import org.charess.training.domain.training.Participant;
import org.charess.training.domain.training.Training;
import org.charess.training.domain.training.TrainingPartner;
import org.charess.training.repository.security.PersonRepository;
import org.charess.training.repository.security.PlaceRepository;
import org.charess.training.repository.training.ParticipantRepository;
import org.charess.training.repository.training.TrainingRepository;
import org.charess.training.service.security.MailService;
import org.charess.training.service.security.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Transactional
@Service("trainingService")
public class TrainingServiceImpl implements TrainingService {

    private final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private TrainingRepository trainingRepository;
    private UserService userService;
    private ParticipantRepository participantRepository;
    private PlaceRepository placeRepository;
    private MailService mailService;
    private PersonRepository personRepository;

    @Autowired
    public TrainingServiceImpl(TrainingRepository trainingRepository, UserService userService, ParticipantRepository participantRepository,
                               PlaceRepository placeRepository, MailService mailService, PersonRepository personRepository) {
        this.trainingRepository = trainingRepository;
        this.userService = userService;
        this.participantRepository = participantRepository;
        this.placeRepository = placeRepository;
        this.mailService = mailService;
        this.personRepository = personRepository;
    }

    private String formatDatesInterval(LocalDate start, LocalDate end){
        String str="";
        if(start!=null && end!=null){

            if(start.getYear()==end.getYear() && start.getMonth()==end.getMonth()){
                str = String.format("du %s au %s %s %s", start.getDayOfMonth(), end.getDayOfMonth(), end.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE), end.getYear());
            } else if(start.getYear()==end.getYear() && start.getMonth()!=end.getMonth()){
                str = String.format("du %s %s au %s %s %s", start.getDayOfMonth(), start.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE), end.getDayOfMonth(),
                        end.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE), end.getYear());
            } else {
                str = String.format("du %s %s %s au %s %s %s", start.getDayOfMonth(), start.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE), start.getYear(),
                        end.getDayOfMonth(), end.getMonth().getDisplayName(TextStyle.FULL, Locale.FRANCE), end.getYear());
            }
        }
        return str;
    }

    private String formatCategory(List<Category> categories){
        String str = "";
        int last = categories.size() - 1;
        for(int i=0; i<categories.size(); i++){
            str += str.length()<1?categories.get(i).getName():String.format(i!=last?", %s":" et %s", categories.get(i).getName());
        }
        return str;
    }

    private String[] getPartnersEmails(List<Place> partners){
        List<String> tos = new ArrayList<>();
        for(Place partner: partners){
            for(Person manager: partner.getManagers()){
                tos.add(manager.getEmail());
            }
        }
        return tos.stream().toArray(String[]::new);
    }

    private void mail(Training training){
        String subject = "Formation «"+training.getTopic().getFullname()+"»";
        String[] ccs = training.getLocation().getManagers().stream().map(m->m.getEmail()).toArray(String[]::new);
        String[] tos = getPartnersEmails(training.getPartners());
        String body = String.format("Cher(s) collaborateur(s), </br></br>" +
            "Le %s a le plaisir de vous inviter à une session de formation sur le thème «%s».</br>" +
            "Cette formation se tiendra %s de 08:00 à 16:30, dans les locaux de la dite institution sise au %s.</br></br>" +
            "Cette session ne concerne que les catégories de personnels ci-après: %s. Chaque réseau ou partenaire ici copié peut avoir %s représentants/participants au plus.</br></br>" +
            "Afin de faciliter les démarches administratives, %s vous prie de soumettre la liste de vos participants (NIF ou CIN, Prénom, Nom, Téléphone et E-mail) " +
            "directement via <a href='http://formation.charess.org/#/partnership/participants' style='cursor:pointer'>http://formation.charess.org/#/partnership/participants</a></br></br>" +
            "Nota Bene:</br>" +
            "1- Pour nous envoyer vos representants, vous devez vous connecter sur <a href='http://formation.charess.org' style='cursor:pointer'>http://formation.charess.org</a>. " +
            "Il vous faudra créer un compte si vous n'en aviez pas eu un.</br>" +
            "2- Une fois les participants désignés, les suivis administratifs vont être faits directement avec eux, en gardant les responsables en copie. " +
            "Chaque personne aura à confirmer sa participation et formuler ses besoins logistiques.</br></br></br>" +
            "Merci de ne pas répondre à cet email.</br></br>" +
            "En espérant compter vos représentants parmi nous lors de cette formation, veuillez recevoir Madame, Monsieur, l’expression de nos sentiments respectueux.</br></br>" +
            "Cordialement,</br></br>" +
            "Équipe de formation</br>" +
            "Centre Haïtien pour le Renforcement des Systèmes de Santé (CHARESS)</br>" +
            "(+509) 2940-4832</br>" +
            "(+509) 2940-4834</br>" +
            "formation@charess.org</br>" +
            "<a href='www.charess.org' style='cursor:pointer'>www.charess.org</a>",
            training.getLocation().getFullname(), training.getTopic().getFullname(), formatDatesInterval(training.getStartDate(), training.getEndDate()),
            training.getLocation().getAddress(), formatCategory(training.getCategories()), training.getCapacity(), training.getLocation().getAccronym()!=null?training.getLocation().getAccronym():"on");
        mailService.sendHtml(subject, ccs, tos, body);
    }

    public Training broadcast(Training training){
        Audit audit = training;
        userService.inject(audit);
        System.out.println(training);
        if(training == null)
            return null;

        trainingRepository.save(training);
        // if(training.getStartDate().isAfter(LocalDate.now()))
        //     mail(training);

        return training;
    }

    public void changeStatus(Integer trainingId, String status){
        Training training = trainingRepository.findById(trainingId).get();
        LocalDateTime time = LocalDateTime.now();
        training.setStatus(status);
        training.setStatusDatetime(time);
        training.setEditor(userService.getCurrentUser().getId());
        training.setEdited(time);
        trainingRepository.save(training);
    }

    public List<Training> all(){
        return this.trainingRepository.all();
    }

    private TrainingPartner setPartnerTrainingStatus(Training training, Place partner){
        List<Person> participants = participantRepository.findByTrainingIdAndPartnerId(training.getId(), partner.getId())
                .stream().map(participant -> participant.getPerson()).collect(Collectors.toList());
        TrainingPartner tp = new TrainingPartner(training, partner, participants);
        return tp;
    }

    public List<TrainingPartner> getPartnerTrainings(Integer partnerId){
        List<Training> trainings = trainingRepository.findPartnerTrainings(partnerId);
        Place partner = placeRepository.findById(partnerId).get();
        List<TrainingPartner> partnerTrainings = new ArrayList<>();

        for(Training t: trainings){
            partnerTrainings.add(setPartnerTrainingStatus(t, partner));
        }

        return partnerTrainings;
    }

    public Training updateParticipants(Training training){
        String editorName = userService.getUserById(userService.getCurrentUser().getId()).getPerson().getFullname();
        Training t = trainingRepository.findById(training.getId()).get();

        participantRepository.deleteAllByTraining(t);
        participantRepository.flush();

        List<Participant> participants = new ArrayList<>();
        for(Participant p: training.getParticipants()){
            Audit audit = p;
            if(audit.getId()!=null){
                audit.setEdited(LocalDateTime.now());
                audit.setEditor(userService.getCurrentUser().getId());
                audit.setEditorName(editorName);

            }else{
                audit.setCreated(LocalDateTime.now());
                audit.setCreator(userService.getCurrentUser().getId());
            }
            Person person = personRepository.save(p.getPerson());
            p.setPerson(person);
            p.setTraining(training);
            log.info("=={}==={}===={}========{}===", person, training, p.getPartner(), p.getLogistic());
            participants.add(new Participant(p, audit));
        }

        participantRepository.saveAll(participants);
        t.setEditor(userService.getCurrentUser().getId());
        t.setEditorName(editorName);
        t.setEdited(LocalDateTime.now());

        if(Status.TRAINING_BROADCAST.toString().equals(t.getStatus())){
            t.setStatus(Status.TRAINING_PENDING.toString());
            trainingRepository.save(t);

        }
        return training;
    }
}




