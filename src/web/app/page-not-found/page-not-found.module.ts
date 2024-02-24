import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PageNotFoundComponent } from './page-not-found.component';
import { TeammatesRouterModule } from '../components/teammates-router/teammates-router.module';

/**
 * Module for "page-not-found" page.
 */
@NgModule({
  imports: [
    CommonModule,
    TeammatesRouterModule,
  ],
  declarations: [
    PageNotFoundComponent,
  ],
  exports: [
    PageNotFoundComponent,
  ],
})
export class PageNotFoundModule {}
