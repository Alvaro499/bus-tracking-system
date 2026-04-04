package com.bustracking.shared.testinfrastructure;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;


/*

    @WebMvcTest: starts only the web layer, including controllers, filters, and Spring MVC configuration. No persistence layer, no security, and no services.


*/

@WebMvcTest
@ActiveProfiles("test")
public abstract class ControllerIntegrationTest {

    // It doesn't need TestContainers because we mock the service layer
}
