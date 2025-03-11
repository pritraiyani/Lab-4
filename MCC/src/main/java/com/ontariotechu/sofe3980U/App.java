package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App {
    public static void main(String[] args) {
        String filePath = "model.csv";  // Path to model.csv
        FileReader filereader;
        List<String[]> allData;
        
        try {
            filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file");
            return;
        }

        // Initialize variables for Cross-Entropy and Confusion Matrix
        double ce = 0.0;
        int[][] confusionMatrix = new int[5][5]; // For 5 classes (y1, y2, y3, y4, y5)

        // Iterate through each row to calculate the metrics
        for (String[] row : allData) {
            int trueClass = Integer.parseInt(row[0]);  // True class (y1 to y5)
            double[] predictedProbs = new double[5];
            
            // Fill the predicted probabilities (y^1, y^2, y^3, y^4, y^5)
            for (int i = 0; i < 5; i++) {
                predictedProbs[i] = Double.parseDouble(row[i + 1]);  // Columns 2 to 6 are probabilities
            }
            
            // Calculate Cross-Entropy
            ce -= Math.log(predictedProbs[trueClass - 1]);  // Log of the predicted probability for the true class

            // Update Confusion Matrix
            int predictedClass = getMaxIndex(predictedProbs) + 1;  // Find the class with max probability
            confusionMatrix[trueClass - 1][predictedClass - 1]++;
        }

        // Calculate average Cross-Entropy
        ce /= allData.size();

        // Print Cross-Entropy and Confusion Matrix
        System.out.println("Cross-Entropy: " + ce);
        printConfusionMatrix(confusionMatrix);
    }

    // Helper method to get the index of the maximum value in an array
    private static int getMaxIndex(double[] probs) {
        int maxIndex = 0;
        for (int i = 1; i < probs.length; i++) {
            if (probs[i] > probs[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    // Helper method to print the confusion matrix
    private static void printConfusionMatrix(int[][] matrix) {
        System.out.println("Confusion Matrix:");
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
