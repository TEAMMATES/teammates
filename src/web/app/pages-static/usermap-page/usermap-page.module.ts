import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UsermapPageComponent } from './usermap-page.component';

const routes: Routes = [
  {
    path: '',
    component: UsermapPageComponent,
  },
];

/**
 * Module for usermap page.
 */
@NgModule({
  declarations: [
    UsermapPageComponent,
  ],
  exports: [
    UsermapPageComponent,
  ],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
  ],
})
export class UsermapPageModule { }
