/**
 * Abstract class for a question detail.
 */
export abstract class AbstractFeedbackQuestionDetails {

  /**
   * Gets name(s) of header(s) for the question in CSV.
   */
  getQuestionCsvHeaders(): string[] {
    return ['Feedback'];
  }

  /**
   * Gets the response answer(s) in CSV for missing response.
   */
  getMissingResponseCsvAnswers(): string[][] {
    return [['No Response']];
  }
}
