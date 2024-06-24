package org.example.frontendapp;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FrontendControllerTest {

    @BeforeEach
    void setUp() {
        FrontendController frontendController = new FrontendController();
    }

    @Tag("integrationTest")
    @Test
    void testFetchDataFromBackendIntegration() throws JSONException {
        // Assuming the API is running and reachable
        String patientID = "123"; // Provide a valid patient ID for testing

        // Adjusted JSON response with integer admissionId
        String jsonResponse = "[{\"admissionId\":2,\"admissionDate\":\"2020-12-07T22:14:00\",\"dischargeDate\":\"0001-01-01T00:00:00\"}]";

        List<String[]> admissionData = FrontendController.parseJsonResponse(jsonResponse);

        assertNotNull(admissionData);
        assertFalse(admissionData.isEmpty());

        // Assuming each admission consists of admissionId, admissionDate, and dischargeDate
        for (String[] admission : admissionData) {
            assertNotNull(admission);
            assertEquals(3, admission.length);
            assertNotNull(admission[0]); // admissionId
            assertNotNull(admission[1]); // admissionDate
            assertNotNull(admission[2]); // dischargeDate
        }
    }

    @Tag("unitTest")
    @Test
    void testFetchDataFromBackendHTTPError() {
        String patientID = "-1";

        List<String[]> admissionData = FrontendController.fetchDataFromBackend(patientID);

        assertNull(admissionData);
    }

    @Tag("cornerCase")
    @Test
    void testFetchDataFromBackendMinimumValue() {
        String patientID = "-1";

        List<String[]> admissionData = FrontendController.fetchDataFromBackend(patientID);

        assertNull(admissionData);
    }

    @Tag("unitTest")
    @Tag("cornerCase")
    @Test
    void testFetchDataFromBackendMaximumValue() {
        String patientID = "10000000000000000000000000000000000000000000000";

        List<String[]> admissionData = FrontendController.fetchDataFromBackend(patientID);

        assertNull(admissionData);
    }

    @Tag("edgeCase")
    @Test
    void testFetchDataFromBackendInvalidJSON() {
        String patientID = "12.3";

        List<String[]> admissionData = FrontendController.fetchDataFromBackend(patientID);

        assertNull(admissionData);
    }

}
