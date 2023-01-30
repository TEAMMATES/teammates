import { JoinState } from '../../../types/api-output';

/**
 * Contains a list of students in a section.
 */
export interface StudentListSectionData {
  sectionName: string;
  isAllowedToViewStudentInSection: boolean;
  isAllowedToModifyStudent: boolean;
  students: StudentListStudentData[];
}

/**
 * Contains details about a student to be displayed in the list.
 */
export interface StudentListStudentData {
  name: string;
  email: string;
  status?: JoinState;
  team: string;
}
