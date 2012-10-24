package teammates.ui.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import teammates.common.BuildProperties;
import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class AdminHomeServlet extends ActionServlet<AdminHomeHelper> {

	@Override
	protected AdminHomeHelper instantiateHelper() {
		return new AdminHomeHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, AdminHomeHelper helper){
		String coordID = req.getParameter(Common.PARAM_COORD_ID);
		String coordName = req.getParameter(Common.PARAM_COORD_NAME);
		String coordEmail = req.getParameter(Common.PARAM_COORD_EMAIL);
		String importSampleData = req.getParameter(Common.PARAM_COORD_IMPORT_SAMPLE);
		
	
		try {
			if(coordID!=null && coordName!=null && coordEmail!=null){
				helper.server.createCoord(coordID, coordName, coordEmail);
				helper.statusMessage = "Coordinator " + coordName + " has been successfully created";
			}
			
			if(importSampleData != null) {
				importDemoData(coordID, coordName, coordEmail, helper);
			}
		} catch (EntityAlreadyExistsException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
	}
	
	
	private void importDemoData(String coordId, String coordName, String coordEmail, AdminHomeHelper helper) {
		String jsonString;
		String courseId = coordId.concat("-demo");
		if(courseId.length() > 20) {
			courseId = courseId.substring(courseId.length() - 20);
		}
		try {
			jsonString = Common.readStream(BuildProperties.class.getClassLoader().getResourceAsStream("CoordinatorSampleData.json"));
			//replace email
		    jsonString = jsonString.replaceAll("teammates.demo.coord@demo.course", coordEmail);
			//replace name
			jsonString = jsonString.replaceAll("Demo_Coord", coordName);
			//replace id
			jsonString = jsonString.replaceAll("teammates.demo.coord", coordId);
			//replace course
			jsonString = jsonString.replaceAll("demo.course", courseId);
		
			//update evaluation time
			Calendar c = Calendar.getInstance();
			c.set(Calendar.AM_PM, Calendar.PM);
			c.set(Calendar.HOUR, 11);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.YEAR, c.get(Calendar.YEAR)+1);
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");; 

			jsonString = jsonString.replace("2013-04-01 11:59 PM", formatter.format(c.getTime()));
			
			
		    Gson gson = Common.getTeammatesGson();
			DataBundle data = gson.fromJson(jsonString, DataBundle.class);
				persist(data.coords, helper);
				persist(data.courses, helper);
				persist(data.students, helper);
				persist(data.evaluations, helper);
				Common.waitBriefly();

				persist(data.submissions, helper);
			
		} catch (Exception e) {
			
		} 
		
	}
	private static void persist(@SuppressWarnings("rawtypes") HashMap map,  AdminHomeHelper helper) throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException
	{
		DataBundle bundle = new DataBundle();
		@SuppressWarnings("unchecked")
		Set<String> set = map.keySet();
	    @SuppressWarnings("rawtypes")
		Iterator itr = set.iterator();
	    List<SubmissionData> submissionDataList = new LinkedList<SubmissionData>();
	    String type = "";
	    while (itr.hasNext())
	    {
	    	String key = (String) itr.next();
	    	Object obj = map.get(key);
	    	
	    	if(obj instanceof CoordData)
			{
	    		type = "CoordData";
				CoordData coordData = (CoordData)obj;
				//skip
			} else if(obj instanceof CourseData)
			{
				type = "CourseData";
				CourseData courseData = (CourseData)obj;
				helper.server.createCourse(courseData.coord, courseData.id, courseData.name);

			} else if(obj instanceof StudentData)
			{
				type = "StudentData";
				StudentData studentData = (StudentData)obj;
				helper.server.createStudent(studentData);
			} else if (obj instanceof EvaluationData)
			{
				type = "EvaluationData";
				EvaluationData evaluationData = (EvaluationData)obj;
				helper.server.createEvaluation(evaluationData);
			} else if (obj instanceof SubmissionData)
			{
				type = "SubmissionData";
				SubmissionData submissionData = (SubmissionData)obj;
				submissionDataList.add(submissionData);
			}
			itr.remove();
			helper.server.editSubmissions(submissionDataList);
	    }
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_HOME;
	}

}
