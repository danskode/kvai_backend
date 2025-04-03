package kea.sofie.kvai_backend.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Politician {

    // Et ID ifht. db?

    private String name;
    private String party;
    //private String description; // evt noget kort info om politikeren?
    //private String imageUrl; skal der være et billede af den enkelte politiker?

}
