package kea.sofie.kvai_backend.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Politician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incremented primary key
    private int id;
    private String name;
    private String party;
    private String area;

    public Politician(String name, String party, String area) {
        this.name = name;
        this.party = party;
        this.area = area;
    }

}
