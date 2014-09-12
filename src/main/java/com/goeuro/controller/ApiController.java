package com.goeuro.controller;

import com.goeuro.bean.ApiBean;
import com.goeuro.bean.Locations;
import com.goeuro.service.ApiService;
import com.goeuro.service.FileService;
import com.goeuro.validator.ApiValidator;
import com.goeuro.validator.ErrorRepository;
import com.goeuro.validator.ValidationException;

/**
 * Handles the get location API request
 */
public class ApiController {

    /**
     * Core method handling the location API request
     *
     * @param args
     */
    public static void main(String args[]) {
        try {
            if (args.length > 0) {
                ApiBean apiBean = new ApiBean();
                apiBean.setQuery(args[0]);

                // Validate the provided input
                new ApiValidator(apiBean).validate();

                // Get the location information
                ApiService apiService = new ApiService();
                Locations locations = apiService.getLocation(apiBean.getQuery());

                // Create a CSV file for locations
                new FileService().createCSVFileForLocations(locations.getLocations());
            } else {
                System.out.println(ErrorRepository.INVALID_QUERY_PARAMETER.getErrorMessage());
            }
        } catch (ValidationException e) {
            System.out.println(e.getErrorResponse());
        }
    }
}
