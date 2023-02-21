import { Pipe, PipeTransform } from '@angular/core';
import { VisibilityControl } from '../../../types/visibility-control';

/**
 * Pipe to handle the display of a map of {@code VisibilityControl} in visibility message.
 */
@Pipe({
  name: 'visibilityCapability',
})
export class VisibilityCapabilityPipe implements PipeTransform {

  b: Record<number, boolean> = Object.fromEntries([...new Array(5)].map((_, i) => [i, false]));
  /**
   * Transforms a map of VisibilityControl to a capability description.
   *
   * @param controls a map where the key is the visibility control
   * and the value indicates whether the visibility control is granted or not.
   */
  transform(controls: { [TKey in VisibilityControl]: boolean }): string {
    let message: string = 'can see your response';

    if (controls.SHOW_RECIPIENT_NAME) {
      this.b[0] = true;
      message += ', the name of the recipient';

      if (controls.SHOW_GIVER_NAME) {
        this.b[1] = true;
        message += ', and your name';
      } else {
        this.b[2] = true;
        message += ', but not your name';
      }
    } else if (controls.SHOW_GIVER_NAME) {
      this.b[3] = true;
      message += ', and your name, but not the name of the recipient';
    } else {
      this.b[4] = true;
      message += ', but not the name of the recipient, or your name';
    }

    return message;
  }

}
