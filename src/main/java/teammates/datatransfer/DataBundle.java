package teammates.datatransfer;

import java.util.HashMap;

/**
 * This is a class to hold a bundle of entities
 * This class is mainly used for serializing JSON strings
 */
public class DataBundle {
	
	public HashMap<String,CoordData> coords = new HashMap<String,CoordData>();
	public HashMap<String, CourseData> courses = new HashMap<String, CourseData>();
	public HashMap<String, StudentData> students = new HashMap<String, StudentData>();
	public HashMap<String, EvaluationData> evaluations = new HashMap<String, EvaluationData>();
	public HashMap<String, SubmissionData> submissions = new HashMap<String, SubmissionData>();
	public HashMap<String, TfsData> teamFormingSessions = new HashMap<String, TfsData>();
	public HashMap<String, TeamProfileData> teamProfiles = new HashMap<String, TeamProfileData>();
	public HashMap<String, StudentActionData> studentActions = new HashMap<String, StudentActionData>();
}
