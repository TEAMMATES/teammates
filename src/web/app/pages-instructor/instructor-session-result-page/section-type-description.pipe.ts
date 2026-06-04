import { Pipe, PipeTransform } from '@angular/core';
import { InstructorSessionResultSectionType } from './instructor-session-result-section-type.enum';
import { instructorSessionResultToDescription } from '../../utils/section-type-description.util';

/**
 * Pipe to transform {@link InstructorSessionResultSectionType} to a description.
 */
@Pipe({ name: 'sectionTypeDescription' })
export class SectionTypeDescriptionPipe implements PipeTransform {
  /**
   * Transforms a {@link InstructorSessionResultSectionType} to a description
   */
  transform(sectionType: InstructorSessionResultSectionType): any {
    return instructorSessionResultToDescription(sectionType);
  }
}
