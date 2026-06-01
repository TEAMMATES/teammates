import { FeedbackSessionPublishStatus } from '../../types/api-output';

export function publishStatusNameToString(status?: FeedbackSessionPublishStatus): string {
  switch (status) {
    case FeedbackSessionPublishStatus.PUBLISHED:
      return 'Published';
    case FeedbackSessionPublishStatus.NOT_PUBLISHED:
      return 'Not Published';
    default:
      return 'Unknown';
  }
}
