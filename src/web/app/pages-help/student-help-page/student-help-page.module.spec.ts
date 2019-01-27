import { StudentHelpPageModule } from './student-help-page.module';

describe('StudentHelpPageModule', () => {
  let studentHelpPageModule: StudentHelpPageModule;

  beforeEach(() => {
    studentHelpPageModule = new StudentHelpPageModule();
  });

  it('should create an instance', () => {
    expect(studentHelpPageModule).toBeTruthy();
  });
});
