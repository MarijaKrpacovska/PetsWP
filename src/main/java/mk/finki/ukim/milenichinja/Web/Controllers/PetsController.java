package mk.finki.ukim.milenichinja.Web.Controllers;

import mk.finki.ukim.milenichinja.Models.Adoption;
import mk.finki.ukim.milenichinja.Models.AppUser;
import mk.finki.ukim.milenichinja.Models.Center;
import mk.finki.ukim.milenichinja.Models.Enums.AgeGroup;
import mk.finki.ukim.milenichinja.Models.Enums.Gender;
import mk.finki.ukim.milenichinja.Models.Exceptions.AdoptionNotFoundException;
import mk.finki.ukim.milenichinja.Models.Exceptions.InvalidActionException;
import mk.finki.ukim.milenichinja.Models.Exceptions.InvalidUserCredentialsException;
import mk.finki.ukim.milenichinja.Models.Exceptions.PetNotFoundException;
import mk.finki.ukim.milenichinja.Models.Pet;
import mk.finki.ukim.milenichinja.Models.Enums.Type;
import mk.finki.ukim.milenichinja.Service.AdoptionService;
import mk.finki.ukim.milenichinja.Service.AppUserService;
import mk.finki.ukim.milenichinja.Service.CenterService;
import mk.finki.ukim.milenichinja.Service.PetService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/petsList")
public class PetsController {

    private final PetService petService;
    private final CenterService centerService;
    private final AppUserService appUserService;
    private final AdoptionService adoptionService;

    public PetsController(PetService petService, CenterService centerService, AppUserService appUserService, AdoptionService adoptionService) {
        this.petService = petService;
        this.centerService = centerService;
        this.appUserService = appUserService;
        this.adoptionService = adoptionService;
    }

    //MAIN GET PAGES
    @GetMapping
    public String getAvaliablePets(@RequestParam(required = false) AgeGroup ageSearch,
                                   @RequestParam(required = false) String breedSearch,
                                   @RequestParam(required = false) Gender genderSearch,
                                   @RequestParam(required = false) Type typeSearch,
                                   HttpServletRequest request, Model model){
        List<Pet> pets;
        request.getSession().getAttribute("klient");
        if ( ageSearch == null && breedSearch == null && genderSearch == null && typeSearch == null) {
            pets = this.petService.nevdomeniMilenichinja();
        }
        else {
            pets = this.petService.search(ageSearch, breedSearch, genderSearch, typeSearch);
        }


        model.addAttribute("petList", pets);
        model.addAttribute("bodyContent", "pet/availablePetsList.html");
        return "fragments/master-template.html";
      //  return "mainPages/petsDetails.html";
    }

    @GetMapping("/pets-info/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAllPets(@RequestParam(required = false) AgeGroup ageSearch,
                             @RequestParam(required = false) String adoptedSearch,
                             @RequestParam(required = false) Gender genderSearch,
                             @RequestParam(required = false) Type typeSearch,
                             Model model){

        List<Pet> pets;

        if ( ageSearch != null || adoptedSearch != null || genderSearch != null || typeSearch != null ) {
            pets = this.petService.searchAll(ageSearch,adoptedSearch,genderSearch,typeSearch);
        }
        else{
            pets = this.petService.listAll();
        }

        model.addAttribute("notAdoptedPetList", pets);
        model.addAttribute("bodyContent", "pet/petsInfo_allPets.html");

        return "fragments/master-template.html";
    }

    @GetMapping("/pets-info/adopted")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getInfoAdoptedPets(@RequestParam(required = false) Integer petSearch,
                                     @RequestParam(required = false) String userSearch,
                                     @RequestParam(required = false) String error,
                                     Model model){

        if(error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }

        try {
            List<Adoption> adoptions;

            if( petSearch != null || userSearch != null ) {
                adoptions = this.adoptionService.search(userSearch,petSearch);
            }
            else{
                adoptions = this.adoptionService.listAll();
            }

            model.addAttribute("adoptions", adoptions);

            return "pet/petsInfo_adopted.html";
        } catch (AdoptionNotFoundException | UsernameNotFoundException exception) {
            return "redirect:/petsList/pets-info/adopted?error=" + exception.getMessage();
        }

    }

    @GetMapping("/pets-info/avaliable")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getInfoAvaliablePets(@RequestParam(required = false) AgeGroup ageSearch,
                                       @RequestParam(required = false) String breedSearch,
                                       @RequestParam(required = false) Gender genderSearch,
                                       @RequestParam(required = false) Type typeSearch,
                                       Model model){
        List<Pet> notAdoptedPets;

        if ( ageSearch != null || breedSearch != null || genderSearch != null || typeSearch != null ) {
            notAdoptedPets = this.petService.search(ageSearch,breedSearch,genderSearch,typeSearch);
        }
        else{
            notAdoptedPets = this.petService.nevdomeniMilenichinja();
        }

        model.addAttribute("notAdoptedPetList",notAdoptedPets);

        return "pet/petsInfo_avaliable.html";
    }
    //GET MAIN PAGES

    //EDIT ADD DELETE
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/edit-form/{id}")
    public String editPetPage(@PathVariable int id, Model model) {
        if (this.petService.findById(id).isPresent()) {
            Pet pet = this.petService.findById(id).get();
            List<Center> centri = this.centerService.listAll();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String today = ZonedDateTime.now().format(formatter);
            String dateOfBirth = pet.getDateOfBirth().format(formatter);

            model.addAttribute("today", today);
            model.addAttribute("dateOfBirth", dateOfBirth);
            model.addAttribute("pet", pet);
            model.addAttribute("centerList",centri);
            model.addAttribute("bodyContent", "pet/addPet");
            return "fragments/master-template.html";
        }
        return "redirect:/products?error=ProductNotFound";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/add-form")
    public String addProductPage(Model model) {
        List<Center> milenichinja = this.centerService.listAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedString = ZonedDateTime.now().format(formatter);

        model.addAttribute("today", formattedString);
        model.addAttribute("petList",milenichinja);
        model.addAttribute("bodyContent", "pet/addPet");
        return "fragments/master-template.html";
    }

    @PostMapping("/add")
    public String addPet(
            @RequestParam(required = false) Integer id,
            @RequestParam String ime,
            @RequestParam Type vid,
            @RequestParam String rasa,
            @RequestParam Gender pol,
            @RequestParam String opis,
            @RequestParam int id_centar,
            @RequestParam String DoB,
            @RequestParam String url_slika,
            HttpServletRequest req) {
        // this.milenicheService.save(ime, vid, vozrast, rasa, pol, opis, embg_volonter, id_centar, url_slika);
        if(id != null){
            this.petService.edit(id, ime, vid, rasa, pol, opis, id_centar, url_slika, DoB );
        }else{
            String username = req.getRemoteUser();
            AppUser user = appUserService.getByUsername(username).orElseThrow(InvalidUserCredentialsException::new);
            this.petService.save(ime, vid, rasa, pol, opis, id_centar, url_slika, user, DoB);
        }
        return "redirect:/petsList";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deletePet/{id}")
    public String deletePet(@PathVariable int id){
        petService.delete(id);
        return "redirect:/petsList";
    }
    //EDIT ADD DELETE

    //ADOPT
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/adoptPet/{id}")
    public String adoptPet(@PathVariable int id, HttpServletRequest req){
        try {
            //AppUser user = (AppUser) req.getSession().getAttribute("user");
            String username = req.getRemoteUser();
            AppUser user = appUserService.getByUsername(username).orElseThrow(InvalidUserCredentialsException::new);
            petService.adoptPet(user, id);
            return "redirect:/petsList";
        } catch (RuntimeException exception) {
            return "redirect:/petsList?error=" + exception.getMessage();
        }
    }
    //ADOPT

    //DETAILS
    @GetMapping("/details/{id}")
    public String detailsPage(@PathVariable int id, Model model) {
        if (this.petService.findById(id).isPresent()) {
            Pet pet = this.petService.findById(id).get();
            List <Pet> relatedPets = this.petService.search(pet.getAgeGroup(), pet.getBreed(), pet.getGender(), pet.getType());
            String dateOfBirth = pet.getDateOfBirth().getDayOfMonth()+"/"+pet.getDateOfBirth().getMonthValue()+"/"+pet.getDateOfBirth().getYear();
            String arrivalDate = pet.getArivalDate().getDayOfMonth()+"/"+pet.getArivalDate().getMonthValue()+"/"+pet.getArivalDate().getYear();
            model.addAttribute("pet", pet);
            model.addAttribute("relatedPets", relatedPets);
            model.addAttribute("dateOfBirth", dateOfBirth);
            model.addAttribute("arrivalDate", arrivalDate);
            model.addAttribute("bodyContent", "pet/petsDetails.html");
            return "fragments/master-template.html";
        }
        return "redirect:/petsList?error=PetNotFound";
    }
    //DETAILS

}
