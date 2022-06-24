import { CourseModel } from '../../pages-instructor/instructor-courses-page/instructor-courses-page.component';
import TestCourses from '../courses';

const cs1231CourseModel: CourseModel = {
  course: TestCourses.cs1231,
  canModifyCourse: true,
  canModifyStudent: true,
  isLoadingCourseStats: false,
};

const cs3281CourseModel: CourseModel = {
  course: TestCourses.cs3281,
  canModifyCourse: true,
  canModifyStudent: true,
  isLoadingCourseStats: false,
};

const cs3282CourseModel: CourseModel = {
  course: TestCourses.cs3282,
  canModifyCourse: false,
  canModifyStudent: false,
  isLoadingCourseStats: false,
};

const st4234CourseModel: CourseModel = {
  course: TestCourses.st4234,
  canModifyCourse: false,
  canModifyStudent: true,
  isLoadingCourseStats: false,
};

const TestCourseModels = {
  cs1231CourseModel,
  cs3281CourseModel,
  cs3282CourseModel,
  st4234CourseModel,
};

export default TestCourseModels;
