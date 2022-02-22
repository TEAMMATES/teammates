package teammates.logic.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CommonData {

     static boolean[] branchReached = new boolean[50];

     public static void printVisitedBranches() {

          int nbBranchesCovered = 0;
          try {
               File file = new File("branchesCoveredInGetRecipientsOfQuestion.txt");
               file.createNewFile();
               PrintWriter writer = new PrintWriter(file);
               for (int i = 0; i < branchReached.length; i++) {
                    writer.println(branchReached[i]);
                    System.out.println(branchReached[i]);
                    if (branchReached[i]) {
                         writer.println(i);
                         nbBranchesCovered += 1;
                    }
               }
               writer.println("nbBranchesCovered: " + nbBranchesCovered);
               writer.println("branchReached: " + branchReached.length);
               writer.println("Coverage: " + (float) nbBranchesCovered / branchReached.length);
               System.out.println("Coverage: " + (float) nbBranchesCovered / branchReached.length);
               writer.close();
          } catch (IOException e) {
               e.printStackTrace();
          }

     }

}
