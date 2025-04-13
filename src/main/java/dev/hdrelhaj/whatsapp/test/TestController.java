package dev.hdrelhaj.whatsapp.test;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protected")
    public ResponseEntity<String> testProtected() {
        return ResponseEntity.ok("✅ You are authenticated!");
    }

    @GetMapping("/public")
    public ResponseEntity<String> testPublic() {
        return ResponseEntity.ok("🌐 This is a public endpoint.");
    }
}
