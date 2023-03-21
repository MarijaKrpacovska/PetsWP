package mk.finki.ukim.milenichinja.Repository.Jpa;

import mk.finki.ukim.milenichinja.Models.Center;
import mk.finki.ukim.milenichinja.Models.Enums.AgeGroup;
import mk.finki.ukim.milenichinja.Models.Enums.Gender;
import mk.finki.ukim.milenichinja.Models.Enums.Type;
import mk.finki.ukim.milenichinja.Models.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet,Integer> {

    void deleteById(int id);

    Optional<Pet> findById(int id);

    List<Pet> findAllByAdopted(Boolean adopted);


    List<Pet> findAllByAgeGroupAndAdopted(AgeGroup age, Boolean adopted);
    List<Pet> findAllByAgeGroup(AgeGroup age);

    List<Pet> findAllByBreedLikeAndAdopted(String breed, Boolean adopted);

    List<Pet> findAllByGenderAndAdopted(Gender gender, Boolean adopted);
    List<Pet> findAllByGender(Gender gender);

    List<Pet> findAllByTypeAndAdopted(Type type, Boolean adopted);
    List<Pet> findAllByType(Type type);


    List<Pet> findAllByAgeGroupAndBreedLikeAndAdopted(AgeGroup age,String breed, Boolean adopted);

    List<Pet> findAllByAgeGroupAndGenderAndAdopted(AgeGroup age,Gender gender, Boolean adopted);
    List<Pet> findAllByAgeGroupAndGender(AgeGroup age,Gender gender);

    List<Pet> findAllByAgeGroupAndTypeAndAdopted(AgeGroup age,Type type, Boolean adopted);
    List<Pet> findAllByAgeGroupAndType(AgeGroup age,Type type);


    List<Pet> findAllByBreedLikeAndGenderAndAdopted(String breed,Gender gender, Boolean adopted);

    List<Pet> findAllByBreedLikeAndTypeAndAdopted(String breed,Type type, Boolean adopted);

    List<Pet> findAllByGenderAndTypeAndAdopted(Gender gender,Type type, Boolean adopted);
    List<Pet> findAllByBreedLikeAndGender(String breed,Gender gender);
    List<Pet> findAllByBreedLikeAndType(String breed,Type type);
    List<Pet> findAllByGenderAndType(Gender gender,Type type);



    List<Pet> findAllByAgeGroupAndBreedLikeAndGenderAndAdopted(AgeGroup age, String breed, Gender gender, Boolean adopted);


    List<Pet> findAllByAgeGroupAndBreedLikeAndTypeAndAdopted(AgeGroup age, String breed, Type type, Boolean adopted);

    List<Pet> findAllByAgeGroupAndGenderAndTypeAndAdopted(AgeGroup age, Gender gender, Type type, Boolean adopted);
    List<Pet> findAllByAgeGroupAndGenderAndType(AgeGroup age, Gender gender, Type type);

    List<Pet> findAllByBreedLikeAndGenderAndTypeAndAdopted(String breed, Gender gender, Type type, Boolean adopted);

    List<Pet> findAllByAgeGroupAndBreedLikeAndGenderAndTypeAndAdopted(AgeGroup age, String breed, Gender gender, Type type, Boolean adopted);


    List<Pet> findAllByAdoptedAndId(Boolean adopted, Integer id);

    List<Pet> findAllByCenter(Center center);

    List<Pet> findAllByCenterAndAdopted(Center center, Boolean adopted);


}
