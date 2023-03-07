import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CourseCopyComponent } from './course-copy.component';

@NgModule({
  declarations: [
    CourseCopyComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
  ],
  exports: [
    CourseCopyComponent,
  ],
})
export class CourseCopyModule { }
