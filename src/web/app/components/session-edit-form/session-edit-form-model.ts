/**
 * The format of a session template.
 */
import {
  FeedbackSessionPublishStatus,
  FeedbackSessionSubmissionStatus,
  ResponseVisibleSetting, SessionVisibleSetting,
} from '../../../types/api-output';
import { DateFormat, TimeFormat } from '../../../types/datetime-const';

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
  courseId: string;
  timeZone: string;
  courseName: string;
  feedbackSessionName: string;
  instructions: string;

  submissionStartTime: TimeFormat;
  submissionStartDate: DateFormat;
  submissionEndTime: TimeFormat;
  submissionEndDate: DateFormat;
  gracePeriod: number;

  sessionVisibleSetting: SessionVisibleSetting;
  customSessionVisibleTime: TimeFormat;
  customSessionVisibleDate: DateFormat;

  responseVisibleSetting: ResponseVisibleSetting;
  customResponseVisibleTime: TimeFormat;
  customResponseVisibleDate: DateFormat;

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
