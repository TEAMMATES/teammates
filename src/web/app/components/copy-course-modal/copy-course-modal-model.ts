import { FeedbackSession } from '../../../types/api-output';

/**
 * Result of {@link CopyCourseModalComponent}
 */
export interface CopyCourseModalResult {
  newCourseId: string;
  newCourseName: string;
  newCourseInstitute: string;
  oldCourseId: string;
  newTimeZone: string;
  selectedFeedbackSessionList: Set<FeedbackSession>;
  totalNumberOfSessions: number;
}
