import { Pipe, PipeTransform } from '@angular/core';

/**
 * Processes and displays the submission status.
 */
@Pipe({ name: 'sessionSubmissionStatus' })
export class SubmissionStatusPipe implements PipeTransform {
  /**
   * Displays the submission status depending on student submissions and whether the session is open.
   */
  transform(isOpened: boolean, isWaitingToOpen: boolean, isSubmitted: boolean): string {
    if (isOpened && isSubmitted) {
      return 'Submitted';
    }

    if (isOpened && !isSubmitted) {
      return 'Pending';
    }

    if (isWaitingToOpen) {
      return 'Awaiting';
    }

    return 'Closed';
  }
}
