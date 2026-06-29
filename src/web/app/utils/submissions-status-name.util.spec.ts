import { submissionsStatusNameToString } from './submissions-status-name.util';
import { FeedbackSessionSubmissionStatus } from '../../types/api-output';

describe('submissionsStatusNameToString', () => {
  it('should return "Awaiting" for NOT_VISIBLE status', () => {
    expect(submissionsStatusNameToString(FeedbackSessionSubmissionStatus.NOT_VISIBLE)).toBe('Awaiting');
  });

  it('should return "Open" for OPEN status', () => {
    expect(submissionsStatusNameToString(FeedbackSessionSubmissionStatus.OPEN)).toBe('Open');
  });

  it('should return "Open (grace period)" for GRACE_PERIOD status', () => {
    expect(submissionsStatusNameToString(FeedbackSessionSubmissionStatus.GRACE_PERIOD)).toBe('Open (grace period)');
  });

  it('should return "Closed" for CLOSED status', () => {
    expect(submissionsStatusNameToString(FeedbackSessionSubmissionStatus.CLOSED)).toBe('Closed');
  });

  it('should return "Unknown" for an unrecognized status', () => {
    expect(submissionsStatusNameToString('INVALID' as FeedbackSessionSubmissionStatus)).toBe('Unknown');
  });
});
