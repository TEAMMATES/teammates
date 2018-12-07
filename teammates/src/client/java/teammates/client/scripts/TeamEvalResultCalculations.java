package teammates.client.scripts;

import static teammates.common.datatransfer.TeamEvalResult.NSB;
import static teammates.common.datatransfer.TeamEvalResult.NSU;

import teammates.common.datatransfer.TeamEvalResult;

/**
 * Displays the step-by-step calculations for contribution question results.
 */
public final class TeamEvalResultCalculations {

    private TeamEvalResultCalculations() {
        // script-like, not meant to be instantiated
    }

    private static void showCalculationSteps(int[][] input) {
        TeamEvalResult t = new TeamEvalResult(input);
        System.out.println(t.toString());
    }

    public static void main(String[] args) {
        int[][] input = {
                { 100, 100, 110 },
                { 100, 100, 110 },
                { 100, 100, 110 }
        };

        showCalculationSteps(input);

        // CHECKSTYLE.OFF:SingleSpaceSeparator vertical alignment of values for readability
        int[][] input2 = {
                { 100, 100, 100, 100 },
                { 110, 110, NSU, 110 },
                { NSB, NSB, NSB, NSB },
                {  70,  80, 110, 120 }
        };

        showCalculationSteps(input2);

        int[][] input3 = {
                { 103, 103,  94 },
                {  90, 110, NSU },
                { 100,  90, 110 }
        };
        // CHECKSTYLE.ON:SingleSpaceSeparator

        showCalculationSteps(input3);

        int[][] input4 = {
                { 110, 120, 130 },
                { 210, 220, 230 },
                { 310, 320, 330 }
        };

        showCalculationSteps(input4);

    }

}
