/**
 * Represents the status of enrolling a student.
 */
export enum EnrollStatus {

  /**
   * The student is newly added to the course.
   */
  NEW = 0,

  /**
   * The student exists in the course, and some fields are modified.
   */
  MODIFIED = 1,

  /**
   * The student exists in the course, and nothing is changed during the enrollment.
   */
  MODIFIED_UNCHANGED = 2,

  /**
   * There are some exceptions thrown when enrolling the student.
   */
  ERROR = 3,

  /**
   * The student is not in the enroll list.
   */
  UNMODIFIED = 4,
}
