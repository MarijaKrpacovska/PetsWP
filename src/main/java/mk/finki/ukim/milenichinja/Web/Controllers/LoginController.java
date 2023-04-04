package mk.finki.ukim.milenichinja.Web.Controllers;

import mk.finki.ukim.milenichinja.Models.AppUser;
import mk.finki.ukim.milenichinja.Models.Center;
import mk.finki.ukim.milenichinja.Models.Enums.City;
import mk.finki.ukim.milenichinja.Models.Exceptions.ClientAlreadyExistsException;
import mk.finki.ukim.milenichinja.Models.Exceptions.InvalidUsernameOrPasswordException;
import mk.finki.ukim.milenichinja.Models.Exceptions.PasswordsDoNotMatchException;
import mk.finki.ukim.milenichinja.Models.Enums.Role;
import mk.finki.ukim.milenichinja.Service.AppUserService;
import mk.finki.ukim.milenichinja.Service.CenterService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping
public class LoginController {

    private final AppUserService appUserService;
    private final CenterService centerService;

    public LoginController(AppUserService appUserService, CenterService centerService) {
        this.appUserService = appUserService;
        this.centerService = centerService;
    }

    //LOGIN
    @GetMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("bodyContent","auth/login.html");
        return "fragments/master-template";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/login";
    }
    //LOGIN

    //REGISTER
    @GetMapping("/register")
    public String getRegisterPage(@RequestParam(required = false) String error, Model model) {
        if(error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        List<Center> worksAtList = centerService.listAll();
        List<City> cityList = Arrays.asList(City.values());
        model.addAttribute("cityList",cityList);
        model.addAttribute("worksAtList",worksAtList);
        model.addAttribute("admin",false);
        model.addAttribute("worksAtList",worksAtList);
        model.addAttribute("bodyContent","auth/register");
        return "fragments/master-template";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String ime,
                           @RequestParam String prezime,
                           @RequestParam City city,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String repeatPass) {
        try{
            Role role = Role.ROLE_USER;
            this.appUserService.registerUser(username,ime,prezime,city,email,password,repeatPass, role);
            return "redirect:/petsList";
        } catch (InvalidUsernameOrPasswordException | ClientAlreadyExistsException | PasswordsDoNotMatchException exception) {
            return "redirect:/register?error=" + exception.getMessage();
        }
    }
    //REGISTER

    //MANAGE ROLES
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/manage-roles")
    public String getManageRolesPage(@RequestParam(required = false) String error, Model model) {
        if(error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        List<Center> worksAtList = centerService.listAll();
        List<AppUser> users = appUserService.getAllUsers();
        List<AppUser> admins = appUserService.getAllAdmins();

        model.addAttribute("worksAtList",worksAtList);
        model.addAttribute("allUsers",users);
        model.addAttribute("allAdmins",admins);
        return "auth/registerA.html";
    }

    @PostMapping("/manage-roles")
    public String manageRoles(@RequestParam(required = false) String allUsers,
                              @RequestParam(required = false) String allAdmins,
                              @RequestParam(required = false) List<Integer> worksAtList) {
        try{
            if(!allUsers.equals(""))
                this.appUserService.addAdmin(allUsers, worksAtList);

            if(!allAdmins.equals(""))
                this.appUserService.removeAdmin(allAdmins);

            return "redirect:/manage-roles";
        } catch (InvalidUsernameOrPasswordException exception) {
            return "redirect:/register?error=" + exception.getMessage();
        }
    }
    //MANAGE ROLES

  /*  @PostMapping
    public String login(HttpServletRequest request, Model model) {
        Klient klient = null;
            klient = this.klientService.login(request.getParameter("username"),
                    request.getParameter("password"));
            request.getSession().setAttribute("klient", klient);
            return "redirect:/petsList";

    }*/
/*
    @PostMapping
    public String login(HttpServletRequest request, Model model) {
        AppUser user = null;
        user = this.appUserService.login(request.getParameter("username"),
                request.getParameter("password"));
        request.getSession().setAttribute("user", user);
        return "redirect:/petsList";
    }*/

}
