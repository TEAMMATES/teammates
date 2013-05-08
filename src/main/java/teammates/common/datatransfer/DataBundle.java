package teammates.common.datatransfer;

import java.util.HashMap;

/**
 * This is a class to hold a bundle of entities
 * This class is mainly used for serializing JSON strings
 */
public class DataBundle {
	public HashMap<String, AccountAttributes> accounts = new HashMap<String, AccountAttributes>();
	public HashMap<String, InstructorAttributes> instructors = new HashMap<String, InstructorAttributes>();
	public HashMap<String, CourseAttributes> courses = new HashMap<String, CourseAttributes>();
	public HashMap<String, StudentAttributes> students = new HashMap<String, StudentAttributes>();
	public HashMap<String, EvaluationAttributes> evaluations = new HashMap<String, EvaluationAttributes>();
	public HashMap<String, SubmissionAttributes> submissions = new HashMap<String, SubmissionAttributes>();
}
