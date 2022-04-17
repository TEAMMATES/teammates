import { Pipe, PipeTransform } from '@angular/core';

/**
 * Processes and displays the submission status.
 */
@Pipe({ name: 'sessionSubmissionStatus' })
export class SubmissionStatusPipe implements PipeTransform {
  /**
   * Displays the submission status depending on student submissions and whether the session is open.
   */
  transform(isOpened: boolean, isWaitingToOpen: boolean, isSubmitted: boolean, hasOngoingExtension?: boolean): string {
    if (isWaitingToOpen) {
      return 'Awaiting';
    }

    if (!isOpened) {
      return 'Closed';
    }

    let msg = '';
    if (isOpened) {
      if (isSubmitted) {
        msg += 'Submitted';
      } else {
        msg += 'Pending';
      }
    }

    if (hasOngoingExtension) {
      msg += ' (with Extension)';
    }

    return msg;
  }
}
