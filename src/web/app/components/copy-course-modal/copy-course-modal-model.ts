import {FeedbackSession} from "../../../types/api-output";

/**
 * Result of {@link CopyCourseModalComponent}
 */
export interface CopyCourseModalResult {
    newCourseId: string;
    newCourseName: string;
    newTimeZone: string;
    chosenFeedbackSessionList: Set<FeedbackSession>;
}