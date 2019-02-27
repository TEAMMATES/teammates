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
        return 'The responses for this session are visible.';
      case FeedbackSessionPublishStatus.NOT_PUBLISHED:
        return 'The responses for this session are not visible.';
      default:
        return 'Unknown';
    }
  }

}
