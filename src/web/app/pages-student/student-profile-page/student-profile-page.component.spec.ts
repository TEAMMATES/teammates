import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { environment } from '../../../environments/environment.prod';
import { Gender, StudentProfile } from '../../../types/api-output';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { StudentProfilePageComponent } from './student-profile-page.component';

describe('StudentProfilePageComponent', () => {
  let component: StudentProfilePageComponent;
  let fixture: ComponentFixture<StudentProfilePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        StudentProfilePageComponent,
      ],
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        HttpClientTestingModule,
        TeammatesCommonModule,
        LoadingSpinnerModule,
        AjaxLoadingModule,
        LoadingRetryModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentProfilePageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a student field without information', () => {
    const studentDetails: StudentProfile = {
      name: '',
      shortName: '',
      email: '',
      institute: '',
      nationality: '',
      gender: Gender.MALE,
      moreInfo: '',
    };
    component.student = studentDetails;
    component.editForm = new FormGroup({
      studentshortname: new FormControl(''),
      studentprofileemail: new FormControl(''),
      studentprofileinstitute: new FormControl(''),
      studentnationality: new FormControl(''),
      existingNationality: new FormControl(''),
      studentgender: new FormControl(''),
      studentprofilemoreinfo: new FormControl(''),
    });
    component.isLoadingStudentProfile = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with values and a profile photo', () => {
    const studentDetails: StudentProfile = {
      name: 'Ayush',
      shortName: 'Ash',
      email: 'ayush@nus.com',
      institute: 'NUS',
      nationality: 'Indian',
      gender: Gender.MALE,
      moreInfo: 'I like to party',
    };
    component.student = studentDetails;
    component.profilePicLink = `${environment.backendUrl}/webapi/students/` +
        'profilePic?blob-key=$photo.jpg&time=1552509888215';
    component.nationalities = ['Derpistan', 'Blablaland'];
    // Note: we are not using the full list of countries as the purpose of the snapshot test is to only check whether
    // the page is being rendered correctly.
    component.editForm = new FormGroup({
      studentshortname: new FormControl('Ash'),
      studentprofileemail: new FormControl('ayush@nus.com'),
      studentprofileinstitute: new FormControl('NUS'),
      studentnationality: new FormControl('Indian'),
      existingNationality: new FormControl('Indian'),
      studentgender: new FormControl(Gender.MALE),
      studentprofilemoreinfo: new FormControl('I like to party'),
    });
    component.isLoadingStudentProfile = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when student profile is still loading', () => {
    component.isLoadingStudentProfile = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
