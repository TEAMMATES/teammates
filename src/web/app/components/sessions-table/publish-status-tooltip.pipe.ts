import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackSessionPublishStatus } from '../../../types/api-output';

/**
 * Pipe to handle the display of {@code FeedbackSessionPublishStatus}.
 */
@Pipe({
  name: 'publishStatusTooltip',
})
export class PublishStatusTooltipPipe implements PipeTransform {

  /**
   * Transforms {@link FeedbackSessionPublishStatus} to a tooltip description.
   */
  transform(status: FeedbackSessionPublishStatus): string {
    switch (status) {
      case FeedbackSessionPublishStatus.PUBLISHED:
        return 'Respondents can view responses received, as per the visibility settings of each question.';
      case FeedbackSessionPublishStatus.NOT_PUBLISHED:
        return 'Respondents cannot view responses received.';
      default:
        return 'Unknown';
    }
  }

}
