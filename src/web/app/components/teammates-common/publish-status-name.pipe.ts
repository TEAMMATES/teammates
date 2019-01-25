import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackSessionPublishStatus } from '../../feedback-session';

/**
 * Pipe to handle the display of {@code FeedbackSessionPublishStatus}.
 */
@Pipe({
  name: 'publishStatusName',
})
export class PublishStatusNamePipe implements PipeTransform {

  /**
   * Transforms {@link FeedbackSessionPublishStatus} to a simple name.
   */
  transform(status: FeedbackSessionPublishStatus): string {
    switch (status) {
      case FeedbackSessionPublishStatus.PUBLISHED:
        return 'Published';
      case FeedbackSessionPublishStatus.NOT_PUBLISHED:
        return 'Not Published';
      default:
        return 'Unknown';
    }
  }

}
