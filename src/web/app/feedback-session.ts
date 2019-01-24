/**
 * The maximum length of the feedback session name.
 */
export const FEEDBACK_SESSION_NAME_MAX_LENGTH: number = 38;

/**
 * A list of feedback session.
 */
export interface FeedbackSessions {
  feedbackSessions: FeedbackSession[];
}

/**
 * Feedback Session.
 */
export interface FeedbackSession {
  courseId: string;
  timeZone: string;
  feedbackSessionName: string;
  instructions: string;

  submissionStartTimestamp: number;
  submissionEndTimestamp: number;
  gracePeriod: number;

  submissionStatus: FeedbackSessionSubmissionStatus;
  publishStatus: FeedbackSessionPublishStatus;

  sessionVisibleSetting: SessionVisibleSetting;
  customSessionVisibleTimestamp?: number;

  responseVisibleSetting: ResponseVisibleSetting;
  customResponseVisibleTimestamp?: number;

  isClosingEmailEnabled: boolean;
  isPublishedEmailEnabled: boolean;

  createdAtTimestamp: number;
  deletedAtTimestamp?: number;
}

/**
 * The option for session visible setting.
 */
export enum SessionVisibleSetting {
  /**
   * Customized session visible time.
   */
  CUSTOM = 'CUSTOM',

  /**
   * Session visible when open.
   */
  AT_OPEN = 'AT_OPEN',
}

/**
 * The option for response visible setting.
 */
export enum ResponseVisibleSetting {
  /**
   * Customized response visible time.
   */
  CUSTOM = 'CUSTOM',

  /**
   * Response visible when session is visible.
   */
  AT_VISIBLE = 'AT_VISIBLE',

  /**
   * Response won't be visible automatically.
   */
  LATER = 'LATER',
}

/**
 * Represents the submission status of the a feedback session.
 */
export enum FeedbackSessionSubmissionStatus {

  /**
   * Feedback session is not visible.
   */
  NOT_VISIBLE = 'NOT_VISIBLE',

  /**
   * Feedback session is visible to view but not open for submission.
   */
  VISIBLE_NOT_OPEN = 'VISIBLE_NOT_OPEN',

  /**
   * Feedback session is open for submission.
   */
  OPEN = 'OPEN',

  /**
   * Feedback session is in grace period.
   */
  GRACE_PERIOD = 'GRACE_PERIOD',

  /**
   * Feedback session is closed for submission.
   */
  CLOSED = 'CLOSED',
}

/**
 * Represents the publish status of the a feedback session.
 */
export enum FeedbackSessionPublishStatus {

  /**
   * Feedback session is published.
   */
  PUBLISHED = 'PUBLISHED',

  /**
   * Feedback session is not published.
   */
  NOT_PUBLISHED = 'NOT_PUBLISHED',
}

/**
 * The submission stats of a feedback session
 */
export interface FeedbackSessionStats {
  submittedTotal: number;
  expectedTotal: number;
}
