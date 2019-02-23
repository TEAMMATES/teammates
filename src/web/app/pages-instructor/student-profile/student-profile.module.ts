import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StudentProfileComponent } from './student-profile.component';

/**
 * Module for student profile component.
 */
@NgModule({
  declarations: [
    StudentProfileComponent,
  ],
  exports: [
    StudentProfileComponent,
  ],
  imports: [
    CommonModule,
    NgbModule,
  ],
})
export class StudentProfileModule { }
