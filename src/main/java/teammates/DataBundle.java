package teammates;

import java.util.HashMap;

import teammates.datatransfer.*;
import teammates.jdo.*;
/**
 * This is a class to hold a bundle of entities
 * This class is mainly used for serializing JSON strings
 */
public class DataBundle {
	
	public HashMap<String,CoordData> coords = new HashMap<String,CoordData>();
	public HashMap<String, Course> courses = new HashMap<String, Course>();
	public HashMap<String, Student> students = new HashMap<String, Student>();
	public HashMap<String, Evaluation> evaluations = new HashMap<String, Evaluation>();
	public HashMap<String, Submission> submissions = new HashMap<String, Submission>();
	public HashMap<String, TeamFormingSession> teamFormingSessions = new HashMap<String, TeamFormingSession>();
	public HashMap<String, TeamProfile> teamProfiles = new HashMap<String, TeamProfile>();
	public HashMap<String, TeamFormingLog> teamFormingLogs = new HashMap<String, TeamFormingLog>();
}
