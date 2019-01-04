import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ResponseStatusPipe } from './sessionResponseStatus.pipe';
import { SubmissionStatusPipe } from './sessionSubmissionStatus.pipe';

@NgModule({
  declarations:[
    ResponseStatusPipe,
    SubmissionStatusPipe
  ],
  imports:[CommonModule],
  exports:[
    ResponseStatusPipe,
    SubmissionStatusPipe
  ]
})

export class Pipes {}
