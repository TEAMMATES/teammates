import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ContactPageComponent } from './contact-page.component';

const routes: Routes = [
  {
    path: '',
    component: ContactPageComponent,
  },
];

/**
 * Module for contact page.
 */
@NgModule({
  exports: [
    ContactPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    ContactPageComponent,
  ],
})
export class ContactPageModule { }
