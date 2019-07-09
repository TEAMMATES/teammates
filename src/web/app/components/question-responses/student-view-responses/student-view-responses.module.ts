import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CommentBoxModule } from '../../comment-box/comment-box.module';
import { SingleResponseModule } from '../single-response/single-response.module';
import { StudentViewResponsesComponent } from './student-view-responses.component';

/**
 * Module for feedback response in student results page view.
 */
@NgModule({
  declarations: [StudentViewResponsesComponent],
  exports: [StudentViewResponsesComponent],
  imports: [
    CommentBoxModule,
    CommonModule,
    SingleResponseModule,
  ],
})
export class StudentViewResponsesModule { }
