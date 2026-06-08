import { instructorSessionResultToDescription } from './section-type-description.util';
import { InstructorSessionResultSectionType } from '../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';

describe('instructorSessionResultToDescription', () => {
  it('should return correct description for BOTH', () => {
    expect(instructorSessionResultToDescription(InstructorSessionResultSectionType.BOTH)).toBe(
      'Show response only if both are in the selected section',
    );
  });

  it('should return correct description for EITHER', () => {
    expect(instructorSessionResultToDescription(InstructorSessionResultSectionType.EITHER)).toBe(
      'Show response if either the giver or evaluee is in the selected section',
    );
  });

  it('should return correct description for EVALUEE', () => {
    expect(instructorSessionResultToDescription(InstructorSessionResultSectionType.EVALUEE)).toBe(
      'Show response if the evaluee is in the selected section',
    );
  });

  it('should return correct description for GIVER', () => {
    expect(instructorSessionResultToDescription(InstructorSessionResultSectionType.GIVER)).toBe(
      'Show response if the giver is in the selected section',
    );
  });

  it('should return "Unknown" for unrecognized type', () => {
    expect(instructorSessionResultToDescription('INVALID' as InstructorSessionResultSectionType)).toBe('Unknown');
  });
});
