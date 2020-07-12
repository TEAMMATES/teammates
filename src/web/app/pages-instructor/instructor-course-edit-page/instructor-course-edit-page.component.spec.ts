import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import {
  CustomPrivilegeSettingPanelComponent,
} from './custom-privilege-setting-panel/custom-privilege-setting-panel.component';
import {
  DeleteInstructorConfirmModalComponent,
} from './delete-instructor-confirm-model/delete-instructor-confirm-modal.component';
import { InstructorCourseEditPageComponent } from './instructor-course-edit-page.component';
import {
  InstructorEditPanelComponent,
} from './instructor-edit-panel/instructor-edit-panel.component';
import { InstructorRoleDescriptionPipe } from './instructor-edit-panel/instructor-role-description.pipe';
import {
  ResendInvitationEmailModalComponent,
} from './resend-invitation-email-modal/resend-invitation-email-modal.component';
import { ViewRolePrivilegesModalComponent } from './view-role-privileges-modal/view-role-privileges-modal.component';

describe('InstructorCourseEditPageComponent', () => {
  let component: InstructorCourseEditPageComponent;
  let fixture: ComponentFixture<InstructorCourseEditPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorCourseEditPageComponent,
        InstructorEditPanelComponent,
        ViewRolePrivilegesModalComponent,
        InstructorRoleDescriptionPipe,
        DeleteInstructorConfirmModalComponent,
        ResendInvitationEmailModalComponent,
        CustomPrivilegeSettingPanelComponent,
      ],
      imports: [
        NgbModule,
        FormsModule,
        TeammatesCommonModule,
        RouterTestingModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseEditPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
