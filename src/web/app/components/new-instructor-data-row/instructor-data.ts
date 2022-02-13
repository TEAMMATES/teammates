/**
 * Represents the data of a new instructor.
 */
export interface InstructorData {
  name: string;
  email: string;
  institution: string;
  status: string;
  isCurrentlyBeingEdited: boolean;
  joinLink?: string;
  message?: string;
}
