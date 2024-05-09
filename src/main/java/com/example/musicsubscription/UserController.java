package com.example.musicsubscription;

import com.example.musicsubscription.model.Usermodel;
import com.example.musicsubscription.model.musicmodel;
import com.example.musicsubscription.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for handling user and music-related HTTP requests.
 */
@RestController
@CrossOrigin(origins = "http://ec2-54-198-229-9.compute-1.amazonaws.com")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Basic endpoint to check if the server is running.
     * @return A welcome message.
     */
    @GetMapping("/")
    public String home() {
        return "Welcome to the Music Subscription Service!";
    }

    /**
     * Endpoint for registering a new user.
     * @param user A Usermodel object containing user registration information.
     * @return A response entity containing a success or failure message.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Usermodel user) {
        boolean success = userService.register(user.getEmail(), user.getPassword(), user.getUsername());
        Map<String, String> response = new HashMap<>();
        if (success) {
            response.put("message", "User registered successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Email already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Endpoint for logging in a user.
     * @param user A Usermodel object containing the user's email and password.
     * @return A response entity with user details if successful, otherwise an error message.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Usermodel user) {
        Usermodel loggedInUser = userService.login(user.getEmail(), user.getPassword());
        Map<String, String> response = new HashMap<>();
        if (loggedInUser != null) {
            response.put("message", "User logged in successfully");
            response.put("user_name", loggedInUser.getUsername());
            response.put("email", loggedInUser.getEmail());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    /**
     * Endpoint to query music based on certain criteria.
     * @param query A musicmodel object containing search criteria.
     * @return A list of music that matches the query.
     */
    @PostMapping("/query")
    public ResponseEntity<List<musicmodel>> queryMusic(@RequestBody musicmodel query) {
        List<musicmodel> result = userService.queryMusic(query);
        if (!result.isEmpty()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * Endpoint to subscribe a user to a music title.
     * @param requestData A map containing the user's email and the music title to subscribe to.
     * @return A response indicating the success or failure of the subscription.
     */
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeToMusic(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        String title = requestData.get("title");
        boolean success = userService.subscribeMusic(email, title);
        if (success) {
            return ResponseEntity.ok("Subscribed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to subscribe");
        }
    }

    /**
     * Endpoint to fetch all subscriptions for a user.
     * @param email The email of the user whose subscriptions are to be retrieved.
     * @return A list of music subscriptions.
     */
    @GetMapping("/subscriptions")
    public ResponseEntity<List<musicmodel>> subscriptions(@RequestParam String email) {
        List<musicmodel> subscriptions = userService.subscriptions(email);
        return ResponseEntity.ok(subscriptions);
    }

    /**
     * Endpoint to unsubscribe a user from a music title.
     * @param email The user's email.
     * @param title The music title from which to unsubscribe.
     * @return A response indicating whether the unsubscription was successful.
     */
    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribeFromMusic(@RequestParam String email, @RequestParam String title) {
        boolean success = userService.unsubscribeMusic(email, title);
        if (success) {
            return ResponseEntity.ok("Unsubscribed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unsubscribe");
        }
    }
}
