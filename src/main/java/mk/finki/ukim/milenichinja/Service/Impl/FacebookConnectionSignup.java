package mk.finki.ukim.milenichinja.Service.Impl;

import mk.finki.ukim.milenichinja.Models.AppUser;
import mk.finki.ukim.milenichinja.Models.Enums.City;
import mk.finki.ukim.milenichinja.Models.Enums.Role;
import mk.finki.ukim.milenichinja.Repository.Jpa.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Service;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Service
public class FacebookConnectionSignup implements ConnectionSignUp {

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public String execute(Connection<?> connection) {

        AppUser user = new AppUser();

        user.setUsername(connection.getDisplayName());
        user.setPassword(randomAlphabetic(8));
        user.setRole(Role.ROLE_USER);
        user.setName(connection.getDisplayName().split(" ")[0]);
        user.setName(connection.getDisplayName().split(" ")[1]);
        user.setCity(City.Skopje);

        user.setProfilePicture(connection.getImageUrl());
        userRepository.save(user);
        return user.getUsername();
    }

}
