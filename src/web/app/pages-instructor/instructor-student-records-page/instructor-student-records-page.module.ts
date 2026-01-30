import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NgbCollapseModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorStudentRecordsPageComponent } from './instructor-student-records-page.component';
import { CommentToCommentRowModelPipe } from '../../components/comment-box/comment-to-comment-row-model.pipe';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';



import {
    GrqRgqViewResponsesModule,
} from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.module';

const routes: Routes = [
  {
    path: '',
    component: InstructorStudentRecordsPageComponent,
  },
];

/**
 * Module for instructor student records page.
 */
@NgModule({
  exports: [
    InstructorStudentRecordsPageComponent,
  ],
  imports: [
    CommonModule,
    NgbCollapseModule,
    GrqRgqViewResponsesModule,
    RouterModule.forChild(routes),
    InstructorStudentRecordsPageComponent,
],
  providers: [
    CommentToCommentRowModelPipe,
    CommentsToCommentTableModelPipe,
  ],
})
export class InstructorStudentRecordsPageModule { }
