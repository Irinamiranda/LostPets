package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Arrays;

@Controller
public class HomeController {

    @Autowired
    PetRepository petRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/")
    public String index(Model model) {
        model.addAttribute("pets", petRepository.findAll());

        return "index";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String addUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "addUser";
    }

    @PostMapping("/processUser")
    public String processUser(@Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "processUser";
        }

        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(Arrays.asList(userRole));

        user.setEnabled(true);

        userRepository.save(user);
        return "redirect:/login";
    }
    @RequestMapping("/pets")
    public String listPets(Model model, Principal principal) {
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        model.addAttribute("pets", petRepository.findAll());
        return "petList";
    }

    @GetMapping("/addPet")
    public String addPet(Model model, Principal principal) {
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        model.addAttribute("pet", new Pet());
        return "addPet";
    }

    @PostMapping("/processPet")
    public String processPet(@Valid Pet pet, BindingResult result) {
        if (result.hasErrors()) {
            return "addPet";
        }

        petRepository.save(pet);
        return "redirect:/pets";
    }


}
