import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  QuestionAdditionalInfoModule,
} from '../question-types/question-additional-info/question-additional-info.module';
import { QuestionTextWithInfoComponent } from './question-text-with-info.component';

@NgModule({
  declarations: [
    QuestionTextWithInfoComponent,
  ],
  exports: [
    QuestionTextWithInfoComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    QuestionAdditionalInfoModule,
  ],
})
export class QuestionTextWithInfoModule { }
