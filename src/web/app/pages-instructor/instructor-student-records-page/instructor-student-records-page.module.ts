import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StudentProfileModule } from '../student-profile/student-profile.module';
import { InstructorStudentRecordsPageComponent } from './instructor-student-records-page.component';

/**
 * Module for instructor student records page.
 */
@NgModule({
  declarations: [
    InstructorStudentRecordsPageComponent,
  ],
  exports: [
    InstructorStudentRecordsPageComponent,
  ],
  imports: [
    CommonModule,
    StudentProfileModule,
    NgbModule,
  ],
})
export class InstructorStudentRecordsPageModule { }
