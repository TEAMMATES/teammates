import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TimeDisplayerModule } from '../../components/time-displayer/time-displayer.module';
import { SessionResultPageComponent } from './session-result-page.component';

/**
 * Module for feedback session result page.
 */
@NgModule({
  imports: [
    CommonModule,
    TimeDisplayerModule,
  ],
  declarations: [
    SessionResultPageComponent,
  ],
  exports: [
    SessionResultPageComponent,
  ],
})
export class SessionResultPageModule { }
