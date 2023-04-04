package mk.finki.ukim.milenichinja.Web.Controllers;


import mk.finki.ukim.milenichinja.Models.Center;
import mk.finki.ukim.milenichinja.Models.Enums.City;
import mk.finki.ukim.milenichinja.Models.Exceptions.CenterNotFoundException;
import mk.finki.ukim.milenichinja.Models.Exceptions.DeleteConstraintViolationException;
import mk.finki.ukim.milenichinja.Models.Pet;
import mk.finki.ukim.milenichinja.Service.CenterService;
import mk.finki.ukim.milenichinja.Service.PetService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/centers")
public class CentersController {

    private final CenterService centerService;
    private final PetService petService;

    public CentersController(CenterService centerService, PetService petService) {
        this.centerService = centerService;
        this.petService = petService;
    }

    //MAIN GET PAGE
    @GetMapping
    public String getCenters(@RequestParam(required = false) String error,
                             Model model){
        if(error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        List<Center> centri = this.centerService.listAll();
        List<City> cities = Arrays.asList(City.values());
        model.addAttribute("centerList", centri);
        model.addAttribute("citiesList", cities);
        model.addAttribute("bodyContent", "center/centersList");
        return "fragments/master-template.html";
    }
    //MAIN GET PAGE

    //ADD EDIT DELETE
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/add-form")
    public String getAddCenterPage(Model model){
        //List<City> cityList = this.cityService.listAll();
        List<City> cityList = Arrays.asList(City.values());
        model.addAttribute("cityList",cityList);
        model.addAttribute("bodyContent", "center/addCenter");
        return "fragments/master-template.html";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/edit-form/{id}")
    public String editPetPage(@PathVariable int id, Model model) {
        Center center = this.centerService.findById(id).get();
        List<City> cities = Arrays.asList(City.values());
        model.addAttribute("center", center);
        model.addAttribute("cityList",cities);
        return "center/addCenter";
    }

    @PostMapping("/add")
    public String addNewCenter(@RequestParam(required = false) Integer id,
                               @RequestParam String address,
                               @RequestParam City city,
                               @RequestParam String url,
                               RedirectAttributes redirectAttributes) {
        try {
            if (id != null) {
                this.centerService.edit(id, address, city, url);
            } else {
                this.centerService.save(address, city, url);
            }
            return "redirect:/centers";
        }
        catch (CenterNotFoundException exception) {
            redirectAttributes.addAttribute("id", id);
            return "redirect:/edit-form/{id}?error=" + exception.getMessage();
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteCenter/{id}")
    public String deleteCenter(@PathVariable int id){
        try {
            centerService.delete(id);
            return "redirect:/centers";
        }
        catch (CenterNotFoundException | DeleteConstraintViolationException exception) {
            return "redirect:/centers?error=" + exception.getMessage();
        }
    }
    //ADD EDIT DELETE

    //DETAILS
    @GetMapping("/details/{id}")
    public String detailsPage(@PathVariable int id, Model model) {
        //
        try {
            if (this.centerService.findById(id).isPresent()) {
                Center center = this.centerService.findById(id).get();
                List<Pet> pets = this.petService.searchByCenter(id);
                model.addAttribute("center", center);
                model.addAttribute("petsList", pets);
            }
            model.addAttribute("bodyContent", "center/centerDetails.html");
            return "fragments/master-template.html";
        }catch (CenterNotFoundException exception) {
            return "redirect:/centers?error=" + exception.getMessage();
        }
    }
    //DETAILS

     /*@PostMapping("/add")
    public String addNewCenter(
            @RequestParam(required = false) Integer id,
            @RequestParam String address,
            @RequestParam int id_city,
            @RequestParam String url) {
        if(id != null){
            this.centerService.edit(id, address,id_city,url);
        }else{
            this.centerService.save(address,id_city, url);
        }
        return "redirect:/centers";
    }*/



}
