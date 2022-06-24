import { FeedbackSessionLog, FeedbackSessionLogType } from '../../../types/api-output';
import TestFeedbackSessions from '../feedback-sessions';
import TestStudents from '../students';

const testLogs1: FeedbackSessionLog = {
  feedbackSessionData: TestFeedbackSessions.testFeedbackSession,
  feedbackSessionLogEntries: [
    {
      studentData: TestStudents.johnDoe,
      feedbackSessionLogType: FeedbackSessionLogType.SUBMISSION,
      timestamp: 0,
    },
  ],
};

const testLogs2: FeedbackSessionLog = {
  feedbackSessionData: TestFeedbackSessions.testFeedbackSession,
  feedbackSessionLogEntries: [
    {
      studentData: TestStudents.johnDoe,
      feedbackSessionLogType: FeedbackSessionLogType.SUBMISSION,
      timestamp: 0,
    },
  ],
};

const TestFeedbackSessionLogs = { testLogs1, testLogs2 };

export default TestFeedbackSessionLogs;
