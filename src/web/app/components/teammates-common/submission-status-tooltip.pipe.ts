import { Pipe, PipeTransform } from '@angular/core';
import { DeadlineExtensionHelper } from '../../../services/deadline-extension-helper';
import { FeedbackSessionSubmissionStatus } from '../../../types/api-output';

/**
 * Pipe to handle the display of {@code FeedbackSessionSubmissionStatus}.
 */
@Pipe({
  name: 'submissionStatusTooltip',
})
export class SubmissionStatusTooltipPipe implements PipeTransform {

  /**
   * Transforms {@link FeedbackSessionSubmissionStatus} to a tooltip description.
   */
  transform(status: FeedbackSessionSubmissionStatus, deadlines?: {
    studentDeadlines: Record<string, number>, instructorDeadlines: Record<string, number>,
  }): string {
    let msg: string = 'The feedback session';

    switch (status) {
      case FeedbackSessionSubmissionStatus.NOT_VISIBLE:
      case FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN:
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
      case FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN:
        msg += ', but is visible to respondents';
        break;
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

    if (deadlines && DeadlineExtensionHelper.hasOngoingExtension(deadlines)) {
      msg += ', with current ongoing individual deadline extensions';
    }

    msg += '.';

    return msg;
  }
}
