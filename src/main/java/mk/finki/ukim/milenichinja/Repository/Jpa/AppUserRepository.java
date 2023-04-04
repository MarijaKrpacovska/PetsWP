package mk.finki.ukim.milenichinja.Repository.Jpa;

import mk.finki.ukim.milenichinja.Models.AppUser;
import mk.finki.ukim.milenichinja.Models.Enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByUsernameAndPassword(String username, String password);

    List<AppUser> findAllByRole(Role role);

}
