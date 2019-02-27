import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { QuestionTextWithInfoModule } from '../../components/question-text-with-info/question-text-with-info.module';
import { TimeDisplayerModule } from '../../components/time-displayer/time-displayer.module';
import { SessionResultPageComponent } from './session-result-page.component';

/**
 * Module for feedback session result page.
 */
@NgModule({
  imports: [
    CommonModule,
    TimeDisplayerModule,
    QuestionTextWithInfoModule,
  ],
  declarations: [
    SessionResultPageComponent,
  ],
  exports: [
    SessionResultPageComponent,
  ],
})
export class SessionResultPageModule { }
