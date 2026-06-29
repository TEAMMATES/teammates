/**
 * The format of a session template.
 */
import {
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
} from '../../../types/api-output';

/**
 * The mode of operation for session edit form.
 */
export enum SessionEditFormMode {
  /**
   * Adding a new feedback session.
   */
  ADD,

  /**
   * Editing the existing feedback session.
   */
  EDIT,
}

/**
 * The form model of session edit form.
 */
export interface SessionEditFormModel {
  feedbackSessionId: string;
  courseId: string;
  timeZone: string;
  courseName: string;
  feedbackSessionName: string;
  instructions: string;

  submissionStartTimestamp: number;
  submissionEndTimestamp: number;
  gracePeriod: number;

  responseVisibleSetting: ResponseVisibleSetting;
  customResponseVisibleTimestamp: number;

  hasVisibleSettingsPanelExpanded: boolean;
  hasEmailSettingsPanelExpanded: boolean;

  // EDIT mode specific
  submissionStatus: FeedbackSessionSubmissionStatus;
  publishStatus: FeedbackSessionPublishStatus;

  // ADD mode specific
  templateSessionName: string;

  isClosingSoonEmailEnabled: boolean;
  isPublishedEmailEnabled: boolean;

  isSaving: boolean;
  isEditable: boolean;
  isDeleting: boolean;
  isCopying: boolean;
}
