import { Pipe, PipeTransform } from '@angular/core';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';

/**
 * Pipe to transform {@link InstructorSessionResultSectionType} to a description.
 */
@Pipe({
  name: 'sectionTypeDescription',
})
export class SectionTypeDescriptionPipe implements PipeTransform {

  /**
   * Transforms a {@link InstructorSessionResultSectionType} to a description
   */
  transform(sectionType: InstructorSessionResultSectionType): any {
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

}
