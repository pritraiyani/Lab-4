package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Single Variable Continuous Regression
 *
 */
public class App {
    public static void main(String[] args) {
        // Array of file names
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};
        
        // Small constant to avoid division by zero in MARE
        double epsilon = 1e-10;
        
        // Iterate over the CSV files for each model
        for (String filePath : files) {
            FileReader filereader;
            List<String[]> allData;
            try {
                filereader = new FileReader(filePath); 
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
                allData = csvReader.readAll();
            } catch (Exception e) {
                System.out.println("Error reading the CSV file: " + filePath);
                return;
            }
            
            // Initialize variables to store the sum of the errors
            double mse = 0.0, mae = 0.0, mare = 0.0;
            int n = allData.size(); // number of rows in the file
            
            // Iterate through each row to calculate the metrics
            for (String[] row : allData) {
                double y_true = Double.parseDouble(row[0]); // true value (first column)
                double y_predicted = Double.parseDouble(row[1]); // predicted value (second column)
                
                // Calculate MSE
                mse += Math.pow(y_true - y_predicted, 2);
                
                // Calculate MAE
                mae += Math.abs(y_true - y_predicted);
                
                // Calculate MARE
                mare += Math.abs(y_true - y_predicted) / (Math.abs(y_true) + epsilon);
            }
            
            // Calculate the averages for MSE, MAE, and MARE
            mse /= n;
            mae /= n;
            mare /= n;
            
            // Print the results for this model
            System.out.println("Results for " + filePath + ":");
            System.out.println("MSE: " + mse);
            System.out.println("MAE: " + mae);
            System.out.println("MARE: " + mare);
            System.out.println();
        }
    }
}
