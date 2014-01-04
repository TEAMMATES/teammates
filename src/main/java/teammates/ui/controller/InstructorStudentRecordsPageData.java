package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentResultBundle;

public class InstructorStudentRecordsPageData extends PageData {
	
	public String courseId;
	public StudentAttributes student;
	public List<CommentAttributes> comments;
	public List<EvaluationAttributes> evaluations;
	public List<FeedbackSessionAttributes> feedbacks;
	public List<StudentResultBundle> studentEvaluationResults;
	public List<FeedbackSessionResultsBundle> studentFeedbackResults;

	public InstructorStudentRecordsPageData(AccountAttributes account) {
		super(account);
	}
}
