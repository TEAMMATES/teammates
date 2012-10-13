package teammates.test.scripts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.tools.ant.taskdefs.condition.Equals;

import com.google.gson.Gson;

import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.test.driver.BackDoor;

public class ImportData {
	private static String jsonString;
	private static final String sourceFileName = "CoordinatorSampleData.json";
	private static DataBundle data;
	private static Gson gson = Common.getTeammatesGson();
	public static final int MAX_NUMBER_OF_ENTITY_PER_REQUEST = 100;
	
	public static void main(String args[]) throws Exception {
		jsonString = Common.readFile(Common.TEST_DATA_FOLDER
				
				+ "/" + sourceFileName);
		
		data = gson.fromJson(jsonString, DataBundle.class);
		BackDoor.deleteCoord("teammates.demo.coord");
		BackDoor.deleteStudent("demo.course", "danny.e.tmms@gmail.com");
		BackDoor.deleteStudent("demo.course", "charlie.d.tmms@gmail.com");
		BackDoor.deleteStudent("demo.course", "benny.c.tmms@gmail.com");
		BackDoor.deleteStudent("demo.course", "alice.b.tmms@gmail.com");
		BackDoor.deleteCourse("demo.course");
		BackDoor.deleteEvaluation("demo.course", "First Eval");
		BackDoor.deleteEvaluation("demo.course", "Second Eval");
		String status = "";
		do
		{
			long start = System.currentTimeMillis();

			if(!data.coords.isEmpty()) {
				status = persist(data.coords);
			} else if (!data.courses.isEmpty()){
				status = persist(data.courses);
			} else if (!data.students.isEmpty()){
				status = persist(data.students);
			} else if (!data.evaluations.isEmpty()){
				status = persist(data.evaluations);
			} else if (!data.submissions.isEmpty()){
				status = persist(data.submissions);
			} else {
				System.out.print("\n finish!");
				break;
			}
			long elapsedTimeMillis = System.currentTimeMillis()-start;

			// Get elapsed time in seconds
			float elapsedTimeSec = elapsedTimeMillis/1000F;
			System.out.print(status + " in "+elapsedTimeSec +" s\n");
		}while (status.equals( Common.BACKEND_STATUS_SUCCESS));
		
	}
	
	private static String persist(@SuppressWarnings("rawtypes") HashMap map)
	{
		DataBundle bundle = new DataBundle();
		int count =0;
		@SuppressWarnings("unchecked")
		Set<String> set = map.keySet();
	    @SuppressWarnings("rawtypes")
		Iterator itr = set.iterator();
	    
	    String type = "";
	    while (itr.hasNext())
	    {
	    	String key = (String) itr.next();
	    	Object obj = map.get(key);
	    	
	    	if(obj instanceof CoordData)
			{
	    		type = "CoordData";
				CoordData coordData = (CoordData)obj;
				bundle.coords.put(key, coordData);
			} else if(obj instanceof CourseData)
			{
				type = "CourseData";
				CourseData courseData = (CourseData)obj;
				bundle.courses.put(key, courseData);
			} else if(obj instanceof StudentData)
			{
				type = "StudentData";
				StudentData studentData = (StudentData)obj;
				bundle.students.put(key, studentData);
			} else if (obj instanceof EvaluationData)
			{
				type = "EvaluationData";
				EvaluationData evaluationData = (EvaluationData)obj;
				bundle.evaluations.put(key, evaluationData);
			} else if (obj instanceof SubmissionData)
			{
				type = "SubmissionData";
				SubmissionData submissionData = (SubmissionData)obj;
				bundle.submissions.put(key, submissionData);
			}
			count ++;
			itr.remove();
			if(count >= MAX_NUMBER_OF_ENTITY_PER_REQUEST)
				break;
			System.out.print(key + "\n");
	    }
	    System.out.print(count+ " entities of type "+ type + " left " + map.size() +" \n" );
		return BackDoor.persistNewDataBundle(gson.toJson(bundle));
	}
}
