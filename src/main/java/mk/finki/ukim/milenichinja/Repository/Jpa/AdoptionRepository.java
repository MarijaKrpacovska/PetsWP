package mk.finki.ukim.milenichinja.Repository.Jpa;


import mk.finki.ukim.milenichinja.Models.*;
import mk.finki.ukim.milenichinja.Models.Enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption,Integer> {

    List<Adoption> findAllByPet(Pet pet);

    List<Adoption> findAllByUser(AppUser user);

    List<Adoption> findAllByUserAndPet(AppUser user, Pet pet);

    List<Adoption> findAllByStatus(Status status);

}
