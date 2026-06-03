import { Pipe, PipeTransform } from '@angular/core';
import { sessionSubmissionStatusDisplay } from '../utils/session-submission-status.util';

/**
 * Processes and displays the submission status.
 */
@Pipe({ name: 'sessionSubmissionStatus' })
export class SubmissionStatusPipe implements PipeTransform {
  /**
   * Displays the submission status depending on student submissions and whether the session is open.
   */
  transform(isOpened: boolean, isWaitingToOpen: boolean, isSubmitted: boolean, hasExtension?: boolean): string {
    return sessionSubmissionStatusDisplay(isOpened, isWaitingToOpen, isSubmitted, hasExtension);
  }
}
