package mk.finki.ukim.milenichinja.Web.Controllers;


import mk.finki.ukim.milenichinja.Models.*;
import mk.finki.ukim.milenichinja.Models.Enums.PaymentMethod;
import mk.finki.ukim.milenichinja.Models.Exceptions.InvalidUserCredentialsException;
import mk.finki.ukim.milenichinja.Models.Exceptions.InvalidUsernameOrPasswordException;
import mk.finki.ukim.milenichinja.Service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping
public class DonateController {

    private final DonationService donationService;

    private final DonationCauseService donationCauseService;

    private final AppUserService appUserService;

    private final PetService petService;

    private final ValuteService valuteService;

    public DonateController(DonationService donationService, DonationCauseService donationCauseService, AppUserService appUserService, PetService petService, ValuteService valuteService) {
        this.donationService = donationService;
        this.donationCauseService = donationCauseService;
        this.appUserService = appUserService;
        this.petService = petService;
        this.valuteService = valuteService;
    }

    //DONATE
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/donate")
    public String getDonatePage(@RequestParam(required = false) String error, Model model) {

        List<DonationCause> donationCauses = this.donationCauseService.listAllActiveCauses();
        List<Valute> valutes = this.valuteService.listAll();
        List<PaymentMethod> paymentMethods = Arrays.asList(PaymentMethod.values());

        model.addAttribute("causesList",donationCauses);
        model.addAttribute("paymentMethods",paymentMethods);
        model.addAttribute("valutes",valutes);

        if(error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        model.addAttribute("bodyContent","donation/donate");
        return "fragments/master-template.html";
    }

    @PostMapping("/donate")
    public String donate(@RequestParam int sum,
                         @RequestParam Integer donationCauseId,
                         HttpServletRequest req
    ) {
        try{
            String username = req.getRemoteUser();
            AppUser user = appUserService.getByUsername(username).orElseThrow(InvalidUserCredentialsException::new);
            DonationCause donationCause=this.donationCauseService.findById(donationCauseId).orElseThrow();
            Valute val = this.valuteService.findByShortName("MKD").get();

            this.donationService.save(user, sum, PaymentMethod.CREDITCARD, Long.getLong("1321"), val, donationCause);
            return "redirect:/donation-checkout?sum="+sum;
        } catch (InvalidUsernameOrPasswordException exception) {
            return "redirect:/register?error=" + exception.getMessage();
        }
    }
    //DONATE

//    @PostMapping("/donate")
//    public String donate(@RequestParam int sum,
//                         @RequestParam Long cardNumber,
//                         @RequestParam String valute,
//                         @RequestParam Integer donationCauseId,
//                         @RequestParam PaymentMethod paymentMethod,
//                         HttpServletRequest req
//    ) {
//        try{
//            String username = req.getRemoteUser();
//            AppUser user = appUserService.getByUsername(username).orElseThrow(InvalidUserCredentialsException::new);
//            DonationCause donationCause=this.donationCauseService.findById(donationCauseId).orElseThrow();
//            Valute val = this.valuteService.findByShortName(valute).orElseThrow( () -> new ValuteNotFoundException(valute));
//
//            this.donationService.save(user, sum, paymentMethod, cardNumber, val, donationCause);
//            return "redirect:/donation-checkout?sum="+sum;
//        } catch (InvalidUsernameOrPasswordException exception) {
//            return "redirect:/register?error=" + exception.getMessage();
//        }
//    }
//    //DONATE

    //VIEW ALL DONATIONS
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/allDonations")
    public String getAllDonationsPage(Model model) {
        List<Donation> donations = this.donationService.listAll();
        model.addAttribute("donationsList",donations);
        return "donation/donationsList";
    }
    //VIEW ALL DONATIONS

}
