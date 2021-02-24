package mk.finki.ukim.milenichinja.Web.Controllers;


import mk.finki.ukim.milenichinja.Models.Center;
import mk.finki.ukim.milenichinja.Models.Enums.City;
import mk.finki.ukim.milenichinja.Service.CenterService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/centers")
public class CentersController {
    public CentersController(CenterService centerService) {
        this.centerService = centerService;
        //this.cityService = cityService;
    }

    private final CenterService centerService;
    //private final CityService cityService;

    @GetMapping
    public String getCenters(Model model){
        List<Center> centri = this.centerService.listAll();
        model.addAttribute("centerList",centri);
        return "mainPages/centers";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/add-form")
    public String getAddCenterPage(Model model){
        //List<City> cityList = this.cityService.listAll();
        List<City> cityList = Arrays.asList(City.values());
        model.addAttribute("cityList",cityList);
        return "posts/addCenter";
    }

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

    @PostMapping("/add")
    public String addNewCenter(
            @RequestParam(required = false) Integer id,
            @RequestParam String address,
            @RequestParam City city,
            @RequestParam String url) {
        if(id != null){
            this.centerService.edit(id, address,city,url);
        }else{
            this.centerService.save(address,city, url);
        }
        return "redirect:/centers";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/deleteCenter/{id}")
    public String deleteCenter(@PathVariable int id){
        centerService.delete(id);
        return "redirect:/centers";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/edit-form/{id}")
    public String editPetPage(@PathVariable int id, Model model) {
        if (this.centerService.findById(id).isPresent()) {
            Center center = this.centerService.findById(id).get();
            //List<City> cities = this.cityService.listAll();
            List<City> cities = Arrays.asList(City.values());
            model.addAttribute("center", center);
            model.addAttribute("cityList",cities);
            return "posts/addCenter";
        }
        return "redirect:/products?error=ProductNotFound";
    }
}