import { Course } from '../../../types/api-output';

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
    institutes: string[];
    activeCourses: Course[];
    allCourses: Course[];

    isCopying: boolean;
}

const DefaultCourse: Course = {
    courseName: '',
    courseId: '',
    institute: '',
    timeZone: 'UTC',
    creationTimestamp: 0,
    deletionTimestamp: 0,
};

const DefaultCourseAddFormModel: CourseAddFormModel = {
    course: JSON.parse(JSON.stringify(DefaultCourse)),
    timezones: [],
    institutes: [],
    activeCourses: [],
    allCourses: [],

    isSaving: false,
    isCopying: false,
};

/**
 * Returns default course add form model.
 */
export const DEFAULT_COURSE_ADD_FORM_MODEL: Function = (): CourseAddFormModel => {
    return JSON.parse(JSON.stringify(DefaultCourseAddFormModel));
};
