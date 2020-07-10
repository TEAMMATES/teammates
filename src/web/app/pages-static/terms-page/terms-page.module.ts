import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TermsPageComponent } from './terms-page.component';

const routes: Routes = [
  {
    path: '',
    component: TermsPageComponent,
  },
];

/**
 * Module for terms page.
 */
@NgModule({
  declarations: [
    TermsPageComponent,
  ],
  exports: [
    TermsPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
})
export class TermsPageModule { }
