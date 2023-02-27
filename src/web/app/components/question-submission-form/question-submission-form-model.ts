import {
  FeedbackParticipantType,
  FeedbackQuestionDetails,
  FeedbackQuestionType,
  FeedbackResponseDetails,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';
import { CommentRowModel } from '../comment-box/comment-row/comment-row.component';

/**
 * The mode of operation for question submission form.
 */
export enum QuestionSubmissionFormMode {
  /**
   * User cannot select recipient to give feedback to.
   */
  FIXED_RECIPIENT,

  /**
   * User can select recipient to give feedback to.
   */
  FLEXIBLE_RECIPIENT,
}

/**
 * The form model of question submission form.
 */
export interface QuestionSubmissionFormModel {
  feedbackQuestionId: string;

  questionNumber: number;
  questionBrief: string;
  questionDescription: string;

  questionType: FeedbackQuestionType;
  questionDetails: FeedbackQuestionDetails;

  giverType: FeedbackParticipantType;
  recipientType: FeedbackParticipantType;
  recipientList: FeedbackResponseRecipient[];
  recipientSubmissionForms: FeedbackResponseRecipientSubmissionFormModel[];

  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting;
  customNumberOfEntitiesToGiveFeedbackTo: number;

  showResponsesTo: FeedbackVisibilityType[];
  showGiverNameTo: FeedbackVisibilityType[];
  showRecipientNameTo: FeedbackVisibilityType[];

  isLoading: boolean;
  isLoaded: boolean;
  isTabExpanded: boolean;
}

/**
 * A recipient of a feedback question.
 */
export interface FeedbackResponseRecipient {
  recipientIdentifier: string;
  recipientName: string;
  recipientSection?: string;
  recipientTeam?: string;
}

/**
 * The form modal of recipient submission form.
 */
export interface FeedbackResponseRecipientSubmissionFormModel {
  responseId: string;
  recipientIdentifier: string;
  responseDetails: FeedbackResponseDetails;

  isValid: boolean;

  // comment by giver
  commentByGiver?: CommentRowModel;
}

export enum FeedbackRecipientLabelType {
  // show section, team and name
  INCLUDE_SECTION,
  // show team and name
  INCLUDE_TEAM,
  // show name
  INCLUDE_NAME,
}
