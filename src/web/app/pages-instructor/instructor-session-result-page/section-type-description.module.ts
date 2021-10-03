import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SectionTypeDescriptionPipe } from './section-type-description.pipe';

/**
 * Section Type Description pipe module.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    SectionTypeDescriptionPipe,
  ],
  exports: [
    SectionTypeDescriptionPipe,
  ],
})
export class SectionTypeDescriptionModule { }
