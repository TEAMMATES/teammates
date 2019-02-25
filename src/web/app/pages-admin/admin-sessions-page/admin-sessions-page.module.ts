import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminSessionsPageComponent } from './admin-sessions-page.component';

/**
 * Module for admin sessions page.
 */
@NgModule({
  declarations: [
    AdminSessionsPageComponent,
  ],
  exports: [
    AdminSessionsPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
  ],
})
export class AdminSessionsPageModule { }
