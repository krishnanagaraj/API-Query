package com.goeuro.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.goeuro.bean.Location;
import com.goeuro.bean.Locations;
import com.goeuro.util.ApiConstants;
import com.goeuro.validator.ErrorRepository;
import com.goeuro.validator.ValidationException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;

/**
 * Handles the get locations API request and response
 */
public class ApiService {

    /**
     * Retrieves the location details
     *
     * @param query
     * @return Locations
     */
    public Locations getLocation(String query) {
        HttpResponse httpResponse = null;

        // Build URL for GoEuro 'Get Location' API
        StringBuilder getLocationApiUrl = new StringBuilder(ApiConstants.GET_LOCATION_API_URL);
        getLocationApiUrl.append(query);
        Locations locations = new Locations();
        try {
            // Make a 'Get Location' API call to GoEuro.
            httpResponse = HttpClient.executeAsGet(getLocationApiUrl.toString());

            // Process response - extract location information.
            String responseBodyAsString = new String(EntityUtils.toByteArray(httpResponse.getEntity()), ApiConstants.UTF_8);
            JSONArray responseBodyAsJsonArray = (JSONArray) JSONSerializer.toJSON(responseBodyAsString);
            if (!responseBodyAsJsonArray.isEmpty()) {
                List<Location> locationList = new ArrayList<Location>();

                for (int i = 0; i < responseBodyAsJsonArray.size(); i++) {
                    JSONObject locationJsonObj = responseBodyAsJsonArray.getJSONObject(i);
                    Location location = new Location();
                    location.setId(Integer.valueOf(locationJsonObj.getString(ApiConstants.ID)));
                    location.setName(locationJsonObj.getString(ApiConstants.NAME));
                    location.setType(locationJsonObj.getString(ApiConstants.TYPE));
                    JSONObject getGeoPositions = locationJsonObj.getJSONObject(ApiConstants.GEO_POSITION);
                    location.setLatitude(getGeoPositions.getString(ApiConstants.LATITUDE));
                    location.setLongitude(getGeoPositions.getString(ApiConstants.LONGITUDE));
                    locationList.add(location);
                }
                locations.setLocations(locationList);
            } else {
                throw new ValidationException(ErrorRepository.INVALID_QUERY_PARAMETER.getErrorMessage());
            }
        }  catch (ValidationException e) {
            System.out.println(e.getErrorResponse());
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            releaseConnection(httpResponse);
        }
        return locations;
    }

    /**
     * This function ensures that the entity content is fully consumed and the content stream, if it exists, is closed.
     *
     * @param response
     */
    private static void releaseConnection(HttpResponse response) {
        if (response != null) {
            if (response.getEntity() != null) {
                try {
                    response.getEntity().consumeContent();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
    }

}
