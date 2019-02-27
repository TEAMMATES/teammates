import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TimeDisplayerPipe } from './time-displayer.pipe';

/**
 * Module for TimeDisplayerPipe.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  exports: [TimeDisplayerPipe],
  declarations: [TimeDisplayerPipe],
})
export class TimeDisplayerModule { }
