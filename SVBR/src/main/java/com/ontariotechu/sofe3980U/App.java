package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App {
    public static void main(String[] args) {
        // Array of CSV files for different models
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};
        
        // Iterate over each file
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

            // Initialize variables
            double bce = 0.0;
            int tp = 0, fp = 0, tn = 0, fn = 0;
            double accuracy = 0.0, precision = 0.0, recall = 0.0, f1Score = 0.0;
            List<Double> truePositivesRate = new ArrayList<>();
            List<Double> falsePositivesRate = new ArrayList<>();
            int n = allData.size();
            
            // Iterate through the data to calculate metrics
            for (String[] row : allData) {
                int y_true = Integer.parseInt(row[0]);  // True value (0 or 1)
                double y_predicted = Double.parseDouble(row[1]);  // Predicted value (continuous between 0 and 1)
                
                // Apply threshold to get binary predicted value
                int predictedBinary = y_predicted >= 0.5 ? 1 : 0;

                // Calculate Binary Cross-Entropy
                bce += y_true * Math.log(y_predicted) + (1 - y_true) * Math.log(1 - y_predicted);

                // Update confusion matrix counts
                if (y_true == 1 && predictedBinary == 1) tp++;  // True Positive
                if (y_true == 0 && predictedBinary == 1) fp++;  // False Positive
                if (y_true == 0 && predictedBinary == 0) tn++;  // True Negative
                if (y_true == 1 && predictedBinary == 0) fn++;  // False Negative
            }

            // Calculate average BCE
            bce = -bce / n;

            // Calculate Accuracy, Precision, Recall, F1 Score
            accuracy = (double) (tp + tn) / (tp + tn + fp + fn);
            precision = (double) tp / (tp + fp);
            recall = (double) tp / (tp + fn);
            f1Score = 2 * (precision * recall) / (precision + recall);

            // Calculate AUC-ROC using thresholds from 0 to 1
            double auc = calculateAUC(allData);

            // Print the results for this model
            System.out.println("Results for " + filePath + ":");
            System.out.println("BCE: " + bce);
            System.out.println("Accuracy: " + accuracy);
            System.out.println("Precision: " + precision);
            System.out.println("Recall: " + recall);
            System.out.println("F1 Score: " + f1Score);
            System.out.println("AUC-ROC: " + auc);
            System.out.println();
        }
    }

    // Method to calculate AUC-ROC for the model
private static double calculateAUC(List<String[]> allData) {
    // List to store true positive rate (TPR) and false positive rate (FPR)
    List<Double> tprList = new ArrayList<>();
    List<Double> fprList = new ArrayList<>();

    // Generate a list of all possible thresholds (0 to 1)
    for (int i = 0; i <= 100; i++) {
        double threshold = i / 100.0;
        int tp = 0, fp = 0, tn = 0, fn = 0;

        // Apply the threshold to calculate TPR and FPR
        for (String[] row : allData) {
            int y_true = Integer.parseInt(row[0]);
            double y_predicted = Double.parseDouble(row[1]);

            // Apply threshold to make binary prediction
            int predictedBinary = y_predicted >= threshold ? 1 : 0;

            // Confusion matrix logic
            if (y_true == 1 && predictedBinary == 1) tp++;
            if (y_true == 0 && predictedBinary == 1) fp++;
            if (y_true == 0 && predictedBinary == 0) tn++;
            if (y_true == 1 && predictedBinary == 0) fn++;
        }

        // Calculate TPR and FPR
        double tpr = (double) tp / (tp + fn);  // True Positive Rate
        double fpr = (double) fp / (fp + tn);  // False Positive Rate

        // Store the values
        tprList.add(tpr);
        fprList.add(fpr);
    }

    // Calculate AUC using the trapezoidal rule
    double auc = 0.0;
    for (int i = 1; i < tprList.size(); i++) {
        double x1 = fprList.get(i - 1);
        double x2 = fprList.get(i);
        double y1 = tprList.get(i - 1);
        double y2 = tprList.get(i);

        // Trapezoidal rule for AUC calculation
        auc += (y1 + y2) * Math.abs(x2 - x1) / 2;
    }

    return auc;
}
}
