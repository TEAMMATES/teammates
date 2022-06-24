import {
    FeedbackSession, FeedbackSessionPublishStatus,
    FeedbackSessionSubmissionStatus,
    ResponseVisibleSetting,
    SessionVisibleSetting,
} from '../../../types/api-output';

const testFeedbackSession: FeedbackSession = {
  feedbackSessionName: 'Feedback Session 1',
  courseId: 'CS9999',
  timeZone: 'Asia/Singapore',
  instructions: '',
  submissionStartTimestamp: 0,
  submissionEndTimestamp: 1549095330000,
  gracePeriod: 0,
  sessionVisibleSetting: SessionVisibleSetting.AT_OPEN,
  responseVisibleSetting: ResponseVisibleSetting.AT_VISIBLE,
  submissionStatus: FeedbackSessionSubmissionStatus.OPEN,
  publishStatus: FeedbackSessionPublishStatus.PUBLISHED,
  isClosingEmailEnabled: true,
  isPublishedEmailEnabled: true,
  createdAtTimestamp: 0,
  studentDeadlines: {},
  instructorDeadlines: {},
};

const TestFeedbackSessions = { testFeedbackSession };

export default TestFeedbackSessions;
