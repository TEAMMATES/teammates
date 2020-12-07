import {
  FeedbackQuestionType,
  FeedbackTextQuestionDetails, QuestionOutput,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackTextQuestionDetails}.
 */
export class FeedbackTextQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackTextQuestionDetails {

  recommendedLength?: number;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.TEXT;
  shouldAllowRichText: boolean;

  constructor(apiOutput: FeedbackTextQuestionDetails) {
    super();
    this.recommendedLength = apiOutput.recommendedLength;
    this.questionText = apiOutput.questionText;
    this.shouldAllowRichText = apiOutput.shouldAllowRichText;
  }

  getQuestionCsvStats(_: QuestionOutput): string[][] {
    // no stats for text question
    return [];
  }

  isParticipantCommentsOnResponsesAllowed(): boolean {
    return false;
  }

  isInstructorCommentsOnResponsesAllowed(): boolean {
    return true;
  }
}
