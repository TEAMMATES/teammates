import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PageNotFoundComponent } from './page-not-found.component';

/**
 * Module for "page-not-found" page.
 */
@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [
    PageNotFoundComponent,
  ],
  exports: [
    PageNotFoundComponent,
  ],
})
export class PageNotFoundModule {}
