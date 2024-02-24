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
import { DatepickerModule } from '../../components/datepicker/datepicker.module';
import { ExtensionConfirmModalModule } from '../../components/extension-confirm-modal/extension-confirm-modal.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { TimepickerModule } from '../../components/timepicker/timepicker.module';

const routes: Routes = [
  {
    path: '',
    component: InstructorSessionIndividualExtensionPageComponent,
  },
];

@NgModule({
  imports: [
    CommonModule,
    TeammatesCommonModule,
    FormsModule,
    RouterModule.forChild(routes),
    LoadingSpinnerModule,
    DatepickerModule,
    TimepickerModule,
    LoadingRetryModule,
    ExtensionConfirmModalModule,
  ],
  declarations: [
    InstructorSessionIndividualExtensionPageComponent,
    IndividualExtensionDateModalComponent,
  ],
  exports: [InstructorSessionIndividualExtensionPageComponent],
})
export class InstructorSessionIndividualExtensionPageModule {}
