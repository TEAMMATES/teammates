import {
  FeedbackMcqQuestionDetails,
  FeedbackParticipantType,
  FeedbackQuestionType,
} from '../api-output';
import { AbstractFeedbackQuestionDetails } from './abstract-feedback-question-details';

/**
 * Concrete implementation of {@link FeedbackMcqQuestionDetails}.
 */
export class FeedbackMcqQuestionDetailsImpl extends AbstractFeedbackQuestionDetails
    implements FeedbackMcqQuestionDetails {

  hasAssignedWeights: boolean = false;
  mcqWeights: number[] = [];
  mcqOtherWeight: number = 0;
  numOfMcqChoices: number = 0;
  mcqChoices: string[] = [];
  otherEnabled: boolean = false;
  generateOptionsFor: FeedbackParticipantType = FeedbackParticipantType.NONE;
  questionText: string = '';
  questionType: FeedbackQuestionType = FeedbackQuestionType.MCQ;

  constructor(apiOutput: FeedbackMcqQuestionDetails) {
    super();
    this.hasAssignedWeights = apiOutput.hasAssignedWeights;
    this.mcqWeights = apiOutput.mcqWeights;
    this.mcqOtherWeight = apiOutput.mcqOtherWeight;
    this.numOfMcqChoices = apiOutput.numOfMcqChoices;
    this.mcqChoices = apiOutput.mcqChoices;
    this.otherEnabled = apiOutput.otherEnabled;
    this.generateOptionsFor = apiOutput.generateOptionsFor;
    this.questionText = apiOutput.questionText;
  }
}
