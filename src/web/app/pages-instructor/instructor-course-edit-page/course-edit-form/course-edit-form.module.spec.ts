import { CourseEditFormModule } from './course-edit-form.module';

describe('CourseEditFormModule', () => {
  let courseEditFormModule: CourseEditFormModule;

  beforeEach(() => {
    courseEditFormModule = new CourseEditFormModule();
  });

  it('should create an instance', () => {
    expect(courseEditFormModule).toBeTruthy();
  });
});
