import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbProgressbarModule } from '@ng-bootstrap/ng-bootstrap';
import { LoaderBarComponent } from './loader-bar.component';

/**
 * Loading progress bar module.
 */
@NgModule({
  declarations: [LoaderBarComponent],
  imports: [
    NgbProgressbarModule,
    CommonModule,
  ],
  exports: [
    LoaderBarComponent,
  ],
})
export class LoaderBarModule { }
