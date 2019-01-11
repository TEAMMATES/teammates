export interface StudentListSectionData {
  sectionName: string;
  isAllowedToViewStudentInSection: boolean;
  isAllowedToModifyStudent: boolean;
  students: StudentListStudentData[];
}

export interface StudentListStudentData {
  name: string;
  email: string;
  status: string;
  team: string;
  photoUrl?: string;
}
