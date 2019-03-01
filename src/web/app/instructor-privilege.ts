import { InstructorPrivilege } from '../types/api-output';

/**
 * The default instructor privilege.
 */
export const defaultInstructorPrivilege: InstructorPrivilege = {
  canModifyCourse: true,
  canModifySession: true,
  canModifyStudent: true,
  canSubmitSessionInSections: true,
  canModifyInstructor: true,
  canViewStudentInSections: true,
  canModifySessionCommentsInSections: true,
  canViewSessionInSections: true,
};
