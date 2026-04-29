/**
 * The model for a row of the student list info table.
 */
export interface StudentListInfoTableRowModel {
  id: string;
  email: string;
  name: string;
  teamName: string;
  sectionName: string;

  hasSubmittedSession: boolean;

  isSelected: boolean;
}

/**
 * The model for a row of the instructor list info table.
 */
export interface InstructorListInfoTableRowModel {
  id: string;
  email: string;
  name: string;

  hasSubmittedSession: boolean;

  isSelected: boolean;
}
