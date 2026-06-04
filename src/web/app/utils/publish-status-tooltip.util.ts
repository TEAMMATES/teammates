import { FeedbackSessionPublishStatus } from '../../types/api-output';

export function publishStatusTooltipUtilToString(status: FeedbackSessionPublishStatus): string {
  switch (status) {
    case FeedbackSessionPublishStatus.PUBLISHED:
      return 'Respondents can view responses received, as per the visibility settings of each question.';
    case FeedbackSessionPublishStatus.NOT_PUBLISHED:
      return 'Respondents cannot view responses received.';
    default:
      return 'Unknown';
  }
}
