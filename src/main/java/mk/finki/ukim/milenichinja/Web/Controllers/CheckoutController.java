package mk.finki.ukim.milenichinja.Web.Controllers;

import mk.finki.ukim.milenichinja.Models.ChargeRequest;
import mk.finki.ukim.milenichinja.Models.DonationCause;
import mk.finki.ukim.milenichinja.Service.DonationCauseService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckoutController {

    private final DonationCauseService donationCauseService;

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    public CheckoutController(DonationCauseService donationCauseService) {
        this.donationCauseService = donationCauseService;
    }

    @RequestMapping("/checkout")
    public String checkout(Model model) {
        DonationCause donationCause= donationCauseService.findById(1).get();
        model.addAttribute("amount", 50 * 100); // in cents
        model.addAttribute("cause", donationCause); // in cents
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.EUR);
        return "stripe/checkout";
    }

    @RequestMapping("/donation-checkout")
    public String donation_checkout(Model model, @RequestParam int sum) {
        DonationCause donationCause= donationCauseService.findById(1).get();
        model.addAttribute("amount", sum * 100); // in cents
        model.addAttribute("cause", donationCause); // in cents
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.EUR);
        model.addAttribute("bodyContent","stripe/checkout");
        return "fragments/master-template.html";
    }
}