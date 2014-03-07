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
	
	public static final String ERROR_ENROLL_LINE_EMPTY = "Enroll line was empty\n";
	public static final String ERROR_ENROLL_LINE_TOOFEWPARTS = "Enroll line had too few parts\n";
	
	public static final String ERROR_HEADER_ROW_FIELD_REPEATED = "The header row contains repeated fields";
	
	public static final int MIN_FIELD_COUNT = 3;
	public static final int MAX_FIELD_COUNT = 4;
	
	public static final int DEFAULT_TEAM_INDEX = 0;
	public static final int DEFAULT_NAME_INDEX = 1;
	public static final int DEFAULT_EMAIL_INDEX = 2;
	public static final int DEFAULT_COMMENT_INDEX = 3;
	
	private int teamColumnIndex;
	private int nameColumnIndex;
	private int emailColumnIndex;
	private int commentColumnIndex;
	
	private boolean hasComment;
	private boolean isHeaderSpecified;
	
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
		
		if (fieldCount < MIN_FIELD_COUNT) {
			isHeaderSpecified = false;
			
			teamColumnIndex = DEFAULT_TEAM_INDEX;
			nameColumnIndex = DEFAULT_NAME_INDEX;
			emailColumnIndex = DEFAULT_EMAIL_INDEX;
			commentColumnIndex = DEFAULT_COMMENT_INDEX;
		} else if (fieldCount > MAX_FIELD_COUNT) {
			throw new EnrollException(ERROR_HEADER_ROW_FIELD_REPEATED);
		} else {
			isHeaderSpecified = true;
		}
		
	}

	/**
	 * Return true if there is a correct header row specified for this object.
	 * Else return false.
	 */
	public boolean hasHeader() {
		return isHeaderSpecified;
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
		if ((hasComment && columns.length > commentColumnIndex) || (!isHeaderSpecified && columns.length == 4)) {
			paramComment = columns[commentColumnIndex];
		} else {
			paramComment = "";
		}
		
		return new StudentAttributes(paramTeam, paramName, paramEmail, paramComment, courseId);
	}
	
	private int locateColumnIndexes(String headerRow) {
		int count = 0;
		
		String[] columns = splitLineIntoColumns(headerRow);
		
		for (int curPos = 0; curPos < columns.length; curPos++) {
			String str = columns[curPos].trim().toLowerCase();
			
			if (StringHelper.isMatching(str, FieldValidator.REGEX_COLUMN_TEAM)) {
				teamColumnIndex = curPos;
				count++;
			} else if (StringHelper.isMatching(str, FieldValidator.REGEX_COLUMN_NAME)) {
				nameColumnIndex = curPos;
				count++;
			} else if (StringHelper.isMatching(str, FieldValidator.REGEX_COLUMN_EMAIL)) {
				emailColumnIndex = curPos;
				count++;
			} else if (StringHelper.isMatching(str, FieldValidator.REGEX_COLUMN_COMMENT)) {
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
