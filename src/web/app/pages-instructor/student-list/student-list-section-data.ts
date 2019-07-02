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
  status: JoinState;
  team: string;
  photoUrl?: string;
}

/**
 * Sort criteria for the students table.
 */
export enum SortBy {
  /**
   * Nothing.
   */
  NONE,

  /**
   * Section Name.
   */
  SECTION_NAME,

  /**
   * Team name.
   */
  TEAM_NAME,

  /**
   * Student Name.
   */
  STUDENT_NAME,

  /**
   * Status.
   */
  STATUS,

  /**
   * Email.
   */
  EMAIL,
}

/**
 * Sort order for the students table.
 */
export enum SortOrder {
  /**
   * Descending sort order.
   */
  DESC,

  /**
   * Ascending sort order
   */
  ASC,
}
