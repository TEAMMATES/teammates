package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Text;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.storage.entity.Student;
import teammates.storage.entity.Submission;

/**
 * Adds any missing submission entities to an evaluation.
 * This is useful in cases where submission creation process for a new evaluation
 * was only partially completed.
 */
public class RepairPartiallyFormedEvaluation extends RemoteApiClient {
	
	//TODO: This class contains lot of code copy-pasted from the Logic and 
	//  Storage layer. This duplication can be removed if we figure out 
	//  to reuse the Logic API from here.
	
	public static void main(String[] args) throws IOException {
		RepairPartiallyFormedEvaluation repairman = new RepairPartiallyFormedEvaluation();
		repairman.doOperationRemotely();
	}
	
	protected void doOperation() {
		try {
			List<SubmissionAttributes> added = repairSubmissionsForEvaluation("CS2103-Aug2013",	"Peer evaluation 1");
			System.out.println("Number of submissions added :"+added.size());
		} catch (EntityAlreadyExistsException | InvalidParametersException
				| EntityDoesNotExistException e) {
			e.printStackTrace();
		}
	}
	
	private List<SubmissionAttributes> repairSubmissionsForEvaluation(String courseId, String evaluationName)
			throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
	
		List<StudentAttributes> studentDataList = getStudentsForCourse(courseId);
		
		List<SubmissionAttributes> listOfSubmissionsToAdd = new ArrayList<SubmissionAttributes>();
	
		// This double loop creates 3 submissions for a pair of students:
		// x->x, x->y, y->x
		for (StudentAttributes sx : studentDataList) {
			System.out.println("checking submissions for reviewer: "+ sx.name);
			for (StudentAttributes sy : studentDataList) {
				if (sx.team.equals(sy.team)) {
					SubmissionAttributes existingSubmission = 
							getSubmission(courseId, evaluationName, sx.email, sy.email);
					if(existingSubmission!=null){
						continue;
					}
					SubmissionAttributes submissionToAdd = 
							new SubmissionAttributes(courseId, evaluationName, sx.team, sx.email, sy.email);
					submissionToAdd.p2pFeedback = new Text("");
					submissionToAdd.justification = new Text("");
					listOfSubmissionsToAdd.add(submissionToAdd);
					System.out.println("Creating missing submission "+ submissionToAdd.toString());
				}
			}
		}
	
		createSubmissions(listOfSubmissionsToAdd);
		return listOfSubmissionsToAdd;
	}
	
	private void createSubmissions(List<SubmissionAttributes> newList) throws InvalidParametersException {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newList);
		
		List<Submission> newEntityList = new ArrayList<Submission>();
		
		for (SubmissionAttributes sd : newList) {
			if (!sd.isValid()) {
				throw new InvalidParametersException(sd.getInvalidityInfo());
			}
			//Existence check omitted to save time
			newEntityList.add(sd.toEntity());
		}
		
		pm.makePersistentAll(newEntityList);
		pm.flush();
		
		//Persistence check omitted to save time
	}
	
	private SubmissionAttributes getSubmission(String courseId, String evaluationName,
			String toStudent, String fromStudent) {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, evaluationName);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, toStudent);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, fromStudent);

		Submission s = getSubmissionEntity(courseId, evaluationName, toStudent, fromStudent);

		if (s == null) {
			return null;
		}
		return new SubmissionAttributes(s);
	}
	
	private Submission getSubmissionEntity(String courseId,
			String evaluationName, String toStudent, String fromStudent) {

		Query q = pm.newQuery(Submission.class);
		q.declareParameters(
				"String courseIdParam, " +
				"String evluationNameParam, " +
				"String fromStudentParam, " +
				"String toStudentParam");
		
		q.setFilter("courseID == courseIdParam"
				+ " && evaluationName == evluationNameParam"
				+ " && fromStudent == fromStudentParam"
				+ " && toStudent == toStudentParam");
		
		// To pass in more than 3 parameters, an object array is needed. 
		Object[] parameters = {courseId, evaluationName, fromStudent, toStudent};

		// jdo.Query.execute() method only support up to 3 parameter.
		// executeWithArray() is used when more than 3 parameters are used in a query.
		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) q.executeWithArray(parameters);

		if (submissionList.isEmpty() || JDOHelper.isDeleted(submissionList.get(0))) {
			return null;
		}

		return submissionList.get(0);
	}
	
	private List<StudentAttributes> getStudentsForCourse(String courseId) {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		
		List<Student> studentList = getStudentEntitiesForCourse(courseId);
		
		List<StudentAttributes> studentDataList = new ArrayList<StudentAttributes>();
	
		for (Student s : studentList) {
			if (!JDOHelper.isDeleted(s)) {
				studentDataList.add(new StudentAttributes(s));
			}
		}
	
		return studentDataList;
	}
	
	private List<Student> getStudentEntitiesForCourse(String courseId) {
		Query q = pm.newQuery(Student.class);
		q.declareParameters("String courseIdParam");
		q.setFilter("courseID == courseIdParam");
		
		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) q.execute(courseId);
		return studentList;
	}
	
}
