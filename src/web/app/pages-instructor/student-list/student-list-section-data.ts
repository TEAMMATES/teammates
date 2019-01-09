export interface StudentListSectionData {
  sectionName: string;
  isAllowedToViewStudentInSection: boolean;
  isAllowedToModifyStudent: boolean;
  students: StudentListStudentData[];
}

interface StudentListStudentData {
  name: string;
  email: string;
  status: string;
  team: string;
}
