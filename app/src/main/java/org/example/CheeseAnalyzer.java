package org.example;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CheeseAnalyzer {

    private static int pasteurizedTotal = 0;
    private static int rawTotal = 0;
    private static int organicMoistureTotal = 0;
    private static final HashMap<String, Integer> milkCounts = new HashMap<>();

    public void analyze() {
        String csvFile = "cheese_data.csv";
        String outputFile = "output.txt";


        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             PrintWriter writer = new PrintWriter(new File(outputFile))) {

            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                ArrayList<String> columns = splitCSV(line);

                if (columns.size() >= 10) {
                    calcMilkTreatment(columns);
                    calcOrganicMoisture(columns);
                    calcMilkType(columns);
                }
            }


            writer.println("Pasteurized Milk Cheeses: " + pasteurizedTotal);
            writer.println("Raw Milk Cheeses: " + rawTotal);
            writer.println("Organic Cheeses with >41% Moisture: " + organicMoistureTotal);
            writer.println("Most Common Milk Type: " + getMostCommonMilk());

            System.out.println("Analysis complete. Results saved to output.txt");

        } catch (IOException e) {
            System.out.println("File Error: " + e.getMessage());
        }
    }


    public static ArrayList<String> splitCSV(String line) {
        ArrayList<String> result = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()) {
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentToken.toString().trim());
                currentToken.setLength(0);
            } else {
                currentToken.append(c);
            }
        }
        result.add(currentToken.toString().trim());
        return result;
    }

    public static void calcMilkTreatment(ArrayList<String> row) {
        String treatment = row.get(9);
        if (treatment.equalsIgnoreCase("pasteurized")) {
            pasteurizedTotal++;
        } else if (treatment.equalsIgnoreCase("raw milk")) {
            rawTotal++;
        }
    }

    public static void calcOrganicMoisture(ArrayList<String> row) {
        try {
            double moisture = Double.parseDouble(row.get(3));
            int organic = Integer.parseInt(row.get(6));

            if (organic == 1 && moisture > 41.0) {
                organicMoistureTotal++;
            }
        } catch (Exception ignored) {

        }
    }

    public static void calcMilkType(ArrayList<String> row) {
        String type = row.get(8);
        if (!type.isEmpty()) {
            milkCounts.put(type, milkCounts.getOrDefault(type, 0) + 1);
        }
    }

    public static String getMostCommonMilk() {
        String winner = "None";
        int max = -1;
        for (String type : milkCounts.keySet()) {
            if (milkCounts.get(type) > max) {
                max = milkCounts.get(type);
                winner = type;
            }
        }
        return winner;
    }
}