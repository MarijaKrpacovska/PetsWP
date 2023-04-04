package mk.finki.ukim.milenichinja.Web.Controllers;

import mk.finki.ukim.milenichinja.Models.DonationCause;
import mk.finki.ukim.milenichinja.Service.DonationCauseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/home")
public class HomeController {

    private final DonationCauseService donationCauseService;

    public HomeController(DonationCauseService donationCauseService) {
        this.donationCauseService = donationCauseService;
    }

    @GetMapping
    public String getHomePage(HttpServletRequest request, Model model){
        model.addAttribute("bodyContent", "home/homepage.html");
        List<DonationCause> causes = this.donationCauseService.listAllActiveCauses();
        DonationCause causeOne = causes.get(0);
        DonationCause causeTwo = causes.get(1);
        DonationCause causeThree = causes.get(2);
        model.addAttribute("causeOne", causeOne);
        model.addAttribute("causeTwo", causeTwo);
        model.addAttribute("causeThree", causeThree);
        return "fragments/master-template.html";
        //  return "mainPages/petsDetails.html";
    }

    @GetMapping("/access_denied")
    public String getAccessDeniedPage(Model model) {
        return "fragments/AccessDenied.html";
    }



}
