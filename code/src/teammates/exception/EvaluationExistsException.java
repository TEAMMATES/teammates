package teammates.exception;

@SuppressWarnings("serial")
public class EvaluationExistsException extends Exception
{
	public EvaluationExistsException()
	{
		super("Evaluation exists in database already.");
	}
}