import { FeedbackSessionSubmissionStatus } from '../../types/api-output';

export function submissionStatusTooltipToString(status: FeedbackSessionSubmissionStatus): string {
  let msg = 'The feedback session';

  switch (status) {
    case FeedbackSessionSubmissionStatus.NOT_VISIBLE:
      msg += ' is waiting to open for submissions';
      break;
    case FeedbackSessionSubmissionStatus.OPEN:
      msg += ' is open for submissions';
      break;
    case FeedbackSessionSubmissionStatus.GRACE_PERIOD:
      msg += ' is open for submissions, is in the grace period';
      break;
    case FeedbackSessionSubmissionStatus.CLOSED:
      msg += ' is closed for submissions';
      break;
    default:
  }

  switch (status) {
    case FeedbackSessionSubmissionStatus.OPEN:
    case FeedbackSessionSubmissionStatus.GRACE_PERIOD:
    case FeedbackSessionSubmissionStatus.CLOSED:
      msg += ', and is visible to respondents';
      break;
    case FeedbackSessionSubmissionStatus.NOT_VISIBLE:
      msg += ', and is not yet visible to respondents';
      break;
    default:
  }

  msg += '.';

  return msg;
}
