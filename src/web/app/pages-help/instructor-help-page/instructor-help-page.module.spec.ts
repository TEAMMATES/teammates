import { InstructorHelpPageModule } from './instructor-help-page.module';

describe('InstructorHelpPageModule', () => {
  let instructorHelpPageModule: InstructorHelpPageModule;

  beforeEach(() => {
    instructorHelpPageModule = new InstructorHelpPageModule();
  });

  it('should create an instance', () => {
    expect(instructorHelpPageModule).toBeTruthy();
  });
});
