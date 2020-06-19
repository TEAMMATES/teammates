import { Pipe, PipeTransform } from '@angular/core';
import { JoinState } from '../../../types/api-output';

/**
 * Pipe to handle the display of join state in the student list component.
 */
@Pipe({
  name: 'joinState',
})
export class JoinStatePipe implements PipeTransform {

  /**
   * Transforms {@code JoinState} to a join state display for the student list component.
   */
  transform(joinState: JoinState): any {
    switch (joinState) {
      case (JoinState.JOINED):
        return 'Joined';
      case (JoinState.NOT_JOINED):
        return 'Yet to Join';
      default:
        return 'Unknown';
    }
  }

}
