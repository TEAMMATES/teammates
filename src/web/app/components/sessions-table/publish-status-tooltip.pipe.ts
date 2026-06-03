import { Pipe, PipeTransform } from '@angular/core';
import { FeedbackSessionPublishStatus } from '../../../types/api-output';
import { publishStatusNameToString } from '../../utils/publish-status-name.util';

/**
 * Pipe to handle the display of {@code FeedbackSessionPublishStatus}.
 */
@Pipe({ name: 'publishStatusTooltip' })
export class PublishStatusTooltipPipe implements PipeTransform {
  /**
   * Transforms {@link FeedbackSessionPublishStatus} to a tooltip description.
   */
  transform(status: FeedbackSessionPublishStatus): string {
    return publishStatusNameToString(status);
  }
}
