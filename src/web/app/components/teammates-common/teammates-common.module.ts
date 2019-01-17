import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { EnumToArrayPipe } from './enum-to-array.pipe';

/**
 * Common module in the project.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    EnumToArrayPipe,
  ],
  exports: [
    EnumToArrayPipe,
  ],
})
export class TeammatesCommonModule { }
