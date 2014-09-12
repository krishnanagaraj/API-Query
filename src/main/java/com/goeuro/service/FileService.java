package com.goeuro.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.goeuro.bean.Location;
import com.goeuro.util.ApiConstants;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Handles the CSV file creation and modification
 */
public class FileService {

    /**
     * Creates a CSV file.
     *
     * @param locations
     */
    public void createCSVFileForLocations(List<Location> locations) {

        CSVFormat csvFormat = CSVFormat.RFC4180.withHeader().withDelimiter(',');

        try {
            String path = System.getProperty("user.dir");
            FileWriter fileWriter = new FileWriter(path.concat(File.separator).concat("location.csv"));
            CSVPrinter locationCsv = new CSVPrinter(fileWriter, csvFormat);
            locationCsv.printRecord("id", ApiConstants.NAME, ApiConstants.TYPE, ApiConstants.LATITUDE, ApiConstants.LONGITUDE);
            for (Location location : locations) {
                List<String> locationInfo = new ArrayList<String>();
                locationInfo.add(String.valueOf(location.getId()));
                locationInfo.add(location.getName());
                locationInfo.add(location.getType());
                locationInfo.add(location.getLatitude());
                locationInfo.add(location.getLongitude());
                locationCsv.printRecord(locationInfo);
            }
            locationCsv.flush();
            locationCsv.close();

            System.out.print(String.format("Successfully created locations.csv file. File can be found here: %s", path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
