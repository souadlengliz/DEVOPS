package tn.esprit.eventsproject.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Participant implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int idPart;
    String nom;
    String prenom;
    @Enumerated(EnumType.STRING)
    Tache tache;
    @ManyToMany
    @JsonIgnore
    Set<Event> events;

}
