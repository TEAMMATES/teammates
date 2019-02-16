import { InstructorCourseEditPageModule } from './instructor-course-edit-page.module';

describe('InstructorCourseEditPageModule', () => {
  let instructorCourseEditPageModule: InstructorCourseEditPageModule;

  beforeEach(() => {
    instructorCourseEditPageModule = new InstructorCourseEditPageModule();
  });

  it('should create an instance', () => {
    expect(instructorCourseEditPageModule).toBeTruthy();
  });
});
