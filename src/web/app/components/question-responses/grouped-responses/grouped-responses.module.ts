import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { GroupedResponsesComponent } from './grouped-responses.component';

import { CommentBoxModule } from '../../comment-box/comment-box.module';




/**
 * Module for a list of responses grouped in GRQ/RGQ mode.
 */
@NgModule({
  exports: [GroupedResponsesComponent],
  imports: [
    CommonModule,
    CommentBoxModule,
    NgbTooltipModule,
    GroupedResponsesComponent,
],
})
export class GroupedResponsesModule { }
