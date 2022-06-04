import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { PanelLinkComponent } from './panel-link.component';

/**
 * Module for link icon used in panel headers.
 */
@NgModule({
  declarations: [PanelLinkComponent],
  exports: [PanelLinkComponent],
  imports: [CommonModule],
})
export class PanelLinkModule { }
