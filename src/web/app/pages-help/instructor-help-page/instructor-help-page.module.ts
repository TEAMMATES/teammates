import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbCollapseModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';


import {
  InstructorHelpCoursesSectionComponent,
} from './instructor-help-courses-section/instructor-help-courses-section.component';
import {
  InstructorHelpGeneralSectionComponent,
} from './instructor-help-general-section/instructor-help-general-section.component';
import { InstructorHelpPageComponent } from './instructor-help-page.component';
import { InstructorHelpPanelComponent } from './instructor-help-panel/instructor-help-panel.component';
import {
  InstructorHelpQuestionsSectionComponent,
} from './instructor-help-questions-section/instructor-help-questions-section.component';
import {
  InstructorHelpSessionsSectionComponent,
} from './instructor-help-sessions-section/instructor-help-sessions-section.component';
import {
  InstructorHelpStudentsSectionComponent,
} from './instructor-help-students-section/instructor-help-students-section.component';

import { CommentBoxModule } from '../../components/comment-box/comment-box.module';




import {
  QuestionEditBriefDescriptionFormModule,
} from '../../components/question-edit-brief-description-form/question-edit-brief-description-form.module';
import { QuestionEditFormModule } from '../../components/question-edit-form/question-edit-form.module';
import { QuestionResponsePanelModule } from '../../components/question-response-panel/question-response-panel.module';

import {
  StudentViewResponsesModule,
} from '../../components/question-responses/student-view-responses/student-view-responses.module';
import {
  QuestionSubmissionFormModule,
} from '../../components/question-submission-form/question-submission-form.module';

import {
  SessionEditFormModule,
} from '../../components/session-edit-form/session-edit-form.module';





import {
  InstructorSearchComponentsModule,
} from '../../pages-instructor/instructor-search-page/instructor-search-components.module';
import {
  InstructorSessionResultViewModule,
} from '../../pages-instructor/instructor-session-result-page/instructor-session-result-view.module';

const routes: Routes = [
  {
    path: '',
    component: InstructorHelpPageComponent,
  },
];

/**
 * Module for instructor help page.
 */
@NgModule({
  imports: [
    CommentBoxModule,
    CommonModule,
    FormsModule,
    NgbCollapseModule,
    NgbTooltipModule,
    RouterModule.forChild(routes),
    QuestionEditFormModule,
    ReactiveFormsModule,
    SessionEditFormModule,
    QuestionSubmissionFormModule,
    StudentViewResponsesModule,
    InstructorSessionResultViewModule,
    InstructorSearchComponentsModule,
    QuestionResponsePanelModule,
    QuestionEditBriefDescriptionFormModule,
    InstructorHelpPageComponent,
    InstructorHelpPanelComponent,
    InstructorHelpStudentsSectionComponent,
    InstructorHelpSessionsSectionComponent,
    InstructorHelpQuestionsSectionComponent,
    InstructorHelpCoursesSectionComponent,
    InstructorHelpGeneralSectionComponent,
],
  exports: [
    InstructorHelpPageComponent,
  ],
})
export class InstructorHelpPageModule { }
