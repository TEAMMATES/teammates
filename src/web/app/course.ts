/**
 * The course.
 */
export interface Course {
  courseId: string;
  courseName: string;
  timeZone: string;
}

/**
 * A list of course.
 */
export interface Courses {
  courses: Course[];
}
