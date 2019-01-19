import { Pipe, PipeTransform } from '@angular/core';

/**
 * Pipe to handle the display of a contribution question answer.
 */
@Pipe({
  name: 'contributionPointDescription',
})
export class ContributionPointDescriptionPipe implements PipeTransform {

  /**
   * Transforms a contribution point to a simple name.
   */
  transform(point: number): string {
    if (point > 100) {
      return `Equal share + ${ point - 100 }%`; // Do more
    }

    if (point === 100) {
      return 'Equal share'; // Do same
    }

    if (point > 0) {
      return `Equal share - ${ 100 - point }%`; // Do less
    }

    if (point === 0) {
      return '0%'; // Do none
    }

    return 'Unknown';
  }

}
