package mk.finki.ukim.milenichinja.Models;


import lombok.Data;
import mk.finki.ukim.milenichinja.Models.Enums.City;
import mk.finki.ukim.milenichinja.Models.Enums.Role;
import mk.finki.ukim.milenichinja.Models.Enums.SocialMediaService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@Table(name = "app_user", schema = "vdomuvanje_milenichinja_wp")
public class AppUser implements UserDetails {
    @Id
    private String username;
    private String name;
    private String lastname;
    private String email;
    private String password;
    private String profilePicture;
    private ZonedDateTime startDate;

    @Enumerated(value = EnumType.STRING)
    private SocialMediaService service;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Enumerated(value = EnumType.STRING)
    private City city;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(name = "AppUserWorksAt", schema = "vdomuvanje_milenichinja_wp" )
    private List<Center> worksAt;

    private boolean isAccountNonExpired=true;
    private boolean isAccountNonLocked=true;
    private boolean isCredentialsNonExpired=true;
    private boolean isEnabled=true;

    public AppUser() {
    }

    //FOR USERS
    public AppUser(String username, String name, String lastname, String email, String password, ZonedDateTime startDate, Role role, City city) {
        this.username = username;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.startDate = startDate;
        this.role = role;
        this.city = city;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

}
