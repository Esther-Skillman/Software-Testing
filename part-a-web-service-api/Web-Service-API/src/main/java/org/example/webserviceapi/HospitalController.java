package org.example.webserviceapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
public class HospitalController {

    private final String API_BASE_URL = "https://web.socem.plymouth.ac.uk/COMP2005/api/";

    private final RestTemplate restTemplate;

    public HospitalController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Retrieve data from an API endpoint
    private ResponseEntity<String> fetchDataFromEndpoint(String endpoint) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(API_BASE_URL + endpoint, String.class);

        if (responseEntity.getBody() == null || responseEntity.getBody().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    // Common method to map JSON response to list of maps
    private List<Map<String, Object>> mapResponseToListOfMaps(ResponseEntity<String> responseEntity) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
    }

    // Common method to convert list of maps to JSON string
    private String convertListOfMapsToJsonString(List<Map<String, Object>> data) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(data);
    }

    // F1 - A list of all admissions for a specific patient
    @GetMapping("/Admission/{patientID}")
    public ResponseEntity<String> getAdmissionsForSpecificPatient(@PathVariable String patientID) {
        ResponseEntity<String> responseEntity = fetchDataFromEndpoint("Admissions");

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return responseEntity;
        }

        String responseBody = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<Map<String, Object>> admissions = mapResponseToListOfMaps(responseEntity);

            List<Map<String, Object>> filteredAdmissions = admissions.stream()
                    .filter(admission -> admission.containsKey("patientID") && admission.get("patientID").toString().equals(patientID))
                    .toList();


            if (filteredAdmissions.isEmpty()) {
                return new ResponseEntity<>("No admissions for patient found with ID " + patientID, HttpStatus.NOT_FOUND);
            }


            List<Map<String, Object>> formattedAdmissions = new ArrayList<>();
            for (Map<String, Object> admission : filteredAdmissions) {
                LinkedHashMap<String, Object> formattedAdmission = new LinkedHashMap<>();
                formattedAdmission.put("admissionId", admission.get("id"));
                formattedAdmission.put("admissionDate", admission.get("admissionDate"));
                formattedAdmission.put("dischargeDate", admission.get("dischargeDate"));
                formattedAdmissions.add(formattedAdmission);
            }

            String filteredResponse = convertListOfMapsToJsonString(formattedAdmissions);

            return new ResponseEntity<>(filteredResponse, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // F2 - A list of patients currently admitted (who have not been discharged yet)
    @GetMapping("/CurrentAdmissions")
    public ResponseEntity<String> getCurrentAdmissions() {
        ResponseEntity<String> responseEntity = fetchDataFromEndpoint("Admissions");

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return responseEntity;
        }

        String responseBody = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<Map<String, Object>> admissions = mapResponseToListOfMaps(responseEntity);

            List<Map<String, Object>> filteredAdmissions = admissions.stream()
                    .filter(admission -> admission.containsValue("0001-01-01T00:00:00")).toList();

            if (filteredAdmissions.isEmpty()) {
                return new ResponseEntity<>("No patients currently admitted", HttpStatus.NOT_FOUND);
            }

            List<Map<String, Object>> formattedAdmissions = new ArrayList<>();
            for (Map<String, Object> admission : filteredAdmissions) {
                LinkedHashMap<String, Object> formattedAdmission = new LinkedHashMap<>();
                formattedAdmission.put("patientID", admission.get("id"));
                formattedAdmission.put("admissionDate", admission.get("admissionDate"));
                formattedAdmissions.add(formattedAdmission);
            }

            String filteredResponse = convertListOfMapsToJsonString(formattedAdmissions);

            return new ResponseEntity<>(filteredResponse, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // F3 - Identify which member of staff has the most admissions
    @GetMapping("/EmployeeWithMostAdmissions")
    public ResponseEntity<String> getEmployeeWithMostAdmissions() {
        ResponseEntity<String> allocationsResponse = fetchDataFromEndpoint("Allocations");

        if (allocationsResponse.getStatusCode() != HttpStatus.OK) {
            return allocationsResponse;
        }

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> allocations;

        try {
            allocations = mapResponseToListOfMaps(allocationsResponse);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<Integer> employeeIDs = allocations.stream()
                .map(admission -> (Integer) admission.get("employeeID"))
                .toList();

        Map<Integer, Long> counts = employeeIDs.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Optional<Map.Entry<Integer, Long>> maxEntry = counts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        if (maxEntry.isPresent()) {
            Integer employeeID = maxEntry.get().getKey();
            ResponseEntity<String> employeeResponse = restTemplate.getForEntity(API_BASE_URL + "Employees/" + employeeID, String.class);
            if (employeeResponse.getBody() != null && !employeeResponse.getBody().isEmpty()) {
                return new ResponseEntity<>(employeeResponse.getBody(), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // F4 - A list of staff who have no (zero) admissions
    @GetMapping("/EmployeesWithZeroAdmissions")
    public ResponseEntity<String> getEmployeesWithZeroAdmissions() {
        ResponseEntity<String> allocationsResponse = fetchDataFromEndpoint("Allocations");
        if (allocationsResponse.getStatusCode() != HttpStatus.OK ) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ResponseEntity<String> employeesResponse = fetchDataFromEndpoint("Employees");
        if ( employeesResponse.getStatusCode() != HttpStatus.OK ) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> admissions;
        List<Map<String, Object>> employees;

        try {
            admissions = mapResponseToListOfMaps(allocationsResponse);
            employees = mapResponseToListOfMaps(employeesResponse);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Set<Integer> employeeIDsInAdmissions = admissions.stream()
                .map(admission -> (Integer) admission.get("employeeID"))
                .collect(Collectors.toSet());

        List<Map<String, Object>> employeesWithZeroAdmissions = employees.stream()
                .filter(employee -> !employeeIDsInAdmissions.contains(employee.get("id")))
                .toList();

        List<Map<String, Object>> employeeDetails = new ArrayList<>();
        for (Map<String, Object> employee : employeesWithZeroAdmissions) {
            Integer employeeID = (Integer) employee.get("id");
            ResponseEntity<String> employeeResponse = restTemplate.getForEntity(API_BASE_URL + "Employees/" + employeeID, String.class);
            if (employeeResponse.getBody() != null) {
                try {
                    Map<String, Object> employeeDetail = mapper.readValue(employeeResponse.getBody(), new TypeReference<>() {});
                    employeeDetails.add(employeeDetail);
                } catch (IOException e) {
                    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }

        String jsonResponse;
        try {
            jsonResponse = convertListOfMapsToJsonString(employeeDetails);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
    }


    //Test web-service-api works
    /*@GetMapping("/Employees/{id}")
    public ResponseEntity<String> getSpecificEmployeeTest( @PathVariable String id) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = API_BASE_URL + "Employees/" + id;
        return restTemplate.getForEntity(apiUrl, String.class);
    }*/
}






