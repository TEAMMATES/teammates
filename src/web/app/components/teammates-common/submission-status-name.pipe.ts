import { Pipe, PipeTransform } from '@angular/core';
import { DeadlineExtensionHelper } from '../../../services/deadline-extension-helper';
import { FeedbackSessionSubmissionStatus } from '../../../types/api-output';

/**
 * Pipe to handle the display of {@code FeedbackSessionSubmissionStatus}.
 */
@Pipe({
  name: 'submissionStatusName',
})
export class SubmissionStatusNamePipe implements PipeTransform {

  /**
   * Transforms {@link FeedbackSessionSubmissionStatus} to a simple name.
   */
  transform(status: FeedbackSessionSubmissionStatus, deadlines?: {
    studentDeadlines: Record<string, number>, instructorDeadlines: Record<string, number>,
  }): string {
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

    if (deadlines && DeadlineExtensionHelper.hasOngoingExtension(deadlines)) {
      string += ' (Ext. ongoing)';
    }

    return string;
  }
}
