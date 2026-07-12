// src/test/java/com/bustracking/shared/security/SecurityTestController.java
package com.bustracking.shared.integration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SecurityTestController {

    @GetMapping("/auth/test")
    public String publicEndpoint() {
        return "public";
    }

    @GetMapping("/tracking/test")
    public String driverEndpoint() {
        return "driver";
    }
}