package mk.finki.ukim.milenichinja.Repository.Jpa;


import mk.finki.ukim.milenichinja.Models.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation,Integer> {

}
