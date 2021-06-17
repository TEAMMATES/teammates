import {
  FeedbackParticipantType,
  FeedbackQuestionDetails,
  FeedbackQuestionType,
  FeedbackVisibilityType,
  NumberOfEntitiesToGiveFeedbackToSetting,
} from '../../../types/api-output';

/**
 * The mode of operation for question edit form.
 */
export enum QuestionEditFormMode {
  /**
   * Adding a new feedback question.
   */
  ADD,

  /**
   * Editing the existing feedback question.
   */
  EDIT,
}

/**
 * The form model of question edit form.
 */
export interface QuestionEditFormModel {
  feedbackQuestionId: string;

  questionNumber: number;
  questionBrief: string;
  questionDescription: string;

  isQuestionHasResponses: boolean;

  questionType: FeedbackQuestionType;
  questionDetails: FeedbackQuestionDetails;

  giverType: FeedbackParticipantType;
  recipientType: FeedbackParticipantType;

  numberOfEntitiesToGiveFeedbackToSetting: NumberOfEntitiesToGiveFeedbackToSetting;
  customNumberOfEntitiesToGiveFeedbackTo: number;

  showResponsesTo: FeedbackVisibilityType[];
  showGiverNameTo: FeedbackVisibilityType[];
  showRecipientNameTo: FeedbackVisibilityType[];

  isUsingOtherFeedbackPath?: boolean;
  commonVisibilitySettingName?: string;
  isUsingOtherVisibilitySetting?: boolean;

  isDuplicating: boolean;
  isDeleting: boolean;
  isEditable: boolean;
  isSaving: boolean;
  isCollapsed: boolean;
  isVisibilityChanged: boolean;
  isFeedbackPathChanged: boolean;
  isQuestionDetailsChanged: boolean;
}
