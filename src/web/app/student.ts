/**
 * A student.
 */
export interface Student {
  name: string;
  lastName: string;
}

/**
 * A student's response status for a feedback session.
 */
export interface StudentResponseStatus {
  email: string;
  name: string;
  sectionName: string;
  teamName: string;
  responseStatus: boolean;
}

/**
 * All the students' response status for a feedback session.
 */
export interface StudentsResponseStatus {
  studentsResponseStatus: StudentResponseStatus[];
}
