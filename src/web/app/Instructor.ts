import { Privileges, Role } from './instructor-privilege';

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
