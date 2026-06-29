import { FeedbackSessionSubmissionStatus } from '../../types/api-output';

export function submissionsStatusNameToString(status: FeedbackSessionSubmissionStatus): string {
  switch (status) {
    case FeedbackSessionSubmissionStatus.NOT_VISIBLE:
      return 'Awaiting';
    case FeedbackSessionSubmissionStatus.OPEN:
      return 'Open';
    case FeedbackSessionSubmissionStatus.GRACE_PERIOD:
      return 'Open (grace period)';
    case FeedbackSessionSubmissionStatus.CLOSED:
      return 'Closed';
    default:
      return 'Unknown';
  }
}
