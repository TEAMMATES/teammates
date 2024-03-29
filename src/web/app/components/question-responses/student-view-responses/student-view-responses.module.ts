import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { StudentViewResponsesComponent } from './student-view-responses.component';
import { CommentBoxModule } from '../../comment-box/comment-box.module';
import { SingleResponseModule } from '../single-response/single-response.module';

/**
 * Module for feedback response in student results page view.
 */
@NgModule({
  declarations: [StudentViewResponsesComponent],
  exports: [StudentViewResponsesComponent],
  imports: [
    CommonModule,
    SingleResponseModule,
    CommentBoxModule,
  ],
})
export class StudentViewResponsesModule { }
