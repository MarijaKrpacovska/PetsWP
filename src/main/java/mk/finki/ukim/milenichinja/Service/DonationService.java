package mk.finki.ukim.milenichinja.Service;

import mk.finki.ukim.milenichinja.Models.*;
import mk.finki.ukim.milenichinja.Models.Enums.PaymentMethod;

import java.util.List;
import java.util.Optional;

public interface DonationService {
    //Donation()
    Optional<Donation> save(AppUser donator, double sum, PaymentMethod paymentMethod, Long cardNumber, String valute, DonationCause donationCause);

    List<Donation> listAll();
}
