import { Pipe, PipeTransform } from '@angular/core';

/**
 * Displays the submission status depending on student submissions and whether the session is open
 */
@Pipe({name: 'sessionSubmissionStatus'})
export class SubmissionStatusPipe implements PipeTransform {
  transform(isOpened: boolean, isWaitingToOpen: boolean, isSubmitted: boolean): string {
    if (isOpened && isSubmitted) {
      return "Submitted";
    } else if (isOpened && !isSubmitted) {
      return "Pending";
    } else if (isWaitingToOpen) {
      return "Awaiting";
    } else {
      return "Closed";
    }
  }
}
