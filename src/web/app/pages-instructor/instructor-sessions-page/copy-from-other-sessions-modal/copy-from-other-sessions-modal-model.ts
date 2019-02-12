import { FeedbackSession } from '../../../../types/api-output';

/**
 * The result of {@link CopyFromOtherSessionsModalComponent}.
 */
export interface CopyFromOtherSessionsResult {
  fromFeedbackSession: FeedbackSession;
  newFeedbackSessionName: string;
  copyToCourseId: string;
}
