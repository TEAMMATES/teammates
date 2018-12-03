import { InstructorSessionEditPageModule } from './instructor-session-edit-page.module';

describe('InstructorSessionEditPageModule', () => {
  let instructorSessionEditPageModule: InstructorSessionEditPageModule;

  beforeEach(() => {
    instructorSessionEditPageModule = new InstructorSessionEditPageModule();
  });

  it('should create an instance', () => {
    expect(instructorSessionEditPageModule).toBeTruthy();
  });
});
