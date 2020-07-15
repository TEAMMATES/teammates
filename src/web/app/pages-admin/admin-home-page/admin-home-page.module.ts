import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { AdminHomePageComponent } from './admin-home-page.component';

const routes: Routes = [
  {
    path: '',
    component: AdminHomePageComponent,
  },
];

/**
 * Module for admin home page.
 */
@NgModule({
  declarations: [
    AdminHomePageComponent,
  ],
  exports: [
    AdminHomePageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
  ],
})
export class AdminHomePageModule { }
