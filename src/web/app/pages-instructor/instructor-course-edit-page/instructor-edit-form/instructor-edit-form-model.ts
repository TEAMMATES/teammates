import { Privileges, Role } from '../instructor-privileges-model';

/**
 * The mode of operation for the instructor edit form.
 */
export enum InstructorEditFormMode {
  /**
   * Adding a new instructor.
   */
  ADD,

  /**
   * Editing an existing instructor.
   */
  EDIT,
}

/**
 * The form model of the instructor edit form.
 */
export interface InstructorEditFormModel {
  googleId: string;
  name: string;
  email: string;
  role: Role;
  isDisplayedToStudents: boolean;
  displayedName: string;
  privileges: Privileges;

  isEditable: boolean;
  isSaving: boolean;
}
