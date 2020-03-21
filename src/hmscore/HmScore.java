package hmscore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author Boyoon Choi
 */

/* The method below accepts a vcf file input, and 
homozygosity score, one of BSA method, per each chromosome number could be acquired using the method */ 

public class HmScore {

    public static void main(String[] args) {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.showOpenDialog(new JFrame());
        File files = chooser.getSelectedFile();

        try {
            BufferedReader br = new BufferedReader(new FileReader(files));
            String line = "";
            List<List<String>> homScoreList = new ArrayList<>(); // Creat arraylist to store chromosomes and their respective scores
            
            // Declare and intialize variable
            double homozyous = 0;
            double hetrozygous = 0;
            double oldHomozygous = 0;
            double oldHeterozygous = 0;
            String Chroms = "";
            String chromNum = "";
            int count = -1;
            boolean same = true;
            boolean first = true;
            Double homScore = 0.0;
            while (((line = br.readLine()) != null)) {
                if (!line.matches("#.*") && !line.matches("##.*")) {
                    String[] columnsName = line.split("\\s");
                    chromNum = columnsName[0];
                    
                    // Assign the first unique chromosome to "Chroms"
                    if (Chroms.equals("") || !Chroms.equals(chromNum)) { 
                        Chroms = chromNum; 
                        homScoreList.add(new ArrayList<>(Arrays.asList(Chroms))); // Assign "Chroms" to "homScoreList" arraylist
                        // Declare variable to acquire current and old homozygous and heterozygous
                        oldHomozygous = homozyous;
                        oldHeterozygous = hetrozygous;
                        homozyous = 0;
                        hetrozygous = 0;
                        count++;
                        same = false;
                    } else {
                        same = true;
                    }

                    if (!columnsName[9].contains("./.")) {
                        String[] info = columnsName[9].split(":");
                        if (info.length == 5) {  // Check 9th column has 5 variables
                            String[] SNPCOL = info[1].split(",");
                            String[] AD = SNPCOL[1].split(",");
                            List<String> Alternate = new ArrayList<>(Arrays.asList(AD)); // Assign "AD" to "Alternate" arraylist
                            String[] genotype = info[0].split("/");
                            String X = genotype[0];
                            String Y = genotype[1];
                            if (X.equals(Y)) {
                                String Homo = Alternate.get(0);
                                homozyous += Double.parseDouble(Homo);
                            } else {
                                String hetro = Alternate.get(0);
                                hetrozygous += Double.parseDouble(hetro);
                            }
                            if ((!same && !first && !(oldHomozygous == 0.0))) { // Get homozygosity score of chromosome 00-11  
                                homScore = oldHomozygous / (oldHomozygous + oldHeterozygous);
                                homScoreList.get(count - 1).add(homScore + "\n");// Add homozygosity score in "homScoreList" 
                            }
                        }
                    }
                }
                if (first) {
                    first = false;
                }
            }
            
            // Get homozygosity score of the last chromosome outside while loop
            homScore = homozyous / (homozyous + hetrozygous);
            homScoreList.get(count).add(homScore + "");
            System.out.println(homScoreList);

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
