package org.example.webserviceapi;

import org.example.webserviceapi.HospitalController;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

@SpringBootTest(classes = HospitalController.class)
class HospitalControllerUnitTests {

    private static final Object API_BASE_URL = "https://web.socem.plymouth.ac.uk/COMP2005/api/";
    @MockBean
    private RestTemplate restTemplate;

    @Tag("unitTest")
    @Test
    void testGetAdmissionsForPatient5() {
        // Define the expected response
        String expectedResponse = "[{\"admissionId\":4,\"admissionDate\":\"2024-02-23T21:50:00\",\"dischargeDate\":\"2024-09-27T09:56:00\"},{\"admissionId\":5,\"admissionDate\":\"2024-04-12T22:55:00\",\"dischargeDate\":\"2024-04-14T11:36:00\"},{\"admissionId\":6,\"admissionDate\":\"2024-04-19T21:50:00\",\"dischargeDate\":\"0001-01-01T00:00:00\"}]";
        ResponseEntity<String> mockAdmissionsEntity = getAdmissions();

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Admissions", String.class)).thenReturn(mockAdmissionsEntity);

        // Call the endpoint
        HospitalController hospitalController = new HospitalController(restTemplate);
        ResponseEntity<String> actualResponse = hospitalController.getAdmissionsForSpecificPatient("5");

        // Assert that the status code is OK
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        // Assert that the response body matches the expected JSON string
        assertEquals(expectedResponse, actualResponse.getBody());
    }


    // Edge case - Patient with no admissions
    @Tag("unitTest")
    @Tag("edgeCase")
    @Test
    void GetAdmissionsForPatientWithNoAdmissions() {
        String patientWithNoAdmissions = "3";

        ResponseEntity<String> mockAdmissionsEntity = getAdmissions();

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Admissions", String.class)).thenReturn(mockAdmissionsEntity);

        HospitalController hospitalController = new HospitalController(restTemplate);

        ResponseEntity<String> response = hospitalController.getAdmissionsForSpecificPatient(patientWithNoAdmissions);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No admissions for patient found with ID " + patientWithNoAdmissions, response.getBody());
    }

    // Boundary Case - Patient 0 is 1 below the minimum patient ID, therefore the boundary
    @Tag("unitTest")
    @Tag("boundaryCase")
    @Test
    void GetAdmissionsForPatient0() {
        String boundaryPatient = "0";

        ResponseEntity<String> mockAdmissionsEntity = getAdmissions();

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Admissions", String.class)).thenReturn(mockAdmissionsEntity);

        HospitalController hospitalController = new HospitalController(restTemplate);

        ResponseEntity<String> response = hospitalController.getAdmissionsForSpecificPatient(boundaryPatient);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No admissions for patient found with ID " + boundaryPatient, response.getBody());
    }

    // Corner Case - Minimum Patient
    @Tag("unitTest")
    @Tag("cornerCase")
    @Test
    void GetAdmissionsForMinimumPatient() {
        String minimumPatient = "1";

        String expectedResponse = "[{\"admissionId\":2,\"admissionDate\":\"2020-12-07T22:14:00\",\"dischargeDate\":\"0001-01-01T00:00:00\"}]";
        ResponseEntity<String> mockAdmissionsEntity = getAdmissions();

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Admissions", String.class)).thenReturn(mockAdmissionsEntity);

        // Call the endpoint
        HospitalController hospitalController = new HospitalController(restTemplate);
        ResponseEntity<String> actualResponse = hospitalController.getAdmissionsForSpecificPatient(minimumPatient);

        // Assert that the status code is OK
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        // Assert that the response body matches the expected JSON string
        assertEquals(expectedResponse, actualResponse.getBody());
    }

    // Corner Case - Maximum Patient
    @Tag("unitTest")
    @Tag("cornerCase")
    @Test
    void GetAdmissionsForMaximumPatient() {
        String maximumPatient = "10000000000000000000000000000000000000000";

        ResponseEntity<String> mockAdmissionsEntity = getAdmissions();

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Admissions", String.class)).thenReturn(mockAdmissionsEntity);

        HospitalController hospitalController = new HospitalController(restTemplate);

        ResponseEntity<String> response = hospitalController.getAdmissionsForSpecificPatient(maximumPatient);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No admissions for patient found with ID " + maximumPatient, response.getBody());
    }

    // Edge case - Invalid PatientID
    @Tag("unitTest")
    @Test
    void GetAdmissionsForInvalidPatient() {
        String negative = "-1";
        String letter = "T";
        String specialCharacter = "&";
        String decimal = "2.999999999";

        ResponseEntity<String> mockAdmissionsEntity = getAdmissions();

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Admissions", String.class)).thenReturn(mockAdmissionsEntity);

        HospitalController hospitalController = new HospitalController(restTemplate);

        ResponseEntity<String> responseNegative = hospitalController.getAdmissionsForSpecificPatient(negative);
        ResponseEntity<String> responseLetter = hospitalController.getAdmissionsForSpecificPatient(letter);
        ResponseEntity<String> responseSpecialCharacter = hospitalController.getAdmissionsForSpecificPatient(specialCharacter);
        ResponseEntity<String> responseDecimal = hospitalController.getAdmissionsForSpecificPatient(decimal);

        assertEquals("No admissions for patient found with ID " + negative, responseNegative.getBody());
        assertEquals("No admissions for patient found with ID " + letter, responseLetter.getBody());
        assertEquals("No admissions for patient found with ID " + specialCharacter, responseSpecialCharacter.getBody());
        assertEquals("No admissions for patient found with ID " + decimal, responseDecimal.getBody());

        assertSame(HttpStatus.NOT_FOUND, responseNegative.getStatusCode(), "API should not accept a negative patientID" );
        assertSame(HttpStatus.NOT_FOUND, responseLetter.getStatusCode(), "API should not accept a letter patientID" );
        assertSame(HttpStatus.NOT_FOUND, responseSpecialCharacter.getStatusCode(), "API should not accept a special character patientID" );
        assertSame(HttpStatus.NOT_FOUND, responseDecimal.getStatusCode(), "API should not accept a decimal character patientID" );
    }


    @Tag("unitTest")
    @Test
    void testGetCurrentAdmissions() {
        String expectedResponse = "[{\"patientID\":2,\"admissionDate\":\"2020-12-07T22:14:00\"},{\"patientID\":6,\"admissionDate\":\"2024-04-19T21:50:00\"}]";
        ResponseEntity<String> mockAdmissionsEntity = getAdmissions();

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Admissions", String.class)).thenReturn(mockAdmissionsEntity);

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity("/CurrentAdmissions", String.class)).thenReturn(new ResponseEntity<>(expectedResponse, HttpStatus.OK));

        HospitalController hospitalController = new HospitalController(restTemplate);
        ResponseEntity<String> actualResponse = hospitalController.getCurrentAdmissions();

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedResponse, actualResponse.getBody());
    }

    @Tag("unitTest")
    @Test
    void testGetNoAdmissions() {
        String expectedResponse = "No patients currently admitted";

        // Mock the RestTemplate's getForEntity method to return an empty list of admissions
        when(restTemplate.getForEntity(API_BASE_URL + "Admissions", String.class)).thenReturn(new ResponseEntity<>("[]", HttpStatus.OK));

        HospitalController hospitalController = new HospitalController(restTemplate);
        ResponseEntity<String> actualResponse = hospitalController.getCurrentAdmissions();

        assertEquals(HttpStatus.NOT_FOUND, actualResponse.getStatusCode());
        assertEquals(expectedResponse, actualResponse.getBody());
    }


    @Tag("unitTest")
    @Test
    void testGetEmployeeWithMostAdmissions() {
        String expectedResponse = "{\"id\":4,\"surname\":\"Jones\",\"forename\":\"Sarah\"}";
        ResponseEntity<String> mockAllocationsEntity = getAllocations();
        ResponseEntity<String> mockSpecificEmployeesEntity = getSpecificEmployee(4);

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Allocations", String.class)).thenReturn(mockAllocationsEntity);

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Employees/4", String.class)).thenReturn(mockSpecificEmployeesEntity);

        HospitalController hospitalController = new HospitalController(restTemplate);
        ResponseEntity<String> actualResponse = hospitalController.getEmployeeWithMostAdmissions();

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedResponse, actualResponse.getBody());
    }

    //Edge case - Admissions endpoint is empty
    @Tag("unitTest")
    @Tag("edgeCase")
    @Test
    void testEmptyResponseFromAdmissions() {
        // Mock the RestTemplate's getForEntity method to return an empty response
        when(restTemplate.getForEntity(API_BASE_URL + "Admissions", String.class))
                .thenReturn(new ResponseEntity<>("", HttpStatus.OK));

        HospitalController hospitalController = new HospitalController(restTemplate);
        ResponseEntity<String> F1Response = hospitalController.getAdmissionsForSpecificPatient("5");
        ResponseEntity<String> F2Response = hospitalController.getCurrentAdmissions();

        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, F1Response.getStatusCode(), "Admissions endpoint for getAdmissionsForSpecificPatient method shouldn't return data");
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, F2Response.getStatusCode(), "Admissions endpoint for getCurrentAdmissions method shouldn't return data");
    }

    //Edge case - Allocations endpoint is empty
    @Tag("unitTest")
    @Tag("edgeCase")
    @Test
    void testEmptyResponseFromAllocations() {
        // Mock the RestTemplate's getForEntity method to return an empty response
        when(restTemplate.getForEntity(API_BASE_URL + "Allocations", String.class))
                .thenReturn(new ResponseEntity<>("", HttpStatus.OK));

        HospitalController hospitalController = new HospitalController(restTemplate);
        ResponseEntity<String> F3Response = hospitalController.getEmployeeWithMostAdmissions();
        ResponseEntity<String> F4Response = hospitalController.getEmployeesWithZeroAdmissions();

        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, F3Response.getStatusCode(), "Allocations endpoint for getEmployeeWithMostAdmissions method shouldn't return data");
        assertSame(HttpStatus.INTERNAL_SERVER_ERROR, F4Response.getStatusCode(), "Allocations endpoint for getEmployeesWithZeroAdmissions method shouldn't return data");

    }

    @Tag("unitTest")
    @Test
    void testGetEmployeesWithZeroAdmissions() {
        String expectedResponse = "[{\"id\":1,\"surname\":\"Finley\",\"forename\":\"Sarah\"},{\"id\":2,\"surname\":\"Jackson\",\"forename\":\"Robert\"},{\"id\":5,\"surname\":\"Wicks\",\"forename\":\"Patrick\"}]";
        ResponseEntity<String> mockAllocationsEntity = getAllocations();
        ResponseEntity<String> mockEmployeesEntity = getEmployees();
        ResponseEntity<String> mockSpecificEmployeesEntity1 = getSpecificEmployee(1);
        ResponseEntity<String> mockSpecificEmployeesEntity2 = getSpecificEmployee(2);
        ResponseEntity<String> mockSpecificEmployeesEntity5 = getSpecificEmployee(5);

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Allocations", String.class)).thenReturn(mockAllocationsEntity);

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Employees", String.class)).thenReturn(mockEmployeesEntity);

        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity(API_BASE_URL + "Employees/1", String.class)).thenReturn(mockSpecificEmployeesEntity1);
        when(restTemplate.getForEntity(API_BASE_URL + "Employees/2", String.class)).thenReturn(mockSpecificEmployeesEntity2);
        when(restTemplate.getForEntity(API_BASE_URL + "Employees/5", String.class)).thenReturn(mockSpecificEmployeesEntity5);

        HospitalController hospitalController = new HospitalController(restTemplate);

        ResponseEntity<String> actualResponse = hospitalController.getEmployeesWithZeroAdmissions();

        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
        assertEquals(expectedResponse, actualResponse.getBody());
    }

    private static ResponseEntity<String> getAdmissions() {
        String mockAPIResponse = "[{\"id\":1,\"admissionDate\":\"2020-11-28T16:45:00\",\"dischargeDate\":\"2020-11-28T23:56:00\",\"patientID\":2},{\"id\":2,\"admissionDate\":\"2020-12-07T22:14:00\",\"dischargeDate\":\"0001-01-01T00:00:00\",\"patientID\":1},{\"id\":3,\"admissionDate\":\"2021-09-23T21:50:00\",\"dischargeDate\":\"2021-09-27T09:56:00\",\"patientID\":2},{\"id\":4,\"admissionDate\":\"2024-02-23T21:50:00\",\"dischargeDate\":\"2024-09-27T09:56:00\",\"patientID\":5},{\"id\":5,\"admissionDate\":\"2024-04-12T22:55:00\",\"dischargeDate\":\"2024-04-14T11:36:00\",\"patientID\":5},{\"id\":6,\"admissionDate\":\"2024-04-19T21:50:00\",\"dischargeDate\":\"0001-01-01T00:00:00\",\"patientID\":5}]";

        // Create a ResponseEntity with the expected response and HttpStatus.OK
        return new ResponseEntity<>(mockAPIResponse, HttpStatus.OK);
    }

    private static ResponseEntity<String> getAllocations() {
        String mockAPIResponse = "[{\"id\":1,\"admissionID\":1,\"employeeID\":4,\"startTime\":\"2020-11-28T16:45:00\",\"endTime\":\"2020-11-28T23:56:00\"},{\"id\":2,\"admissionID\":3,\"employeeID\":4,\"startTime\":\"2021-09-23T21:50:00\",\"endTime\":\"2021-09-24T09:50:00\"},{\"id\":3,\"admissionID\":2,\"employeeID\":6,\"startTime\":\"2020-12-07T22:14:00\",\"endTime\":\"2020-12-08T20:00:00\"},{\"id\":4,\"admissionID\":2,\"employeeID\":3,\"startTime\":\"2020-12-08T20:00:00\",\"endTime\":\"2020-12-09T20:00:00\"}]";

        // Create a ResponseEntity with the expected response and HttpStatus.OK
        return new ResponseEntity<>(mockAPIResponse, HttpStatus.OK);
    }

    private static ResponseEntity<String> getEmployees() {
        String mockAPIResponse = "[{\"id\":1,\"surname\":\"Finley\",\"forename\":\"Sarah\"},{\"id\":2,\"surname\":\"Jackson\",\"forename\":\"Robert\"},{\"id\":3,\"surname\":\"Allen\",\"forename\":\"Alice\"},{\"id\":4,\"surname\":\"Jones\",\"forename\":\"Sarah\"},{\"id\":5,\"surname\":\"Wicks\",\"forename\":\"Patrick\"},{\"id\":6,\"surname\":\"Smith\",\"forename\":\"Alice\"}]";
        // Create a ResponseEntity with the expected response and HttpStatus.OK
        return new ResponseEntity<>(mockAPIResponse, HttpStatus.OK);
    }

    private static ResponseEntity<String> getSpecificEmployee( Integer id ) {
        String mockAPIResponse = null;
        if (id == 1){
            mockAPIResponse = "{\"id\":1,\"surname\":\"Finley\",\"forename\":\"Sarah\"}";
        } else if (id == 2 ) {
            mockAPIResponse = "{\"id\":2,\"surname\":\"Jackson\",\"forename\":\"Robert\"}";
        } else if (id == 3 ) {
            mockAPIResponse = "{\"id\":3,\"surname\":\"Allen\",\"forename\":\"Alice\"}";
        }else if (id == 4 ) {
            mockAPIResponse = "{\"id\":4,\"surname\":\"Jones\",\"forename\":\"Sarah\"}";
        }else if (id == 5 ) {
            mockAPIResponse = "{\"id\":5,\"surname\":\"Wicks\",\"forename\":\"Patrick\"}";
        }else if (id == 6 ) {
            mockAPIResponse = "{\"id\":6,\"surname\":\"Smith\",\"forename\":\"Alice\"}";
        }

        // Create a ResponseEntity with the expected response and HttpStatus.OK
        return new ResponseEntity<>(mockAPIResponse, HttpStatus.OK);
    }

    //Test web-service-api works
    /*@Test
    void getSpecificEmployeeTest() {
        // Define the expected response
        String expectedResponse = "{\"id\":1,\"surname\":\"Finley\",\"forename\":\"Sarah\"}";
        // Create a ResponseEntity with the expected response and HttpStatus.OK
        ResponseEntity<String> expectedEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        // Mock the RestTemplate's getForEntity method to return the expected response
        when(restTemplate.getForEntity("/Admissions/1", String.class)).thenReturn(expectedEntity);
        // Call the endpoint
        HospitalController hospitalController = new HospitalController(restTemplate);
        ResponseEntity<String> response = hospitalController.getSpecificEmployeeTest("1");
        // Assert that the status code is OK
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Assert that the response body matches the expected JSON string
        assertEquals(expectedResponse, response.getBody());
    }*/
}


