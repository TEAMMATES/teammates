import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { Gender, StudentProfile } from '../../../types/api-output';
import { CommentsToCommentTableModelPipe } from '../../components/comment-box/comments-to-comment-table-model.pipe';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import {
  GrqRgqViewResponsesModule,
} from '../../components/question-responses/grq-rgq-view-responses/grq-rgq-view-responses.module';
import { InstructorStudentRecordsPageComponent } from './instructor-student-records-page.component';

@Component({ selector: 'tm-student-profile', template: '' })
class StudentProfileStubComponent {
  @Input() studentProfile: StudentProfile | undefined;
  @Input() studentName: string = '';
  @Input() photoUrl: string = '/assets/images/profile_picture_default.png';
  @Input() hideMoreInfo: boolean = false;
}
@Component({ selector: 'tm-more-info', template: '' })
class MoreInfoStubComponent {
  @Input() studentName: string = '';
  @Input() moreInfoText: string = '';
}

describe('InstructorStudentRecordsPageComponent', () => {
  let component: InstructorStudentRecordsPageComponent;
  let fixture: ComponentFixture<InstructorStudentRecordsPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorStudentRecordsPageComponent,
        StudentProfileStubComponent,
        MoreInfoStubComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        NgbModule,
        GrqRgqViewResponsesModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        PanelChevronModule,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            queryParams: of({ courseid: 'su1337', studentemail: 'punk@punk.com' }),
          },
        },
        CommentsToCommentTableModelPipe,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorStudentRecordsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with populated fields', () => {
    const studentProfile: StudentProfile = {
      name: 'John Doe',
      shortName: 'JD',
      email: 'jd@jd.com',
      institute: 'Area51',
      nationality: 'Antarctican',
      gender: Gender.OTHER,
      moreInfo: '',
    };

    component.studentName = 'Not John Doe';
    component.studentProfile = studentProfile;
    component.courseId = 'su1337';
    component.isStudentLoading = false;
    component.isStudentProfileLoading = false;
    component.isStudentResultsLoading = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when student results are still loading', () => {
    const studentProfile: StudentProfile = {
      name: 'John Doe',
      shortName: 'JD',
      email: 'jd@jd.com',
      institute: 'Area51',
      nationality: 'Antarctican',
      gender: Gender.OTHER,
      moreInfo: '',
    };
    component.studentName = 'John Doe';
    component.studentProfile = studentProfile;
    component.courseId = 'CS1111';
    component.isStudentResultsLoading = true;
    component.isStudentLoading = false;
    component.isStudentProfileLoading = false;

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should output a correctly formatted url string', () => {
    expect(component.photoUrl)
      .toEqual('http://localhost:8080/webapi/student/profilePic?courseid=su1337&studentemail=punk@punk.com');
  });
});
