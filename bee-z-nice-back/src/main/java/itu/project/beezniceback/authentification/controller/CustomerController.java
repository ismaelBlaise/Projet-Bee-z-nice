package itu.project.beezniceback.authentification.controller;

import itu.project.beezniceback.authentification.dto.LoginDTO;
import itu.project.beezniceback.authentification.model.Customer;
import itu.project.beezniceback.authentification.model.CustomerException;
import itu.project.beezniceback.authentification.model.CustomerService;
import itu.project.beezniceback.authentification.model.LoggedCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.bind.annotation.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SessionRepository<?> sessionRepository;

    @PostMapping("/api/register")
    public ResponseEntity<?> createUser(@RequestBody Customer customer) {
        try {
            LoggedCustomer loggedCustomer = customerService.authenticate(customer);
            Session session = sessionRepository.createSession();
            session.setAttribute("user", loggedCustomer);
            return ResponseEntity.ok(loggedCustomer);
        } catch (CustomerException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/api/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoggedCustomer loggedCustomer = customerService.login(loginDTO);
            Session session = sessionRepository.createSession();
            session.setAttribute("user", loggedCustomer);
            session.setAttribute("message", "Hello world");
            return ResponseEntity.ok(loggedCustomer);
        } catch (CustomerException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }

//    @GetMapping("/api/sessionAttributes")
//    public Map<String, Object> getSessionAttributes(Session session) {
//        // Create a Map to store session attributes
//        Map<String, Object> sessionAttributes = new HashMap<>();
//
//        // Get the names of all attributes bound to the session
//        Enumeration<String> attributeNames = (Enumeration<String>) session.getAttributeNames();
//
//        // Iterate through the attribute names and retrieve their values
//        while (attributeNames.hasMoreElements()) {
//            String attributeName = attributeNames.nextElement();
//            Object attributeValue = session.getAttribute(attributeName);
//            sessionAttributes.put(attributeName, attributeValue);
//        }
//
//        // Return the Map containing all session attributes
//        return sessionAttributes;
//    }

    @GetMapping("/api/checkSession")
    public ResponseEntity<?> sessionExist(@SessionAttribute("user") LoggedCustomer user) {
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found in session");
        }
    }
}
