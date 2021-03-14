package mk.finki.ukim.milenichinja.Service.Impl;

import mk.finki.ukim.milenichinja.Models.*;
import mk.finki.ukim.milenichinja.Models.Enums.Status;
import mk.finki.ukim.milenichinja.Models.Exceptions.AdoptionNotFoundException;
import mk.finki.ukim.milenichinja.Models.Exceptions.PetNotFoundException;
import mk.finki.ukim.milenichinja.Repository.Jpa.AdoptionRepository;
import mk.finki.ukim.milenichinja.Repository.Jpa.AppUserRepository;
import mk.finki.ukim.milenichinja.Repository.Jpa.PetRepository;
import mk.finki.ukim.milenichinja.Service.AdoptionService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdoptionServiceImpl implements AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository;
    private final AppUserRepository appUserRepository;

    public AdoptionServiceImpl(AdoptionRepository adoptionRepository, PetRepository petRepository, AppUserRepository appUserRepository) {
        this.adoptionRepository = adoptionRepository;
        this.petRepository = petRepository;
        this.appUserRepository = appUserRepository;
    }

    @Override
    public Optional<Adoption> adopt(AppUser user, int petId, String realTimeDate) {

        ZonedDateTime currentDateTime = ZonedDateTime.now();

        String realTimeDateNew = realTimeDate + ":00+00:00";
        ZonedDateTime rtd = ZonedDateTime.parse(realTimeDateNew);

        Pet pet = petRepository.findById(petId).orElseThrow( () -> new PetNotFoundException(petId) );
        pet.setAdopted(true);
        petRepository.save(pet);

        Adoption adoption = new Adoption(currentDateTime, rtd, Status.ACTIVE, user, pet);

        return Optional.of(adoptionRepository.save(adoption));
    }

    @Override
    public List<Adoption> listAll() {
        return this.adoptionRepository.findAll();
    }


   /* @Override
    public void updateAdoptions() {
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        List<Pet> unadoptedPets = this.petRepository.findAllByAdopted(true);

        for (Pet pet : unadoptedPets) {
            AppUser user = pet.getOwner();
            ZonedDateTime realTimeDate = pet.getAdoptionDate();
            Adoption adoption = new Adoption(currentDateTime, realTimeDate, Status.ACTIVE, user, pet);

            adoptionRepository.save(adoption);

    }*/

    @Override
    public List<Adoption> search(String username, Integer id) {

        if(id != null && username != null) {
            Pet pet = this.petRepository.findById(id).orElseThrow( () -> new PetNotFoundException(id));
            AppUser user  = this.appUserRepository.findByUsername(username).orElseThrow( () -> new UsernameNotFoundException(username));
            return this.adoptionRepository.findAllByUserAndPet(user, pet);
        }
        else if(id != null && username == null){
            Pet pet = this.petRepository.findById(id).orElseThrow( () -> new PetNotFoundException(id));
            return this.adoptionRepository.findAllByPet(pet);
        }
        else if(id == null && username != null) {
            AppUser user  = this.appUserRepository.findByUsername(username).orElseThrow( () -> new UsernameNotFoundException(username));
            return this.adoptionRepository.findAllByUser(user);
        }
        else {
            return this.adoptionRepository.findAll();
        }

    }

    @Override
    public Optional<Adoption> confirmAdoption(Integer id) {
        Adoption adoption = this.adoptionRepository.findById(id).orElseThrow( () -> new AdoptionNotFoundException(id));
        adoption.setStatus(Status.COMPLETED);
        return Optional.of( this.adoptionRepository.save(adoption) );
    }

    @Override
    public Optional<Adoption> cancelAdoption(Integer id) {
        Adoption adoption = this.adoptionRepository.findById(id).orElseThrow( () -> new AdoptionNotFoundException(id));
        adoption.setStatus(Status.CLOSED);

        Pet pet = this.petRepository.findById(adoption.getPet().getId()).orElseThrow( () -> new PetNotFoundException(id) );
        pet.setAdopted(false);
        petRepository.save(pet);

        return Optional.of( this.adoptionRepository.save(adoption) );
    }
}