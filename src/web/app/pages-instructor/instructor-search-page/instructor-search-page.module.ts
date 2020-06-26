import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommentBoxModule } from '../../components/comment-box/comment-box.module';
import { SingleResponseModule } from '../../components/question-responses/single-response/single-response.module';
import { StudentListModule } from '../../components/student-list/student-list.module';
import { CommentResultTableComponent } from './comment-result-table/comment-result-table.component';
import { InstructorSearchBarComponent } from './instructor-search-bar/instructor-search-bar.component';
import { InstructorSearchPageComponent } from './instructor-search-page.component';
import { StudentResultTableComponent } from './student-result-table/student-result-table.component';

const routes: Routes = [
  {
    path: '',
    component: InstructorSearchPageComponent,
  },
];

/**
 * Module for instructor search page.
 */
@NgModule({
  declarations: [
    InstructorSearchPageComponent,
    InstructorSearchBarComponent,
    StudentResultTableComponent,
    CommentResultTableComponent,
  ],
  exports: [
    InstructorSearchPageComponent,
    InstructorSearchBarComponent,
    StudentResultTableComponent,
    CommentResultTableComponent,
  ],
  imports: [
    CommonModule,
    StudentListModule,
    FormsModule,
    RouterModule.forChild(routes),
    NgbModule,
    CommentBoxModule,
    SingleResponseModule,
  ],
})
export class InstructorSearchPageModule { }
