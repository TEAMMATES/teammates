package teammates.common.datatransfer;

import teammates.common.exception.EnrollException;
import teammates.common.util.Assumption;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;

/**
 * A factory for the creation of {@link StudentAttributes} objects.
 * It encapsulates the mechanism to create a {@link StudentAttributes} object from
 * a given enroll line.
 */
public class StudentAttributesFactory {        
    public static final int MIN_FIELD_COUNT = 3;
    public static final int MAX_FIELD_COUNT = 5;
    
    public static final String ERROR_HEADER_ROW_FIELD_REPEATED = "The header row contains repeated fields";
    public static final String ERROR_HEADER_ROW_FIELD_MISSED = "The header row misses required fields";

    public static final String ERROR_ENROLL_LINE_EMPTY = "Enroll line was empty\n";
    public static final String ERROR_ENROLL_LINE_TOOFEWPARTS 
        = "Enroll line had fewer than the minimally expected " + MIN_FIELD_COUNT + " columns (Team, Name and Email)\n";
    
    public static final int FIRST_COLUMN_INDEX = 0;
    public static final int SECOND_COLUMN_INDEX = 1;
    public static final int THIRD_COLUMN_INDEX = 2;
    public static final int FOURTH_COLUMN_INDEX = 3;
    public static final int FIFTH_COLUMN_INDEX = 4;
    
    private int sectionColumnIndex;
    private int teamColumnIndex;
    private int nameColumnIndex;
    private int emailColumnIndex;
    private int commentColumnIndex;
    
    private boolean hasSection;
    private boolean hasTeam;
    private boolean hasName;
    private boolean hasEmail;
    private boolean hasComment;
   
    public StudentAttributesFactory() throws EnrollException {
        this("");
    }
    
    /**
     * Construct a StudentAttributesFactory object with the given header row.<br>
     * The column names allowed in header row: {team, name, email, comment}<br>
     * They are case insensitive and plural nouns are allowed.
     * If the header row doesn't contain all required columns, then it is ignored and
     * treated as a normal enroll line.
     * <br>Pre-condition: headerRow must not be null
     * @throws EnrollException
     */
    public StudentAttributesFactory(String headerRow) throws EnrollException {    
        Assumption.assertNotNull(headerRow);
        
        int fieldCount = locateColumnIndexes(headerRow);
        
        if (fieldCount < MIN_FIELD_COUNT || !hasTeam || !hasName || !hasEmail) {
            throw new EnrollException(ERROR_HEADER_ROW_FIELD_MISSED);
        } else if (fieldCount > MAX_FIELD_COUNT) {
            throw new EnrollException(ERROR_HEADER_ROW_FIELD_REPEATED);
        }
    }
    
    /**
     * Return a StudentAttributes object created from the given enrollLine.
     * @throws EnrollException
     */
    public StudentAttributes makeStudent(String enrollLine, String courseId)
            throws EnrollException {
        
        if (enrollLine.isEmpty()) {
            throw new EnrollException(ERROR_ENROLL_LINE_EMPTY);
        }

        String[] columns = splitLineIntoColumns(enrollLine);

        if (columns.length < MIN_FIELD_COUNT) {
            throw new EnrollException(ERROR_ENROLL_LINE_TOOFEWPARTS);
        }
        
        String paramTeam = columns[teamColumnIndex];
        String paramName = columns[nameColumnIndex];
        String paramEmail = columns[emailColumnIndex];
        
        String paramComment;
        if (hasComment && columns.length > commentColumnIndex) {
            paramComment = columns[commentColumnIndex];
        } else {
            paramComment = "";
        }

        String paramSection;
        if(hasSection){
            paramSection = columns[sectionColumnIndex];
        } else {
            paramSection = "None";
        }
        
        return new StudentAttributes(paramSection, paramTeam, paramName, paramEmail, paramComment, courseId);
    }
    
    private int locateColumnIndexes(String headerRow) {
        int count = 0;
        
        String[] columns = splitLineIntoColumns(headerRow);
        
        for (int curPos = 0; curPos < columns.length; curPos++) {
            String str = columns[curPos].trim().toLowerCase();
            
            if(StringHelper.isMatching(str, FieldValidator.REGEX_COLUMN_SECTION) && !hasSection){
                sectionColumnIndex = curPos;
                count++;
                hasSection = true;
            } else if (StringHelper.isMatching(str, FieldValidator.REGEX_COLUMN_TEAM) && !hasTeam) {
                teamColumnIndex = curPos;
                count++;
                hasTeam = true;
            } else if (StringHelper.isMatching(str, FieldValidator.REGEX_COLUMN_NAME) && !hasName) {
                nameColumnIndex = curPos;
                count++;
                hasName = true;
            } else if (StringHelper.isMatching(str, FieldValidator.REGEX_COLUMN_EMAIL) && !hasEmail) {
                emailColumnIndex = curPos;
                count++;
                hasEmail = true;
            } else if (StringHelper.isMatching(str, FieldValidator.REGEX_COLUMN_COMMENT) && !hasComment) {
                commentColumnIndex = curPos;
                count++;
                hasComment = true;
            } else {
                //do nothing as it is a empty column
            }
        }
        
        return count;
    }
    
    private String[] splitLineIntoColumns(String line) {
        Assumption.assertNotNull(line);
        
        String[] cols = line.replace("|", "\t").split("\t", -1);
        return cols;
    }
}
