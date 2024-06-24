package org.example.webserviceapi;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HospitalControllerIntegrationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Tag("integrationTest")
    @Test
    void testGetAdmissionsForSpecificPatient5() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/Admission/{patientID}", String.class, "5");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Tag("integrationTest")
    @Tag("boundaryCase")
    @Test
    void GetAdmissionsForPatient0() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/Admission/{patientID}", String.class, "0");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Tag("integrationTest")
    @Tag("cornerCase")
    @Test
    void GetAdmissionsForMinimumPatient() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/Admission/{patientID}", String.class, "1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Tag("integrationTest")
    @Test
    void GetAdmissionsForInvalidPatient() {
        ResponseEntity<String> responseNegative = restTemplate.getForEntity("http://localhost:" + port + "/Admission/{patientID}", String.class, "-1");
        ResponseEntity<String> responseLetter = restTemplate.getForEntity("http://localhost:" + port + "/Admission/{patientID}", String.class, "A");
        ResponseEntity<String> responseSpecialCharacter = restTemplate.getForEntity("http://localhost:" + port + "/Admission/{patientID}", String.class, "%");
        ResponseEntity<String> responseDecimal = restTemplate.getForEntity("http://localhost:" + port + "/Admission/{patientID}", String.class, "2.9999999");

        assertEquals(HttpStatus.NOT_FOUND, responseNegative.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, responseLetter.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, responseSpecialCharacter.getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, responseDecimal.getStatusCode());
    }

    @Tag("integrationTest")
    @Test
    void testGetCurrentAdmissions() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/CurrentAdmissions", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Tag("integrationTest")
    @Test
    void testGetEmployeeWithMostAdmissions() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/EmployeeWithMostAdmissions", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Tag("integrationTest")
    @Test
    void testGetEmployeesWithZeroAdmissions() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/EmployeesWithZeroAdmissions", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
