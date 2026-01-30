import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SessionResultPageComponent } from './session-result-page.component';


import { QuestionResponsePanelModule } from '../../components/question-response-panel/question-response-panel.module';

import {
  StudentViewResponsesModule,
} from '../../components/question-responses/student-view-responses/student-view-responses.module';


const routes: Routes = [
  {
    path: '',
    component: SessionResultPageComponent,
  },
];

/**
 * Module for feedback session result page.
 */
@NgModule({
  imports: [
    CommonModule,
    StudentViewResponsesModule,
    RouterModule.forChild(routes),
    QuestionResponsePanelModule,
    SessionResultPageComponent,
],
  exports: [
    SessionResultPageComponent,
  ],
})
export class SessionResultPageModule { }
