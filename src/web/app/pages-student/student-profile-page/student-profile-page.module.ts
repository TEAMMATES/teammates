import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { GenderFormatPipe } from './student-profile-gender.pipe';
import { StudentProfilePageComponent } from './student-profile-page.component';

/**
 * Module for student profile page.
 */
@NgModule({
  declarations: [
    StudentProfilePageComponent,
    GenderFormatPipe,
  ],
  exports: [
    StudentProfilePageComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    TeammatesCommonModule,
  ],
})
export class StudentProfilePageModule { }
