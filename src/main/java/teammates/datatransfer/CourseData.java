package teammates.datatransfer;

import java.util.ArrayList;

import teammates.Common;
import teammates.jdo.EvaluationDetailsForCoordinator;


public class CourseData {
	public String id;
	public String name;
	public String coordId;
	
	//these are marked transient because we don't want to involve them in
	//Json conversions.
	public transient int teamsTotal = Common.UNINITIALIZED_INT;
	public transient int studentsTotal = Common.UNINITIALIZED_INT;
	public transient int unregisteredTotal = Common.UNINITIALIZED_INT;
	public transient ArrayList<EvaluationDetailsForCoordinator> evaluations = new ArrayList<EvaluationDetailsForCoordinator>();


public CourseData(){
	
}

public CourseData (String id, String name, String coordId){
	this.id = id;
	this.name = name;
	this.coordId = coordId;
}

}
