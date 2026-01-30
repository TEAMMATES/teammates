import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import {
  IndividualExtensionDateModalComponent,
} from './individual-extension-date-modal/individual-extension-date-modal.component';
import {
  InstructorSessionIndividualExtensionPageComponent,
} from './instructor-session-individual-extension-page.component';







const routes: Routes = [
  {
    path: '',
    component: InstructorSessionIndividualExtensionPageComponent,
  },
];

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    InstructorSessionIndividualExtensionPageComponent,
    IndividualExtensionDateModalComponent,
],
  exports: [InstructorSessionIndividualExtensionPageComponent],
})
export class InstructorSessionIndividualExtensionPageModule {}
