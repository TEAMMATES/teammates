package teammates.client.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.logging.Logger;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.util.Utils;
import teammates.logic.api.Logic;
import teammates.storage.datastore.Datastore;
import teammates.test.driver.TestProperties;
import teammates.common.util.FileHelper;

public class GenerateLargeScaledData extends RemoteApiClient{
    private static Logger logger = Logger.getLogger(GenerateLargeScaledData.class.getName());
    
    public static void main(String[] args) throws IOException {
        GenerateLargeScaledData dataGenerator = new GenerateLargeScaledData();
        dataGenerator.doOperationRemotely();
    }
    
    final static int NUM_STUDENTS = 500;
    final static int NUM_QUESTIONS = 20;
    
    private static void generateData(){
        try {
            File file = new File(TestProperties.TEST_DATA_FOLDER + "/LargeScaleTest.json");
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.println("{");
            /*
            printAccounts(printWriter);
            printCourses(printWriter);
            printInstructors(printWriter);
            */
            printStudents(printWriter);
            /*
            printSessions(printWriter);
            printQuestions(printWriter);
            */
            printReponses(printWriter);
            printWriter.println("}");
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void printAccounts(PrintWriter out){
        printIndent(out, 1);
        out.println("\"accounts\":{");
        printIndent(out, 2);
        out.println("\"imouto1994\":{");
        printIndent(out, 3);
        out.println("\"googleId\":\"imouto1994\",");
        printIndent(out, 3);
        out.println("\"name\":\"Large Scale Instructor\",");
        printIndent(out, 3);
        out.println("\"isInstructor\":true,");
        printIndent(out, 3);
        out.println("\"email\":\"imouto1994@gmail.com\",");
        printIndent(out, 3);
        out.println("\"institute\":\"National University of Singapore\"");
        printIndent(out, 2);
        out.println("}");
        printIndent(out, 1);
        out.println("},");
    }
    
    private static void printCourses(PrintWriter out){
        printIndent(out, 1);
        out.println("\"courses\":{");
        printIndent(out, 2);
        out.println("\"LargeScaleT.CS2103\":{");
        printIndent(out, 3);
        out.println("\"id\":\"LargeScaleT.CS2103\",");
        printIndent(out, 3);
        out.println("\"name\":\"Software Engineering\",");
        printIndent(out, 3);
        out.println("\"isArchived\":false");
        printIndent(out, 2);
        out.println("}");
        printIndent(out, 1);
        out.println("},");
    }

    private static void printInstructors(PrintWriter out){
        printIndent(out, 1);
        out.println("\"instructors\":{");
        printIndent(out, 2);
        out.println("\"imouto1994\":{");
        printIndent(out, 3);
        out.println("\"googleId\":\"imouto1994\",");
        printIndent(out, 3);
        out.println("\"courseId\":\"LargeScaleT.CS2103\",");
        printIndent(out, 3);
        out.println("\"name\":\"Large Scale Instructor\",");
        printIndent(out, 3);
        out.println("\"email\":\"imouto1994@gmail.com\",");
        printIndent(out, 3);
        out.println("\"role\":\"Co-owner\",");
        printIndent(out, 3);
        out.println("\"displayedName\":\"Co-owner\",");
        printIndent(out, 3);
        out.println("\"instructorPrivilegesAsText\":{" ); 
        printIndent(out, 4);
        out.println("\"courseLevel\":{");
        printIndent(out, 5);
        out.println("\"canviewstudentinsection\": true,");    
        printIndent(out, 5);
        out.println("\"cangivecommentinsection\": true,");
        printIndent(out, 5);
        out.println("\"cansubmitsessioninsection\": true,");
        printIndent(out, 5);
        out.println("\"canmodifysessioncommentinsection\": true,");
        printIndent(out, 5);
        out.println("\"canmodifycommentinsection\": true,");
        printIndent(out, 5);
        out.println("\"canmodifycourse\": true,");
        printIndent(out, 5);
        out.println("\"canviewsessioninsection\": true,");
        printIndent(out, 5);
        out.println("\"canmodifysession\": true,");
        printIndent(out, 5);
        out.println("\"canviewcommentinsection\": true,");
        printIndent(out, 5);
        out.println("\"canmodifystudent\": true,");
        printIndent(out, 5);
        out.println("\"canmodifyinstructor\": true");
        printIndent(out, 4);
        out.println("},");
        printIndent(out, 4);
        out.println("\"sectionLevel\": {},");
        printIndent(out, 4);
        out.println("\"sessionLevel\": {}");
        printIndent(out, 3);
        out.println("}");
        printIndent(out, 2);
        out.println("}");
        printIndent(out, 1);
        out.println("},");
    }
    
    private static void printStudents(PrintWriter out){
        printIndent(out, 1);
        out.println("\"students\":{");
        for(int i = 300; i < NUM_STUDENTS; i++){
            printIndent(out, 2);
            out.println("\"LargeScaleT.student" + i + "\":{");
            printIndent(out, 3);
            out.println("\"googleId\":\"LargeScaleT.student" + i + "\",");
            printIndent(out, 3);
            out.println("\"email\":\"LargeScaleT.student" + i + "@gmail.com\",");
            printIndent(out, 3);
            out.println("\"course\":\"LargeScaleT.CS2103\",");
            printIndent(out, 3);
            out.println("\"name\":\"Student Name" +  i + "\",");
            printIndent(out, 3);
            out.println("\"comments\":\"\",");
            printIndent(out, 3);
            out.println("\"team\":\"" + determineTeam(i) + "\",");
            printIndent(out, 3);
            out.println("\"section\":\"" + determineSection(i) + "\"");
            printIndent(out, 2);
            if(i < NUM_STUDENTS - 1){
                out.println("},");
            } else {
                out.println("}");
            }
        }
        printIndent(out, 1);
        out.println("},");
    }
    
    private static void printSessions(PrintWriter out){
        printIndent(out, 1);
        out.println("\"feedbackSessions\":{");
        printIndent(out, 2);
        out.println("\"Open Session\":{");
        printIndent(out, 3);
        out.println("\"feedbackSessionName\":\"Large Scale Session\",");
        printIndent(out, 3);
        out.println("\"courseId\":\"LargeScaleT.CS2103\",");
        printIndent(out, 3);
        out.println("\"creatorEmail\":\"imouto1994@gmail.com\",");
        printIndent(out, 3);
        out.println("\"instructions\":{");
        printIndent(out, 4);
        out.println("\"value\":\"Instructions for Large Scale Session\"");
        printIndent(out, 3);
        out.println("},");
        printIndent(out, 3);
        out.println("\"createdTime\":\"2012-04-01 11:59 PM UTC\",");
        printIndent(out, 3);
        out.println("\"startTime\":\"2012-04-01 11:59 PM UTC\",");
        printIndent(out, 3);
        out.println("\"endTime\":\"2016-04-30 11:59 PM UTC\",");
        printIndent(out, 3);
        out.println("\"sessionVisibleFromTime\":\"2012-04-01 11:59 PM UTC\",");
        printIndent(out, 3);
        out.println("\"resultsVisibleFromTime\":\"2012-05-01 11:59 PM UTC\",");
        printIndent(out, 3);
        out.println("\"timeZone\":8.0,");
        printIndent(out, 3);
        out.println("\"gracePeriod\":10,");
        printIndent(out, 3);
        out.println("\"feedbackSessionType\":\"STANDARD\",");
        printIndent(out, 3);
        out.println("\"sentOpenEmail\":true,");
        printIndent(out, 3);
        out.println("\"sentPublishedEmail\":true");
        printIndent(out, 2);
        out.println("}");
        printIndent(out, 1);
        out.println("},");
    }
    
    private static void printQuestions(PrintWriter out){
        printIndent(out, 1);
        out.println("\"feedbackQuestions\":{");
        for(int i = 0; i < NUM_QUESTIONS; i++){
            printIndent(out, 2);
            out.println("\"question" + i + "\":{");
            printIndent(out, 3);
            out.println("\"feedbackSessionName\":\"Large Scale Session\",");
            printIndent(out, 3);
            out.println("\"courseId\":\"LargeScaleT.CS2103\",");
            printIndent(out, 3);
            out.println("\"creatorEmail\":\"imouto1994@gmail.com\",");
            printIndent(out, 3);
            out.println("\"questionMetaData\":{");
            printIndent(out, 4);
            out.println("\"value\":\"Info for question " + i + "\"");
            printIndent(out, 3);
            out.println("},");
            printIndent(out, 3);
            out.println("\"questionNumber\":\"" + (i + 1) + "\",");
            printIndent(out, 3);
            out.println("\"questionType\":\"" + determineQuestionType(i) + "\",");
            printIndent(out, 3);
            out.println("\"giverType\":\"" + determineGiverType(i) + "\",");
            printIndent(out, 3);
            out.println("\"recipientType\":\"" + determineRecipientType(i) + "\",");
            printIndent(out, 3);
            out.println("\"numberOfEntitiesToGiveFeedbackTo\":" + determineNumberOfEntities(i) + ",");
            printIndent(out, 3);
            out.println("\"showResponsesTo\":[");
            printIndent(out, 4);
            out.println("\"INSTRUCTORS\",");
            printIndent(out, 4);
            out.println("\"RECEIVER\"");
            printIndent(out, 3);
            out.println("],");
            printIndent(out, 3);
            out.println("\"showGiverNameTo\":[");
            printIndent(out, 4);
            out.println("\"INSTRUCTORS\",");
            printIndent(out, 4);
            out.println("\"RECEIVER\"");
            printIndent(out, 3);
            out.println("],");
            printIndent(out, 3);
            out.println("\"showRecipientNameTo\":[");
            printIndent(out, 4);
            out.println("\"INSTRUCTORS\",");
            printIndent(out, 4);
            out.println("\"RECEIVER\"");
            printIndent(out, 3);
            out.println("]");
            printIndent(out, 2);
            if(i < NUM_QUESTIONS - 1){
                out.println("},");
            } else{
                out.println("}");
            }
        }
        printIndent(out, 1);
        out.println("},");
    }
    
    private static void printReponses(PrintWriter out){
        printIndent(out, 1);
        out.println("\"feedbackResponses\":{");
        int index = 0;
        Random rnd = new Random();
        for(int questionIdx = 0; questionIdx < NUM_QUESTIONS; questionIdx++){
            for(int studentIdx = 300; studentIdx < NUM_STUDENTS; studentIdx++){
                printIndent(out, 2);
                out.println("\"response" + index + "\":{");
                printIndent(out, 3);
                out.println("\"feedbackSessionName\":\"Large Scale Session\",");
                printIndent(out, 3);
                out.println("\"courseId\":\"LargeScaleT.CS2103\",");
                printIndent(out, 3);
                out.println("\"feedbackQuestionId\":\"" + (questionIdx + 1) + "\",");
                printIndent(out, 3);
                out.println("\"feedbackQuestionType\":\"" + determineQuestionType(questionIdx) + "\",");
                printIndent(out, 3);
                out.println("\"giverEmail\":\"LargeScaleT.student" + studentIdx + "@gmail.com\",");
                printIndent(out, 3);
                out.println("\"giverSection\":\"" + determineSection(studentIdx) + "\",");
                int recipientIdx =  (studentIdx / 100) * 100 + rnd.nextInt(100);
                printIndent(out, 3);
                out.println("\"recipientEmail\":\"LargeScaleT.student" + recipientIdx + "@gmail.com\",");
                printIndent(out, 3);
                out.println("\"recipientSection\":\"" + determineSection(recipientIdx) + "\",");
                printIndent(out, 3);
                out.println("\"responseMetaData\":{");
                printIndent(out, 4);
                out.println("\"value\":\"Feedback for question " + questionIdx + " from student " + studentIdx + " to student " + recipientIdx + "\"");
                printIndent(out, 3);
                out.println("}");
                printIndent(out, 2);
                if(questionIdx == NUM_QUESTIONS - 1 && studentIdx == NUM_STUDENTS - 1){
                    out.println("}");
                } else {
                    out.println("},");
                }
                index++;
            }
        }
        printIndent(out, 1);
        out.println("}");
    }

    private static String determineQuestionType(int num){
        switch(num){
            default:
                return "TEXT";
        }
    }

    private static String determineGiverType(int num){
        switch(num){
            default:
                return "STUDENTS";
        }
    }

    private static String determineRecipientType(int num){
        switch(num){
            default:
                return "STUDENTS";
        }
    }

    private static String determineNumberOfEntities(int num){
        switch(num){
            default:
                return "1";
        }
    }
    
    private static String determineSection(int num){
        return "Section " + num / 100;
    }

    private static String determineTeam(int num){
        return "Team " + num / 4;
    }

    private static void printIndent(PrintWriter out, int num){
        for(int i = 0; i < num; i++){
            out.print("\t");
        }
    }
    
    protected void doOperation() {
        Datastore.initialize(); //TODO: push to parent class
        Logic logic = new Logic();
        DataBundle largeScaleBundle = loadDataBundle("/largeScaleTest.json");
        
        
        try{
            // Create questions
            for(FeedbackQuestionAttributes question : largeScaleBundle.feedbackQuestions.values()){
                logic.createFeedbackQuestion(question);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private static DataBundle loadDataBundle(String pathToJsonFile){
        if(pathToJsonFile.startsWith("/")){
            pathToJsonFile = TestProperties.TEST_DATA_FOLDER + pathToJsonFile;
        }
        String jsonString;
        try {
            jsonString = FileHelper.readFile(pathToJsonFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Utils.getTeammatesGson().fromJson(jsonString, DataBundle.class);
    }
}
