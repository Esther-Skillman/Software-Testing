package org.example.frontendapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FrontendController {

    static List<String[]> fetchDataFromBackend(String patientID) {
        List<String[]> admissionData = new ArrayList<>();
        try {
            URL url = new URL("http://localhost:8080/Admission/" + patientID);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Print the JSON response for inspection
                System.out.println("JSON Response: " + response.toString());

                // Process the JSON response and extract admission data
                JSONArray admissions = new JSONArray(response.toString());
                for (int i = 0; i < admissions.length(); i++) {
                    JSONObject admission = admissions.getJSONObject(i);
                    // Verify JSON structure
                    if (admission.has("admissionId") && admission.has("admissionDate") && admission.has("dischargeDate")) {
                        Integer admissionId = admission.getInt("admissionId");
                        String admissionDate = admission.getString("admissionDate");
                        String dischargeDate = admission.getString("dischargeDate");
                        String admissionIdString = String.valueOf(admission.getInt("admissionId"));

                        admissionData.add(new String[]{admissionIdString, admissionDate, dischargeDate});
                    } else {
                        // Handle missing data
                        System.out.println("Missing data in JSON for admission: " + i);
                    }
                }

                return admissionData;
            } else {
                // Handle HTTP error response
                System.out.println("Error fetching data from backend: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }
    static List<String[]> parseJsonResponse(String jsonResponse) throws JSONException {
        List<String[]> admissionData = new ArrayList<>();
        JSONArray admissions = new JSONArray(jsonResponse);
        for (int i = 0; i < admissions.length(); i++) {
            JSONObject admission = admissions.getJSONObject(i);
            String admissionId = String.valueOf(admission.getInt("admissionId")); // Convert to string
            String admissionDate = admission.getString("admissionDate");
            String dischargeDate = admission.getString("dischargeDate");
            admissionData.add(new String[]{admissionId, admissionDate, dischargeDate});
        }
        return admissionData;
    }
}
