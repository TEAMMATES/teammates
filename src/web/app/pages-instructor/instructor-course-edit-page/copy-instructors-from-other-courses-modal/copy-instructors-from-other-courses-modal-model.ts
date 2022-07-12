import { Instructor } from '../../../../types/api-output';
import { SortBy, SortOrder } from '../../../../types/sort-properties';

/**
 * The model for a course tab.
 */
export interface CourseTabModel {
  courseId: string;
  courseName: string;
  creationTimestamp: number;
  isArchived: boolean;

  instructorCandidates: InstructorToCopyCandidateModel[];
  instructorCandidatesSortBy: SortBy;
  instructorCandidatesSortOrder: SortOrder;

  hasInstructorsLoaded: boolean;
  isTabExpanded: boolean;
  hasLoadingFailed: boolean;
}

/**
 * The model for an instructor to copy.
 */
export interface InstructorToCopyCandidateModel {
  instructor: Instructor;
  isSelected: boolean;
}
