package mk.finki.ukim.milenichinja.Service;

import mk.finki.ukim.milenichinja.Models.AppUser;
import mk.finki.ukim.milenichinja.Models.Enums.AgeGroup;
import mk.finki.ukim.milenichinja.Models.Enums.Gender;
import mk.finki.ukim.milenichinja.Models.Pet;
import mk.finki.ukim.milenichinja.Models.Enums.Type;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PetService {
    List<Pet> listAll();

    List<Pet> nevdomeniMilenichinja();

    List<Pet> vdomeniMilenichinja();

    Optional<Pet> findById(int id);

    Optional<Pet> save(String ime, Type vid,
                       String rasa, Gender pol, String opis,
                       int id_centar, String url_slika,
                       AppUser odgovorenVolonter, String DoB);

    Optional<Pet> edit(int id, String ime, Type vid,
                      String rasa, Gender pol,
                      String opis, int id_centar, String url_slika, String DoB);

    void delete(int id);

    void updateAge(Pet pet);

    void updateAllAges();

    void adoptPet(AppUser user, int id_mileniche);

    List<Pet> search(AgeGroup age, String breed, Gender gender, Type type);

    List<Pet> searchAll(AgeGroup age, String adopted, Gender gender, Type type);

    List<Pet> searchByCenter(Integer centerId);

    List<Pet> searchAdopted(Integer id);
}
