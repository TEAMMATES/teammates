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
	
	static boolean isTrial = false; //set this true to skip writing to database
	
	//TODO: This class contains lot of code copy-pasted from the Logic and 
	//  Storage layer. This duplication can be removed if we figure out 
	//  to reuse the Logic API from here.
	
	public static void main(String[] args) throws IOException {
		RepairPartiallyFormedEvaluation repairman = new RepairPartiallyFormedEvaluation();
		repairman.doOperationRemotely();
	}
	
	protected void doOperation() {
		try {
			List<SubmissionAttributes> added = repairSubmissionsForEvaluation("ENTR3312-Sp14",	"WK03 Peer Evaluation Trial");
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
		
		if (!isTrial) {
			pm.makePersistentAll(newEntityList);
			pm.flush();
		}
		
		//Persistence check omitted to save time
	}
	
	//TODO: method name not good
	private SubmissionAttributes getSubmission(String courseId, String evaluationName,
			String toStudent, String fromStudent) {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, evaluationName);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, toStudent);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, fromStudent);

		List<Submission> submissions = getSubmissionEntities(courseId, evaluationName, toStudent, fromStudent);
		
		if (submissions.size() == 0) {
			return null;
		} else if(submissions.size()>1){
			System.out.println("Multiple submissions");
			return new SubmissionAttributes(pruneExtrasAndReturnSurvivor(submissions));
		} else {
			return new SubmissionAttributes(submissions.get(0));
			//TODO: use JDOHelper.isDeleted(submissionList.get(0)) 
		}
	}
	
	private Submission pruneExtrasAndReturnSurvivor(List<Submission> submissions) {
		Assumption.assertTrue(submissions.size()==2);
		Submission firstSubmission = submissions.get(0);
		Submission secondSubmission = submissions.get(1);
		if(isEmptySubmission(firstSubmission)){
			deleteSubmission(firstSubmission);
			return secondSubmission;
		}else if(isEmptySubmission(secondSubmission)){
			deleteSubmission(secondSubmission);
			return firstSubmission;
		}else{
			System.out.println("###### both submissions not empty!!!!!!!!");
			return null;
		}
	}

	private void deleteSubmission(Submission s) {
		System.out.println("deleting duplicate " + s.toString());
		if (!isTrial) {
			pm.deletePersistent(s);
			pm.flush();
		}
		
	}

	private boolean isEmptySubmission(Submission s) {
		return s.getPoints() == Const.POINTS_NOT_SUBMITTED 
				&& hasNoValue(s.getCommentsToStudent())
				&& hasNoValue(s.getJustification());
	}
	
	private boolean hasNoValue(Text text){
		return text.getValue() == null || 
				text.getValue().isEmpty();
	}

	private List<Submission> getSubmissionEntities(String courseId,
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

		return submissionList;
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
