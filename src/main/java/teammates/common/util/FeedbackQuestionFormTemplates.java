package teammates.common.util;

public class FeedbackQuestionFormTemplates {
	public static String TEXT_SUBMISSION_FORM = FileHelper.readResourseFile("feedbackQuestionTextSubmissionFormTemplate.html");
	
	public static String MCQ_SUBMISSION_FORM = FileHelper.readResourseFile("feedbackQuestionMcqSubmissionFormTemplate.html");
	public static String MCQ_SUBMISSION_FORM_OPTIONFRAGMENT = FileHelper.readResourseFile("feedbackQuestionMcqSubmissionFormOptionFragment.html");
	public static String MCQ_EDIT_FORM = FileHelper.readResourseFile("feedbackQuestionMcqEditFormTemplate.html");
	public static String MCQ_EDIT_FORM_OPTIONFRAGMENT = FileHelper.readResourseFile("feedbackQuestionMcqEditFormOptionFragment.html");

	/** Populates the feedback question form html templates by replacing 
	 * variables in the template string with the given value string.
	 * @param template The template html to be populated
	 * @param values Array of a variable number of variable-value pairs. <br>
	 * * The array is in the form { "var1", "val1", "var2", "val2", ... } 
	 * @return The populated template
	 */
	public static String populateTemplate(String template, String... values) {
		String populatedTemplate = template;
		for(int i=0; i < values.length; i += 2) {
			populatedTemplate = populatedTemplate.replace(values[i], values[i+1]);
		}
		
		return populatedTemplate;	
	}
}
