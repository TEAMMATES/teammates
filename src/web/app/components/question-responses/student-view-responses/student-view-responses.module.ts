import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { StudentViewResponsesComponent } from './student-view-responses.component';
import { CommentBoxModule } from '../../comment-box/comment-box.module';


/**
 * Module for feedback response in student results page view.
 */
@NgModule({
  exports: [StudentViewResponsesComponent],
  imports: [
    CommonModule,
    CommentBoxModule,
    StudentViewResponsesComponent,
],
})
export class StudentViewResponsesModule { }
