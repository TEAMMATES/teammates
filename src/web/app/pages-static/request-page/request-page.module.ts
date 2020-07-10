import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RequestPageComponent } from './request-page.component';

const routes: Routes = [
  {
    path: '',
    component: RequestPageComponent,
  },
];

/**
 * Module for request page.
 */
@NgModule({
  declarations: [
    RequestPageComponent,
  ],
  exports: [
    RequestPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
})
export class RequestPageModule { }
