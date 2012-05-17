package teammates;

import java.util.HashMap;

import teammates.jdo.*;
/**
 * This is a class to hold a bundle of entities
 * This class is mainly used for serializing JSON strings
 */
public class DataBundle {
	
	public HashMap<String,Coordinator> coords = new HashMap<String,Coordinator>();
	public HashMap<String, Course> courses = new HashMap<String, Course>();
	public HashMap<String, Student> students = new HashMap<String, Student>();
	public HashMap<String, Evaluation> evaluations = new HashMap<String, Evaluation>();
	
}
