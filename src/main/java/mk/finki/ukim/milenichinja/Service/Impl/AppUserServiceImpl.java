package mk.finki.ukim.milenichinja.Service.Impl;

import mk.finki.ukim.milenichinja.Models.AppUser;
import mk.finki.ukim.milenichinja.Models.Center;
import mk.finki.ukim.milenichinja.Models.Enums.City;
import mk.finki.ukim.milenichinja.Models.Exceptions.ClientAlreadyExistsException;
import mk.finki.ukim.milenichinja.Models.Exceptions.InvalidUserCredentialsException;
import mk.finki.ukim.milenichinja.Models.Exceptions.InvalidUsernameOrPasswordException;
import mk.finki.ukim.milenichinja.Models.Exceptions.PasswordsDoNotMatchException;
import mk.finki.ukim.milenichinja.Models.Role;
import mk.finki.ukim.milenichinja.Repository.Jpa.AppUserRepository;
import mk.finki.ukim.milenichinja.Repository.Jpa.CenterRepository;
import mk.finki.ukim.milenichinja.Service.AppUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final CenterRepository centerRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserServiceImpl(AppUserRepository appUserRepository, CenterRepository centerRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.centerRepository = centerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<AppUser> listAll() {
        return appUserRepository.findAll();
    }

    @Override
    public AppUser login(String username, String password) {
        return appUserRepository.findByUsernameAndPassword(username,
                password).orElseThrow(InvalidUserCredentialsException::new);
    }

    @Override
    public Optional<AppUser> getByUsername(String username) {
        return this.appUserRepository.findByUsername(username);
    }

    /*@Override
    public AppUser register(String username, String ime, String prezime, int id_grad, String email, String password, String repeatPass) {
        if (username==null || username.isEmpty()  || password==null || password.isEmpty())
            throw new InvalidUsernameOrPasswordException();
        if (!password.equals(repeatPass))
            throw new PasswordsDoNotMatchException();
        if(this.appUserRepository.findByUsername(username).isPresent())
            throw new ClientAlreadyExistsException(username);
       // City city = cityRepository.findById(id_grad).orElseThrow(InvalidUserCredentialsException::new);
        City city = City.Bitola;
       // Center center = centerRepository.findById(centar_id).orElseThrow(InvalidUserCredentialsException::new);
        //(String username, String name, String lastname, City city, String email, String password, ZonedDateTime
        //startDate, ZonedDateTime endDate, Role role)
        //AppUser user = new AppUser(username,ime,prezime, city, email, password);
        ZonedDateTime date1= ZonedDateTime.now();
        ZonedDateTime date2= ZonedDateTime.now();
        Role role = Role.ROLE_USER;
        AppUser user = new AppUser(username,ime,prezime, city, email,passwordEncoder.encode(password) , date1, date2,role);
        return appUserRepository.save(user);

    }*/

    /*@Override
    public AppUser register(String username, String ime, String prezime, City city, String email, String password, String repeatPass) {
        if (username==null || username.isEmpty()  || password==null || password.isEmpty())
            throw new InvalidUsernameOrPasswordException();
        if (!password.equals(repeatPass))
            throw new PasswordsDoNotMatchException();
        if(this.appUserRepository.findByUsername(username).isPresent())
            throw new ClientAlreadyExistsException(username);
        // City city = cityRepository.findById(id_grad).orElseThrow(InvalidUserCredentialsException::new);
       // City city = City.Bitola;
        // Center center = centerRepository.findById(centar_id).orElseThrow(InvalidUserCredentialsException::new);
        //(String username, String name, String lastname, City city, String email, String password, ZonedDateTime
        //startDate, ZonedDateTime endDate, Role role)
        //AppUser user = new AppUser(username,ime,prezime, city, email, password);
        ZonedDateTime date1= ZonedDateTime.now();
        ZonedDateTime date2= ZonedDateTime.now();
        Role role = Role.ROLE_USER;
        AppUser user = new AppUser(username,ime,prezime, city, email,passwordEncoder.encode(password) , date1, date2,role);
        return appUserRepository.save(user);
    }*/

    @Override
    public AppUser register(String username, String ime, String prezime, City city, String email, String password, String repeatPass, List<Integer> worksAt, Role role) {
        if (username==null || username.isEmpty()  || password==null || password.isEmpty())
            throw new InvalidUsernameOrPasswordException();
        if (!password.equals(repeatPass))
            throw new PasswordsDoNotMatchException();
        if(this.appUserRepository.findByUsername(username).isPresent())
            throw new ClientAlreadyExistsException(username);
        ZonedDateTime date1= ZonedDateTime.now();
        List<Center> worksAtCenters = this.centerRepository.findAllById(worksAt);
        AppUser user = new AppUser(username,ime,prezime,email,passwordEncoder.encode(password),date1,role,city,worksAtCenters);
        return appUserRepository.save(user);
    }

    @Override
    public AppUser registerUser(String username, String ime, String prezime, City city, String email, String password, String repeatPass, Role role) {
        if (username==null || username.isEmpty()  || password==null || password.isEmpty())
            throw new InvalidUsernameOrPasswordException();
        if (!password.equals(repeatPass))
            throw new PasswordsDoNotMatchException();
        if(this.appUserRepository.findByUsername(username).isPresent())
            throw new ClientAlreadyExistsException(username);
        ZonedDateTime date1= ZonedDateTime.now();
        AppUser user = new AppUser(username,ime,prezime,email,passwordEncoder.encode(password),date1,role,city);
        return appUserRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(s).orElseThrow(() -> new  UsernameNotFoundException(s));
        return user;
    }
}