import { Pipe, PipeTransform } from '@angular/core';
import { JoinState } from '../../../types/api-output';
import { joinStateToString } from '../../utils/join-state.util';

/**
 * Pipe to handle the display of join state in the student list component.
 */
@Pipe({ name: 'joinState' })
export class JoinStatePipe implements PipeTransform {
  /**
   * Transforms {@code JoinState} to a join state display for the student list component.
   */
  transform(joinState?: JoinState): any {
    return joinStateToString(joinState);
  }
}
