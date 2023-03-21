package mk.finki.ukim.milenichinja.Service;

import mk.finki.ukim.milenichinja.Models.*;
import mk.finki.ukim.milenichinja.Models.Enums.PaymentMethod;
import mk.finki.ukim.milenichinja.Models.Enums.Status;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface AdoptionService {

    Optional<Adoption> adopt(AppUser user, int petId, String realTimeDate);

    List<Adoption> listAll();
    List<Adoption> listAllByStatus(Status status);


    //void updateAdoptions();

    List<Adoption> search(String username, Integer petId);

    Optional<Adoption> confirmAdoption(Integer id);

    public Optional<Adoption> cancelAdoption(Integer id);

}
