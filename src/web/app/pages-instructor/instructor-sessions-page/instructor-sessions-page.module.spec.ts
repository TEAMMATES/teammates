import { InstructorSessionsPageModule } from './instructor-sessions-page.module';

describe('InstructorSessionsPageModule', () => {
  let instructorSessionsPageModule: InstructorSessionsPageModule;

  beforeEach(() => {
    instructorSessionsPageModule = new InstructorSessionsPageModule();
  });

  it('should create an instance', () => {
    expect(instructorSessionsPageModule).toBeTruthy();
  });
});
