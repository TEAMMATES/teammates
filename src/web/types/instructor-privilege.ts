import { InstructorPrivilege } from './api-output';

/**
 * The default instructor privilege.
 */
export const DEFAULT_INSTRUCTOR_PRIVILEGE: InstructorPrivilege = {
  canModifyCourse: false,
  canModifySession: false,
  canModifyStudent: false,
  canSubmitSessionInSections: false,
  canModifyInstructor: false,
  canViewStudentInSections: false,
  canModifySessionCommentsInSections: false,
  canViewSessionInSections: false,
};
