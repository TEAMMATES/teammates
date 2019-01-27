import { Pipe, PipeTransform } from '@angular/core';

/**
 * Pipe to handle the display of gender options in the student profile page.
 */
@Pipe({
  name: 'genderFormat',
})
export class GenderFormatPipe implements PipeTransform {

  /**
   * Transforms the list of genders as display options for the student profile page.
   */
  transform(gender: String): string {
    switch (gender) {
      case 'male':
        return 'Male';
      case 'female':
        return 'Female';
      case 'other':
        return 'Not Specified';
      default:
        return 'Unknown';
    }
  }
}
