import { FeedbackParticipantType } from './feedback-participant-type';
import { FeedbackVisibilityType } from './feedback-visibility';

/**
 * Feedback question types.
 */
export enum FeedbackQuestionType {
  /**
   * Text question type.
   */
  TEXT = 'TEXT',

  /**
   * Contribution question type.
   */
  CONTRIB = 'CONTRIB',
}

/**
 * The feedback question.
 */
export interface FeedbackQuestion {
  feedbackQuestionId: string;
  questionNumber: number;
  questionBrief: string;
  questionDescription: string;

  questionDetails: FeedbackQuestionDetails;

  questionType: FeedbackQuestionType;
  giverType: FeedbackParticipantType;
  recipientType: FeedbackParticipantType;

  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting;
  customNumberOfEntitiesToGiveFeedbackTo?: number;

  showResponsesTo: FeedbackVisibilityType[];
  showGiverNameTo: FeedbackVisibilityType[];
  showRecipientNameTo: FeedbackVisibilityType[];
}

/**
 * The setting of number of entities to giver feedback to.
 */
export enum NumberOfEntitiesToGiveFeedbackToSetting {
  /**
   * Custom number of entities to give feedback to.
   */
  CUSTOM = 'CUSTOM',

  /**
   * Unlimited number of entities to give feedback to.
   */
  UNLIMITED = 'UNLIMITED',
}

/**
 * The abstract feedback question details.
 */
// tslint:disable-next-line:no-empty-interface
export interface FeedbackQuestionDetails {

}

/**
 * The details of the contribution question type.
 */
export interface FeedbackContributionQuestionDetails extends FeedbackQuestionDetails {
  isNotSureAllowed: boolean;
}

/**
 * The details of the text question type.
 */
export interface FeedbackTextQuestionDetails extends FeedbackQuestionDetails {
  recommendedLength: number;
}
