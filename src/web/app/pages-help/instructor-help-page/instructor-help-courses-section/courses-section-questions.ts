/**
 * Unique identifiers for each question in the courses section of instructor help page
 */
export enum CoursesSectionQuestions {
  /**
   * How do I add students to a course?
   */
  COURSE_ADD_STUDENTS = 'course-add-students',

  /**
   * Is there a size limit for a course?
   */
  SIZE_LIMIT = 'size-limit',

  /**
   * What should I do if my course doesn’t have teams?
   */
  NO_TEAMS = 'no-teams',

  /**
   * What are sections meant for?
   */
  SECTIONS = 'sections',

  /**
   * How do I enroll students into sections?
   */
  ENROLL_SECTIONS = 'enroll-sections',

  /**
   * How do I add instructors to my course?
   */
  COURSE_ADD_INSTRUCTOR = 'course-add-instructor',

  /**
   * How do I edit the information of an instructor in my course?
   */
  COURSE_EDIT_INSTRUCTOR = 'course-edit-instructor',

  /**
   * How do I set an instructor's access level?
   */
  COURSE_INSTRUCTOR_ACCESS = 'course-instructor-access',

  /**
   * How do I set custom privileges for an instructor?
   */
  PRIVILEGES = 'privileges',

  /**
   * How do I view a list of students in a course?
   */
  COURSE_VIEW_STUDENTS = 'course-view-students',

  /**
   * How do I change a student's section?
   */
  CHANGE_SECTION = 'change-section',

  /**
   * What should I do if a student says his/her courses have disappeared from the system?
   */
  DISAPPEARED_COURSE = 'disappeared-course',

  /**
   * How do I delete students from a course?
   */
  DEL_STUDENTS = 'del-students',

  /**
   * How do I archive a course?
   */
  COURSE_ARCHIVE = 'course-archive',

  /**
   * How do I view courses I have archived?
   */
  COURSE_VIEW_ARCHIVED = 'course-view-archived',

  /**
   * How do I unarchive an archived course?
   */
  COURSE_UNARCHIVE = 'course-unarchive',

  /**
   * How do I view courses I have deleted?
   */
  COURSE_VIEW_DELETED = 'course-view-deleted',

  /**
   * How do I restore a deleted course?
   */
  COURSE_RESTORE = 'course-restore',

  /**
   * How do I permanently delete a course?
   */
  PERM_DEL = 'perm-del',

  /**
   * How do I restore/delete all courses from Recycle Bin?
   */
  RESTORE_ALL = 'restore-all',
}
