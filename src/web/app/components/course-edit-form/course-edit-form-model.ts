import { Course, Institute } from '../../../types/api-output';

/**
 * The mode of operation for question edit form.
 */
export enum CourseEditFormMode {
  /**
   * Adding a new course.
   */
  ADD,

  /**
   * Editing the existing course.
   */
  EDIT,
}

/**
 * Timezone format used along with course.
 */
export interface Timezone {
  id: string;
  offset: string;
}

/**
 * The form model of course form.
 */
export interface CourseFormModel {
  course: Course;
  timezones: Timezone[];

  isSaving: boolean;
}

/**
 * The form model of course edit form in ADD mode.
 */
export interface CourseAddFormModel extends CourseFormModel {
  institutes: Institute[];
  activeCourses: Course[];
  allCourses: Course[];

  isCopying: boolean;
}

/**
 * The form model of course edit form in Edit mode.
 */
export interface CourseEditFormModel extends CourseFormModel {
  originalCourse: Course;

  isEditing: boolean;
  canModifyCourse: boolean;
}

const DefaultCourse: Course = {
  courseName: '',
  courseId: '',
  institute: '',
  country: '',
  instituteId: '',
  timeZone: 'UTC',
  creationTimestamp: 0,
  deletionTimestamp: 0,
};

const DefaultCourseModel: CourseFormModel = {
  course: structuredClone(DefaultCourse),
  timezones: [],
  isSaving: false,
};

const DefaultCourseAddFormModel: CourseAddFormModel = {
  course: structuredClone(DefaultCourse),
  timezones: [],
  institutes: [],
  activeCourses: [],
  allCourses: [],

  isSaving: false,
  isCopying: false,
};

const DefaultCourseEditFormModel: CourseEditFormModel = {
  course: structuredClone(DefaultCourse),
  originalCourse: structuredClone(DefaultCourse),
  timezones: [],

  isSaving: false,
  isEditing: false,
  canModifyCourse: false,
};

/**
 * Returns default course form model.
 */
export const DEFAULT_COURSE_FORM_MODEL = (): CourseFormModel => {
  return structuredClone(DefaultCourseModel);
};

/**
 * Returns default course edit form model.
 */
export const DEFAULT_COURSE_EDIT_FORM_MODEL = (): CourseEditFormModel => {
  return structuredClone(DefaultCourseEditFormModel);
};

/**
 * Returns default course add form model.
 */
export const DEFAULT_COURSE_ADD_FORM_MODEL = (): CourseAddFormModel => {
  return structuredClone(DefaultCourseAddFormModel);
};
