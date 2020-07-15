import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ExampleBoxComponent } from './example-box.component';

/**
 * Module for example boxes used in help page.
 */
@NgModule({
  declarations: [ExampleBoxComponent],
  exports: [ExampleBoxComponent],
  imports: [CommonModule],
})
export class ExampleBoxModule { }
