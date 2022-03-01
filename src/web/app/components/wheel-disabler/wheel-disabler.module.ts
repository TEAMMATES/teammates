import { NgModule } from '@angular/core';
import { WheelDisablerDirective } from './wheel-disabler.directive';

/**
 * Module for progress bar used to show download progress.
 */
@NgModule({
  declarations: [WheelDisablerDirective],
  imports: [],
  exports: [
    WheelDisablerDirective,
  ],
})
export class WheelDisablerModule { }
