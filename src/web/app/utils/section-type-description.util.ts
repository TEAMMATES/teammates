import { InstructorSessionResultSectionType } from '../pages-instructor/instructor-session-result-page/instructor-session-result-section-type.enum';

export function instructorSessionResultToDescription(sectionType: InstructorSessionResultSectionType): string {
  switch (sectionType) {
    case InstructorSessionResultSectionType.BOTH:
      return 'Show response only if both are in the selected section';
    case InstructorSessionResultSectionType.EITHER:
      return 'Show response if either the giver or evaluee is in the selected section';
    case InstructorSessionResultSectionType.EVALUEE:
      return 'Show response if the evaluee is in the selected section';
    case InstructorSessionResultSectionType.GIVER:
      return 'Show response if the giver is in the selected section';
    default:
      return 'Unknown';
  }
}
