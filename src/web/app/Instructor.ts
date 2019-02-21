import { Privileges, Role } from './pages-instructor/instructor-course-edit-page/instructor-privileges-model';

/**
 * An instructor.
 */
export interface Instructor {
  googleId: string;
  name: string;
  email: string;
  role: Role;
  isDisplayedToStudents: boolean;
  displayedName: string;
  privileges: Privileges;
}
