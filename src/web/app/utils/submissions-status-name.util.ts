import { FeedbackSessionSubmissionStatus } from '../../types/api-output';

export function submissionsStatusNameToString(status: FeedbackSessionSubmissionStatus): string {
  let string = '';
  switch (status) {
    case FeedbackSessionSubmissionStatus.NOT_VISIBLE:
    case FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN:
      string += 'Awaiting';
      break;
    case FeedbackSessionSubmissionStatus.OPEN:
      string += 'Open';
      break;
    case FeedbackSessionSubmissionStatus.GRACE_PERIOD:
      string += 'Open (grace period)';
      break;
    case FeedbackSessionSubmissionStatus.CLOSED:
      string += 'Closed';
      break;
    default:
      return 'Unknown';
  }

  return string;
}
