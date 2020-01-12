import { Pipe, PipeTransform } from '@angular/core';
import { Gender } from '../../../types/api-output';

/**
 * Pipe to handle the display of gender options in the student profile page.
 */
@Pipe({
  name: 'genderFormat',
})

export class GenderFormatPipe implements PipeTransform {

  /**
   * Transforms {@code gender} to a gender display option for the student profile page.
   */
  transform(gender: Gender): string {
    switch (gender) {
      case Gender.MALE:
        return 'Male';
      case Gender.FEMALE:
        return 'Female';
      case Gender.OTHER:
        return 'Not Specified';
      default:
        return 'Unknown';
    }
  }
}
