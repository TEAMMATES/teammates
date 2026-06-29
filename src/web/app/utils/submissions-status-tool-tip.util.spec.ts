import { submissionStatusTooltipToString } from './submissions-status-tool-tip.util';
import { FeedbackSessionSubmissionStatus } from '../../types/api-output';

describe('submissionStatusTooltipToString', () => {
  it('should return correct message for NOT_VISIBLE status', () => {
    expect(submissionStatusTooltipToString(FeedbackSessionSubmissionStatus.NOT_VISIBLE)).toBe(
      'The feedback session is waiting to open for submissions, and is not yet visible to respondents.',
    );
  });

  it('should return correct message for OPEN status', () => {
    expect(submissionStatusTooltipToString(FeedbackSessionSubmissionStatus.OPEN)).toBe(
      'The feedback session is open for submissions, and is visible to respondents.',
    );
  });

  it('should return correct message for GRACE_PERIOD status', () => {
    expect(submissionStatusTooltipToString(FeedbackSessionSubmissionStatus.GRACE_PERIOD)).toBe(
      'The feedback session is open for submissions, is in the grace period, and is visible to respondents.',
    );
  });

  it('should return correct message for CLOSED status', () => {
    expect(submissionStatusTooltipToString(FeedbackSessionSubmissionStatus.CLOSED)).toBe(
      'The feedback session is closed for submissions, and is visible to respondents.',
    );
  });
});
