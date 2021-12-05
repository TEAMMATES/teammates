import { Pipe, PipeTransform } from '@angular/core';
import { VisibilityControl } from '../../../types/visibility-control';

/**
 * Pipe to handle the display of a map of {@code VisibilityControl} in visibility message.
 */
@Pipe({
  name: 'visibilityCapability',
})
export class VisibilityCapabilityPipe implements PipeTransform {

  /**
   * Transforms a map of VisibilityControl to a capability description.
   *
   * @param controls a map where the key is the visibility control
   * and the value indicates whether the visibility control is granted or not.
   */
  transform(controls: { [TKey in VisibilityControl]: boolean }): string {
    let message: string = 'can see your response';

    if (controls.SHOW_RECIPIENT_NAME) {
      message += ', the name of the recipient';

      if (controls.SHOW_GIVER_NAME) {
        message += ', and your name';
      } else {
        message += ', but not your name';
      }
    } else if (controls.SHOW_GIVER_NAME) {
      message += ', and your name, but not the name of the recipient';
    } else {
      message += ', but not the name of the recipient, or your name';
    }

    return message;
  }

}
