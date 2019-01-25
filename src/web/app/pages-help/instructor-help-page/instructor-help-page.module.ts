import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { InstructorHelpPageComponent } from './instructor-help-page.component';
import { InstructorHelpStudentsSectionComponent } from './instructor-help-students-section/instructor-help-students-section.component';
import { InstructorHelpCoursesSectionComponent } from './instructor-help-courses-section/instructor-help-courses-section.component';
import { ViewProfileFaqComponent } from './instructor-help-students-section/view-profile-faq/view-profile-faq.component';
import { EditStudentDetailsFaqComponent } from './instructor-help-students-section/edit-student-details-faq/edit-student-details-faq.component';
import { ViewStudentResponsesFaqComponent } from './instructor-help-students-section/view-student-responses-faq/view-student-responses-faq.component';
import { FormsModule } from '@angular/forms';
import { SearchStudentFaqComponent } from './instructor-help-students-section/search-student-faq/search-student-faq.component';
import { EmailStudentFaqComponent } from './instructor-help-students-section/email-student-faq/email-student-faq.component';
import { StudentUseGaccountFaqComponent } from './instructor-help-students-section/student-use-gaccount-faq/student-use-gaccount-faq.component';
import { StudentChangeGaccountFaqComponent } from './instructor-help-students-section/student-change-gaccount-faq/student-change-gaccount-faq.component';

/**
 * Module for instructor help page.
 */
@NgModule({
  imports: [
    CommonModule,
    NgbModule,
    FormsModule
  ],
  declarations: [
    InstructorHelpPageComponent,
    InstructorHelpStudentsSectionComponent,
    InstructorHelpCoursesSectionComponent,
    ViewProfileFaqComponent,
    EditStudentDetailsFaqComponent,
    ViewStudentResponsesFaqComponent,
    SearchStudentFaqComponent,
    EmailStudentFaqComponent,
    StudentUseGaccountFaqComponent,
    StudentChangeGaccountFaqComponent
  ],
  exports: [
    InstructorHelpPageComponent,
  ],
})
export class InstructorHelpPageModule {}
