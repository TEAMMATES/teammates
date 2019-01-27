import { FeedbackSession } from '../../../feedback-session';

/**
 * The result of {@link CopyFromOtherSessionsModalComponent}.
 */
export interface CopyFromOtherSessionsResult {
  fromFeedbackSession: FeedbackSession;
  newFeedbackSessionName: string;
  copyToCourseId: string;
}
