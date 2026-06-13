/**
 * The format of a session template.
 */
import {
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting,
  SessionVisibleSetting,
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

  // Timestamps are undefined until set (e.g. before defaults are populated, or while a CUSTOM time is unpicked).
  submissionStartTimestamp?: number;
  submissionEndTimestamp?: number;
  gracePeriod: number;

  sessionVisibleSetting: SessionVisibleSetting;
  customSessionVisibleTimestamp?: number;

  responseVisibleSetting: ResponseVisibleSetting;
  customResponseVisibleTimestamp?: number;

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
