import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackSessionSubmissionStatus } from '../../../types/api-output';
import { submissionStatusTooltipToString } from '../../utils/submissions-status-tool-tip.util';

/**
 * Pipe to handle the display of {@code FeedbackSessionSubmissionStatus}.
 */
@Pipe({ name: 'submissionStatusTooltip' })
export class SubmissionStatusTooltipPipe implements PipeTransform {
  /**
   * Transforms {@link FeedbackSessionSubmissionStatus} to a tooltip description.
   */
  transform(status: FeedbackSessionSubmissionStatus): string {
    return submissionStatusTooltipToString(status);
  }
}
