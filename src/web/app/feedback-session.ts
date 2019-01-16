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

  submissionStatus: string;
  publishStatus: string;

  sessionVisibleSetting: SessionVisibleSetting;
  customSessionVisibleTimestamp?: number;

  responseVisibleSetting: ResponseVisibleSetting;
  customResponseVisibleTimestamp?: number;

  isClosingEmailEnabled: boolean;
  isPublishedEmailEnabled: boolean;
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
