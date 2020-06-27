import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommentBoxModule } from '../../components/comment-box/comment-box.module';
import { SingleResponseModule } from '../../components/question-responses/single-response/single-response.module';
import { StudentListModule } from '../../components/student-list/student-list.module';
import { CommentResultTableComponent } from './comment-result-table/comment-result-table.component';
import { InstructorSearchBarComponent } from './instructor-search-bar/instructor-search-bar.component';
import { StudentResultTableComponent } from './student-result-table/student-result-table.component';

/**
 * Module for different components used in instructor search page.
 */
@NgModule({
  declarations: [
    InstructorSearchBarComponent,
    StudentResultTableComponent,
    CommentResultTableComponent,
  ],
  exports: [
    InstructorSearchBarComponent,
    StudentResultTableComponent,
    CommentResultTableComponent,
  ],
  imports: [
    CommonModule,
    StudentListModule,
    FormsModule,
    CommentBoxModule,
    SingleResponseModule,
  ],
})
export class InstructorSearchComponentsModule { }
