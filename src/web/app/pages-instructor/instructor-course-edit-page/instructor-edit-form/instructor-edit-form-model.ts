import {
  CourseLevelPrivileges, Role, SectionLevelPrivileges,
  SessionLevelPrivileges
} from '../instructor-privileges-model';

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
  courseLevel: CourseLevelPrivileges;
  sectionLevel: { [section: string]: SectionLevelPrivileges };
  sessionLevel: { [section: string]: { [session: string]: SessionLevelPrivileges } };

  isEditable: boolean;
  isSaving: boolean;
}
