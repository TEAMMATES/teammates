import { Course } from '../../../types/api-output';

/**
 * Represents the data of a new instructor.
 */
export interface InstructorData {
  name: string;
  email: string;
  institution: string;
  status: string;
  isCurrentlyBeingEdited: boolean;
  statusCode?: number;
  joinLink?: string;
  message?: string;
}

/**
 * Represents the account data associated with a registered instructor.
 * See registered instructor modal of {@link AdminHomePageComponent}.
 */
export interface RegisteredInstructorAccountData {
  googleId: string;
  studentCourses: Course[];
  instructorCourses: Course[];
  manageAccountLink: string;
}
