import {
  FeedbackMsqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackMsqQuestionDetails}.
 */
export class FeedbackMsqQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackMsqQuestionDetails {

  msqChoices: string[] = [];
  otherEnabled: boolean = false;
  generateOptionsFor: FeedbackParticipantType = FeedbackParticipantType.NONE;
  maxSelectableChoices: number = Number.MIN_VALUE;
  minSelectableChoices: number = Number.MIN_VALUE;
  hasAssignedWeights: boolean = false;
  msqWeights: number[] = [];
  msqOtherWeight: number = 0;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.MSQ;

  constructor(apiOutput: FeedbackMsqQuestionDetails) {
    super();
    this.msqChoices = apiOutput.msqChoices;
    this.otherEnabled = apiOutput.otherEnabled;
    this.generateOptionsFor = apiOutput.generateOptionsFor;
    this.maxSelectableChoices = apiOutput.maxSelectableChoices;
    this.minSelectableChoices = apiOutput.minSelectableChoices;
    this.hasAssignedWeights = apiOutput.hasAssignedWeights;
    this.msqWeights = apiOutput.msqWeights;
    this.msqOtherWeight = apiOutput.msqOtherWeight;
    this.questionText = apiOutput.questionText;
  }

  getQuestionCsvHeaders(): string[] {
    return ['Feedback', ...this.msqChoices];
  }

}
