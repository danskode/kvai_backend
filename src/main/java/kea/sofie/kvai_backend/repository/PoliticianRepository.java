package kea.sofie.kvai_backend.repository;


import kea.sofie.kvai_backend.model.Politician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoliticianRepository extends JpaRepository<Politician, Integer> {
    Politician findByName(String name);
}
