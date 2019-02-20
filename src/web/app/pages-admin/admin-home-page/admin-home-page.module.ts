import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AdminHomePageComponent } from './admin-home-page.component';

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
  ],
})
export class AdminHomePageModule { }
