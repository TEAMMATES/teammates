import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InstructorSearchBarComponent } from './instructor-search-bar/instructor-search-bar.component';
import { StudentResultTableComponent } from './student-result-table/student-result-table.component';
import { CommentBoxModule } from '../../components/comment-box/comment-box.module';
import { SingleResponseModule } from '../../components/question-responses/single-response/single-response.module';
import { StudentListModule } from '../../components/student-list/student-list.module';
import { Pipes } from '../../pipes/pipes.module';

/**
 * Module for different components used in instructor search page.
 */
@NgModule({
  declarations: [
    InstructorSearchBarComponent,
    StudentResultTableComponent,
  ],
  exports: [
    InstructorSearchBarComponent,
    StudentResultTableComponent,
  ],
  imports: [
    CommonModule,
    StudentListModule,
    FormsModule,
    CommentBoxModule,
    SingleResponseModule,
    Pipes,
  ],
})
export class InstructorSearchComponentsModule { }
