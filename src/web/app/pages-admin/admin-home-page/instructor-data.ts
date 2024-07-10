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
