import {
  QuestionStatistics,
  Response,
} from '../../app/components/question-types/question-statistics/question-statistics';
import {
  FeedbackQuestionDetails,
  FeedbackResponseDetails,
  QuestionOutput,
  ResponseOutput,
} from '../api-output';

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

  /**
   * Check if a feedback participant can comment on responses to the question
   */
  abstract isParticipantCommentsOnResponsesAllowed(): boolean;

  /**
   * Checks if an instructor can comment on responses to the question
   */
  abstract isInstructorCommentsOnResponsesAllowed(): boolean;

  /**
   * Gets question stats in CSV.
   */
  abstract getQuestionCsvStats(question: QuestionOutput): string[][];

  /**
   * Populates the {@code questionStatistics} with the responses and corresponding question.
   */
  populateQuestionStatistics<Q extends FeedbackQuestionDetails, R extends FeedbackResponseDetails>(
      questionStatistics: QuestionStatistics<Q, R>, question: QuestionOutput): void {
    questionStatistics.responses = question.allResponses
        // Missing response is meaningless for statistics
        .filter((response: ResponseOutput) => !response.isMissingResponse)
        .map((response: ResponseOutput) => (response as unknown as Response<R>));
    questionStatistics.question =
        question.feedbackQuestion.questionDetails as unknown as Q;
    questionStatistics.recipientType = question.feedbackQuestion.recipientType;
    questionStatistics.isStudent = false;
  }
}
