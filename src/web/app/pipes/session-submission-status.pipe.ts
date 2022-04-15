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
    if (isOpened && isSubmitted) {
      if (hasOngoingExtension) {
        return 'Submitted (with Extension)';
      }
      return 'Submitted';
    }

    if (!isSubmitted) {
      if (hasOngoingExtension) {
        return 'Pending (with Extension)';
      }
      if (isOpened) {
        return 'Pending';
      }
    }

    if (isWaitingToOpen) {
      return 'Awaiting';
    }

    return 'Closed';
  }
}
