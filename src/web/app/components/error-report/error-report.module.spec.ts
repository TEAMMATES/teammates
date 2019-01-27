import { ErrorReportModule } from './error-report.module';

describe('ErrorReportModule', () => {
  let errorReportModule: ErrorReportModule;

  beforeEach(() => {
    errorReportModule = new ErrorReportModule();
  });

  it('should create an instance', () => {
    expect(errorReportModule).toBeTruthy();
  });
});
