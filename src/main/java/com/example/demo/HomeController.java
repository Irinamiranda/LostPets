package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String index(Model model, Principal principal) {
        User currentUser = principal != null ? userRepository.findByUsername(principal.getName()) : null;
        model.addAttribute("user", currentUser);

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
            return "addUser";
        }

        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(Arrays.asList(userRole));

        user.setEnabled(true);

        userRepository.save(user);
        return "redirect:/login";
    }

    @RequestMapping("/pets")
    public String listPets(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", currentUser);

        model.addAttribute("pets", petRepository.findByOwner(currentUser));

        return "petList";
    }

    @GetMapping("/addPet")
    public String addPet(Model model, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName());
        model.addAttribute("user", currentUser);

        Pet pet = new Pet();
        pet.setOwner(currentUser);

        model.addAttribute("pet", pet);
        return "addPet";
    }

    @PostMapping("/processPet")
    public String processPet(@Valid @ModelAttribute Pet pet, BindingResult result, Model model, Principal principal, @RequestParam("file") MultipartFile file) {
        if (result.hasErrors()) {
            // -- This is to prevent "Welcome null" message in the header
            User currentUser = userRepository.findByUsername(principal.getName());
            model.addAttribute("user", currentUser);


            return "addPet";
        }
        petRepository.save(pet);
        return "redirect:/";

        }



    @RequestMapping("/update/{id}")
    public String updatePet(@PathVariable("id") long id, Model model, Principal principal){
        model.addAttribute("user", userRepository.findByUsername(principal.getName()));
        model.addAttribute("pet", petRepository.findById(id).get());
        return "addPet";
    }



}