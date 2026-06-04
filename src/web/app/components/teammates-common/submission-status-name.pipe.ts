import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackSessionSubmissionStatus } from '../../../types/api-output';
import { submissionsStatusNameToString } from '../../utils/submissions-status-name.util';

/**
 * Pipe to handle the display of {@code FeedbackSessionSubmissionStatus}.
 */
@Pipe({ name: 'submissionStatusName' })
export class SubmissionStatusNamePipe implements PipeTransform {
  /**
   * Transforms {@link FeedbackSessionSubmissionStatus} to a simple name.
   */
  transform(status: FeedbackSessionSubmissionStatus): string {
    return submissionsStatusNameToString(status);
  }
}
