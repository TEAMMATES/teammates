import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { InViewportModule } from 'ng-in-viewport';
import { QuestionResponsePanelComponent } from './question-response-panel.component';



import {
  StudentViewResponsesModule,
} from '../question-responses/student-view-responses/student-view-responses.module';


/**
 * Question Response Panel module.
 */
@NgModule({
  imports: [
    CommonModule,
    StudentViewResponsesModule,
    InViewportModule,
    QuestionResponsePanelComponent,
],
  exports: [
    QuestionResponsePanelComponent,
  ],
})
export class QuestionResponsePanelModule { }
