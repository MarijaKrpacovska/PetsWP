package mk.finki.ukim.milenichinja.Service.Impl;

import mk.finki.ukim.milenichinja.Models.AppUser;
import mk.finki.ukim.milenichinja.Models.Center;
import mk.finki.ukim.milenichinja.Models.Enums.AgeGroup;
import mk.finki.ukim.milenichinja.Models.Enums.Gender;
import mk.finki.ukim.milenichinja.Models.Exceptions.CenterNotFoundException;
import mk.finki.ukim.milenichinja.Models.Exceptions.PetNotFoundException;
import mk.finki.ukim.milenichinja.Models.Pet;
import mk.finki.ukim.milenichinja.Models.Enums.Type;
import mk.finki.ukim.milenichinja.Repository.Jpa.AppUserRepository;
import mk.finki.ukim.milenichinja.Repository.Jpa.CenterRepository;
import mk.finki.ukim.milenichinja.Repository.Jpa.PetRepository;
import mk.finki.ukim.milenichinja.Service.AdoptionService;
import mk.finki.ukim.milenichinja.Service.PetService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.time.ZonedDateTime;

@Service
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final CenterRepository centerRepository;
    private final AppUserRepository appUserRepository;
    private final AdoptionService adoptionService;

    public PetServiceImpl(PetRepository petRepository, CenterRepository centerRepository, AppUserRepository appUserRepository, AdoptionService adoptionService) {
        this.petRepository = petRepository;
        this.centerRepository = centerRepository;
        this.appUserRepository = appUserRepository;
        this.adoptionService = adoptionService;
    }

    @Override
    public List<Pet> listAll() {
        return petRepository.findAll();
    }

    @Override
    public Optional<Pet> findById(int id) {
        return petRepository.findById(id);
    }

    @Override
    public List<Pet> nevdomeniMilenichinja() {
        //this.adoptionService.updateAdoptions();
        this.updateAllAges();
        return this.petRepository.findAllByAdopted(false);
    }

    @Override
    public List<Pet> vdomeniMilenichinja() {
        this.updateAllAges();
        return this.petRepository.findAllByAdopted(true);
    }

    @Override
    @Transactional
    public void adoptPet(AppUser user, int id) {
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        Pet pet = petRepository.findById(id).orElseThrow( () -> new PetNotFoundException(id) );
        pet.setAdopted(true);
       // pet.setOwner(user);
       // pet.setAdoptionDate(currentDateTime);
        petRepository.save(pet);
    }

    @Override
    public void delete(int id) {
        if(petRepository.findById(id).isPresent()){
            petRepository.deleteById(id);
        }
    }

    @Override
    public void updateAge(Pet pet) {
        Period period = Period.between(
                pet.getDateOfBirth().toLocalDate(),
                LocalDate.now()
        );
        int months = period.getMonths();
        int years = period.getYears();
        String age;
        if (years == 0){
            age = months + " months";
        }
        else {
            if (months > 8){
                years++;
            }
            age = years + " years";
        }
        pet.setAge(age);
    }

    @Override
    public void updateAllAges() {
        List<Pet> pets = this.listAll();
        for (Pet pet:
             pets) {
        Period period = Period.between(
                pet.getDateOfBirth().toLocalDate(),
                LocalDate.now()
        );
        int months = period.getMonths();
        int years = period.getYears();
        String age;
        if (years == 0){
            age = months + " months";
            pet.setAgeGroup(AgeGroup.lessThanYear);
        }
        else {
            if (months > 8){
                years++;
            }
            age = years + " years";
            if(Integer.parseInt(age.split(" ")[0]) == 1 || Integer.parseInt(age.split(" ")[0]) == 2){
                pet.setAgeGroup(AgeGroup.oneToTwoYears);
            }
            else if(Integer.parseInt(age.split(" ")[0]) >= 3 && Integer.parseInt(age.split(" ")[0]) <= 5){
                pet.setAgeGroup(AgeGroup.threeToFiveYears);
            }
            else if(Integer.parseInt(age.split(" ")[0]) >= 6 && Integer.parseInt(age.split(" ")[0]) <= 9){
                pet.setAgeGroup(AgeGroup.sixToNineYears);
            }
            else if(Integer.parseInt(age.split(" ")[0]) >= 10){
                pet.setAgeGroup(AgeGroup.tenYearsOrOlder);
            }
        }
        pet.setAge(age);
        this.petRepository.save(pet);
        }

    }

    @Override
    public Optional<Pet> save(String ime, Type vid, String rasa, Gender pol, String opis, int id_centar, String url_slika, AppUser odgovorenVolonter, String DoB) {
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        String doBNew = DoB + "T10:00:00+00:00";
        ZonedDateTime dateOfBirth = ZonedDateTime.parse(doBNew);
        Center center = centerRepository.findById(id_centar).orElseThrow( () -> new CenterNotFoundException(id_centar));
        Pet pet = new Pet(ime, vid, rasa, pol, opis, center, currentDateTime, url_slika, odgovorenVolonter, false, dateOfBirth);
        this.updateAge(pet);
        return Optional.of(this.petRepository.save(pet));
    }

    @Override
    public Optional<Pet> edit(int id, String ime, Type vid, String rasa, Gender pol, String opis, int id_centar, String url_slika, String DoB) {
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        String doBNew = DoB + "T10:00:00+00:00";
        ZonedDateTime dateOfBirth = ZonedDateTime.parse(doBNew);

        Pet pet= petRepository.findById(id).orElseThrow( () -> new PetNotFoundException(id) );
        Center center = centerRepository.findById(id_centar).orElseThrow( () -> new PetNotFoundException(id) );

        pet.setName(ime);
        pet.setType(vid);
        //pet.setAge(vozrast);
        pet.setBreed(rasa);
        pet.setGender(pol);
        pet.setDescription(opis);
        pet.setCenter(center);
        pet.setUrl(url_slika);
        pet.setDateOfBirth(dateOfBirth);
        this.updateAge(pet);

        return Optional.of(this.petRepository.save(pet));
    }

    @Override
    public List<Pet> search(AgeGroup age, String breed, Gender gender, Type type) {
        if(age != null  && !breed.equals("") && gender != null && type != null) {
            return this.petRepository.findAllByAgeGroupAndBreedLikeAndGenderAndTypeAndAdopted(age,"%"+breed+"%",gender,type, false);
        }
        else if(age == null && !breed.equals("") && gender != null && type != null){
            return this.petRepository.findAllByBreedLikeAndGenderAndTypeAndAdopted("%"+breed+"%",gender,type, false);
        }
        else if(age != null && breed.equals("") && gender != null && type != null){
            return this.petRepository.findAllByAgeGroupAndGenderAndTypeAndAdopted(age,gender,type, false);
        }
        else if(age != null&& !breed.equals("") && gender == null && type != null){
            return this.petRepository.findAllByAgeGroupAndBreedLikeAndTypeAndAdopted(age,"%"+breed+"%",type, false);
        }
        else if(age != null && !breed.equals("") && gender != null && type == null){
            return this.petRepository.findAllByAgeGroupAndBreedLikeAndGenderAndAdopted(age,"%"+breed+"%",gender, false);
        }
        else if(age == null && breed.equals("") && gender != null && type != null){
            return this.petRepository.findAllByGenderAndTypeAndAdopted(gender, type, false);
        }
        else if(age == null && !breed.equals("") && gender == null && type != null){
            return this.petRepository.findAllByBreedLikeAndTypeAndAdopted("%"+breed+"%", type, false);
        }
        else if(age == null && !breed.equals("") && gender != null && type == null){
            return this.petRepository.findAllByBreedLikeAndGenderAndAdopted("%"+breed+"%", gender, false);
        }
        else if(age != null && breed.equals("") && gender == null && type != null){
            return this.petRepository.findAllByAgeGroupAndTypeAndAdopted(age, type, false);
        }
        else if(age != null && breed.equals("") && gender != null && type == null){
            return this.petRepository.findAllByAgeGroupAndGenderAndAdopted(age, gender, false);
        }
        else if(age != null && !breed.equals("") && gender == null && type == null){
            return this.petRepository.findAllByAgeGroupAndBreedLikeAndAdopted(age,breed, false);
        }
        else if(age == null && breed.equals("") && gender == null && type != null){
            return this.petRepository.findAllByTypeAndAdopted(type, false);
        }
        else if(age == null && breed.equals("") && gender != null && type == null){
            return this.petRepository.findAllByGenderAndAdopted(gender, false);
        }
        else if(age == null && !breed.equals("") && gender == null && type == null){
            return this.petRepository.findAllByBreedLikeAndAdopted("%"+breed+"%", false);
        }
        else if(age != null && breed.equals("") && gender == null && type == null){
            return this.petRepository.findAllByAgeGroupAndAdopted(age, false);
        }
        else{
            return nevdomeniMilenichinja();
        }
    }

    @Override
    public List<Pet> searchAll(AgeGroup age, String  adopted, Gender gender, Type type) {
        boolean adoptedBoolean = true;

        if(!adopted.equals(""))
            adoptedBoolean = Boolean.parseBoolean(adopted);

        if(age != null  && !adopted.equals("") && gender != null && type != null) {
            return this.petRepository.findAllByAgeGroupAndBreedLikeAndGenderAndTypeAndAdopted(age,"%"+""+"%",gender,type, adoptedBoolean);
        }
        else if(age == null && !adopted.equals("") && gender != null && type != null){
            return this.petRepository.findAllByBreedLikeAndGenderAndTypeAndAdopted("%"+""+"%",gender,type, adoptedBoolean);
        }
        else if(age != null && adopted.equals("") && gender != null && type != null){
            return this.petRepository.findAllByAgeGroupAndGenderAndType(age,gender,type);
        }
        else if(age != null&& !adopted.equals("") && gender == null && type != null){
            return this.petRepository.findAllByAgeGroupAndBreedLikeAndTypeAndAdopted(age,"%"+""+"%",type, adoptedBoolean);
        }
        else if(age != null && !adopted.equals("") && gender != null && type == null){
            return this.petRepository.findAllByAgeGroupAndBreedLikeAndGenderAndAdopted(age,"%"+""+"%",gender, adoptedBoolean);
        }
        else if(age == null && adopted.equals("") && gender != null && type != null){
            return this.petRepository.findAllByGenderAndType(gender, type);
        }
        else if(age == null && !adopted.equals("") && gender == null && type != null){
            return this.petRepository.findAllByBreedLikeAndTypeAndAdopted("%"+""+"%", type, adoptedBoolean);
        }
        else if(age == null && !adopted.equals("") && gender != null && type == null){
            return this.petRepository.findAllByBreedLikeAndGenderAndAdopted("%"+""+"%", gender, adoptedBoolean);
        }
        else if(age != null && adopted.equals("") && gender == null && type != null){
            return this.petRepository.findAllByAgeGroupAndType(age, type);
        }
        else if(age != null && adopted.equals("") && gender != null && type == null){
            return this.petRepository.findAllByAgeGroupAndGender(age, gender);
        }
        else if(age != null && !adopted.equals("") && gender == null && type == null){
            return this.petRepository.findAllByAgeGroupAndBreedLikeAndAdopted(age,"%"+""+"%", adoptedBoolean);
        }
        else if(age == null && adopted.equals("") && gender == null && type != null){
            return this.petRepository.findAllByType(type);
        }
        else if(age == null && adopted.equals("") && gender != null && type == null){
            return this.petRepository.findAllByGender(gender);
        }
        else if(age == null && !adopted.equals("") && gender == null && type == null){
            return this.petRepository.findAllByBreedLikeAndAdopted("%"+""+"%", adoptedBoolean);
        }
        else if(age != null && adopted.equals("") && gender == null && type == null){
            return this.petRepository.findAllByAgeGroup(age);
        }
        else{
            return nevdomeniMilenichinja();
        }
    }

    @Override
    public List<Pet> searchByCenter(Integer centerId) {
        return this.petRepository.findAllByCenterAndAdopted(centerRepository.findById(centerId).get(),false);
    }

    @Override
    public List<Pet> searchAdopted(Integer id) {
        if(id != null) {
            return this.petRepository.findAllByAdoptedAndId(true,id);
        }
        else{
            return vdomeniMilenichinja();
        }
    }



}
