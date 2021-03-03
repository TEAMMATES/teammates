import {FeedbackSession} from "../../../types/api-output";

/**
 * Result of {@link CopyCourseModalComponent}
 */
export interface CopyCourseModalResult {
    newCourseId: string;
    chosenFeedbackSessionList: Set<FeedbackSession>;
}