package mk.finki.ukim.milenichinja.Web.Controllers;

import mk.finki.ukim.milenichinja.Models.Center;
import mk.finki.ukim.milenichinja.Models.DonationCause;
import mk.finki.ukim.milenichinja.Models.Enums.City;
import mk.finki.ukim.milenichinja.Models.Pet;
import mk.finki.ukim.milenichinja.Service.AppUserService;
import mk.finki.ukim.milenichinja.Service.DonationCauseService;
import mk.finki.ukim.milenichinja.Service.DonationService;
import mk.finki.ukim.milenichinja.Service.PetService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/causes")
public class DonationCausesController {

    private final DonationService donationService;

    private final DonationCauseService donationCauseService;

    private final AppUserService appUserService;

    private final PetService petService;


    public DonationCausesController(DonationService donationService, DonationCauseService donationCauseService, AppUserService appUserService, PetService petService) {
        this.donationService = donationService;
        this.donationCauseService = donationCauseService;
        this.appUserService = appUserService;
        this.petService = petService;
    }

    //MAIN GET PAGE
    @GetMapping
    public String getAllCausesPage(Model model) {
        List<DonationCause> donationCauses = this.donationCauseService.listAll();
        model.addAttribute("causesList",donationCauses);
        model.addAttribute("bodyContent", "mainPages/donationCauses");
        return "mainPages/master-template.html";
    }
    //MAIN GET PAGE

    //ADD EDIT DELETE
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/addDonationCause")
    public String getAddCausePage(@RequestParam(required = false) String error, Model model) {
        List<Pet> allPets = petService.listAll();
        model.addAttribute("allPets", allPets);
        if(error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        // model.addAttribute("bodyContent","register");

        return "posts/addDonationCause.html";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/edit-form/{id}")
    public String editPetPage(@PathVariable int id, Model model) {
        if (this.donationCauseService.findById(id).isPresent()) {
            DonationCause cause = this.donationCauseService.findById(id).get();
            //List<City> cities = this.cityService.listAll();

            List<Pet> allPets = petService.listAll();
            model.addAttribute("allPets", allPets);

            model.addAttribute("cause", cause);
            return "posts/addDonationCause";
        }
        return "redirect:/causes?error=DonationCauseNotFound";
    }

    @PostMapping("/causePost")
    public String addCausePost(@RequestParam(required = false) Integer id,
                               @RequestParam String name,
                               @RequestParam String url,
                               @RequestParam String description,
                               @RequestParam double goal,
                               @RequestParam int importance,
                               @RequestParam(required = false) List<Integer> petsIds,
                               HttpServletRequest req) {
        if(id != null){
            this.donationCauseService.edit(id,description,url,petsIds,goal,name,importance);
        }else{
            this.donationCauseService.save(description,url,petsIds,goal,name,importance);
        }

        return "redirect:/causes";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteCause/{id}")
    public String deleteCause(@PathVariable int id){
        donationCauseService.delete(id);
        return "redirect:/causes";
    }
    //ADD EDIT DELETE

    //DETAILS
    @GetMapping("/details/{id}")
    public String detailsPage(@PathVariable int id, Model model) {
        if (this.donationCauseService.findById(id).isPresent()) {
            DonationCause cause = this.donationCauseService.findById(id).get();
            //String causeSum = donationCauseService.currentState(cause);

            model.addAttribute("cause", cause);
            //model.addAttribute("causeSum", causeSum);
            return "details/donationCause.html";
        }
        return "redirect:/products?error=ProductNotFound";
    }
    //DETAILS

    //CANCEL, COMPLETE DONATION CAUSE, TRANSFER MONEY
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/cancel/{id}")
    public String cancelDonationCause(@PathVariable int id, Model model) {
        if (this.donationCauseService.findById(id).isPresent()) {
            DonationCause cause = this.donationCauseService.findById(id).get();
            List<DonationCause> causesList = this.donationCauseService.listAll();
            model.addAttribute("causes", causesList);
            model.addAttribute("cause", cause);
            return "posts/cancelCause";
        }
        return "redirect:/products?error=ProductNotFound";
    }

    @PostMapping("/cancel")
    public String cancelDonationCause(@RequestParam int id,
                                      @RequestParam int idTransfer,
                                      Model model,
                                      HttpServletRequest req) {
        DonationCause cause = this.donationCauseService.findById(id).get();

        model.addAttribute("cause", cause);
        this.donationCauseService.cancelCause(id, idTransfer);

        return "redirect:/causes";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/complete/{id}")
    public String completeDonationCause(@PathVariable int id) {
        this.donationCauseService.finishCause(id);
        return "redirect:/causes";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/transfer-sum")
    public String transferMoneyPage(Model model) {
        List<DonationCause> causesList = this.donationCauseService.listAll();
        model.addAttribute("causes", causesList);
        return "posts/transferMoney";
    }

    @PostMapping("/transfer-sum")
    public String transferMoney(@RequestParam int idFrom,
                                @RequestParam int idTo,
                                @RequestParam double sum) {
        this.donationCauseService.transferSumMoney(idFrom, idTo, sum);
        return "redirect:/causes";
    }
    //CANCEL, COMPLETE DONATION CAUSE, TRANSFER MONEY

}
