package tn.esprit.eventsproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServicesImpl implements IEventServices{

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final LogisticsRepository logisticsRepository;

    @Override
    public Participant addParticipant(Participant participant) {
        return participantRepository.save(participant);
    }

     @Override
    public Event addAffectEvenParticipant(Event event, int idParticipant) {
        Participant participant = participantRepository.findById(idParticipant).orElse(null);
        if (participant == null) {
            throw new IllegalArgumentException("Participant with ID " + idParticipant + " not found.");
        }
        if (participant.getEvents() == null) {
            Set<Event> events = new HashSet<>();
            events.add(event);
            participant.setEvents(events);
        } else {
            participant.getEvents().add(event);
        }
        return eventRepository.save(event);
    }



    @Override
    public Event addAffectEvenParticipant(Event event) {
        Set<Participant> participants = event.getParticipants();
        if (participants != null) {
            for (Participant aParticipant : participants) {
                Participant participant = participantRepository.findById(aParticipant.getIdPart()).orElse(null);
                if (participant != null) {
                    if (participant.getEvents() == null) {
                        Set<Event> events = new HashSet<>();
                        events.add(event);
                        participant.setEvents(events);
                    } else {
                        participant.getEvents().add(event);
                    }
                }
            }
        }
        return eventRepository.save(event);
    }


    @Override
    public Logistics addAffectLog(Logistics logistics, String descriptionEvent) {
      Event event = eventRepository.findByDescription(descriptionEvent);
      if(event.getLogistics() == null){
          Set<Logistics> logisticsSet = new HashSet<>();
          logisticsSet.add(logistics);
          event.setLogistics(logisticsSet);
          eventRepository.save(event);
      }
      else{
          event.getLogistics().add(logistics);
      }
        return logisticsRepository.save(logistics);
    }

    @Override
    public List<Logistics> getLogisticsDates(LocalDate dateDebut, LocalDate dateFin) {
        List<Event> events = eventRepository.findByDateDebutBetween(dateDebut, dateFin);

        List<Logistics> logisticsList = new ArrayList<>();
        for (Event event:events){
            if(event.getLogistics().isEmpty()){

                return Collections.emptyList();
            }

            else {
                Set<Logistics> logisticsSet = event.getLogistics();
                for (Logistics logistics:logisticsSet){
                    if(logistics.isReserve())
                        logisticsList.add(logistics);
                }
            }
        }
        return logisticsList;
    }

    @Scheduled(cron = "*/60 * * * * *")
    @Override
    public void calculCout() {
        List<Event> events = eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi","Ahmed", Tache.ORGANISATEUR);
        float somme = 0f;
        for(Event event:events){
            log.info(event.getDescription());
            Set<Logistics> logisticsSet = event.getLogistics();
            for (Logistics logistics:logisticsSet){
                if(logistics.isReserve())
                    somme+=logistics.getPrixUnit()*logistics.getQuantite();
            }
            event.setCout(somme);
            eventRepository.save(event);
            log.info("Cout de l'Event "+event.getDescription()+" est "+ somme);

        }
    }
    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

}
