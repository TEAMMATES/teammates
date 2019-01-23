/**
 * The intent of calling the REST API.
 */
export enum Intent {

  /**
   * To get the full detail of the entities.
   */
  FULL_DETAIL = 'FULL_DETAIL',

  /**
   * To submit the feedback session as instructors.
   */
  INSTRUCTOR_SUBMISSION = 'INSTRUCTOR_SUBMISSION',

  /**
   * To submit the feedback session as students.
   */
  STUDENT_SUBMISSION = 'STUDENT_SUBMISSION',
}
