import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GenderFormatPipe } from './student-profile-gender.pipe';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { StudentProfilePageComponent } from './student-profile-page.component';
import {Gender} from "../../../types/gender";
import { FormGroup } from '@angular/forms';

describe('StudentProfilePageComponent', () => {
  let component: StudentProfilePageComponent;
  let fixture: ComponentFixture<StudentProfilePageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        StudentProfilePageComponent,
        GenderFormatPipe,
      ],
      imports: [
        RouterTestingModule,
        ReactiveFormsModule,
        HttpClientTestingModule,
        TeammatesCommonModule,
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
    const studentDetails: any = {
      studentProfile: {
        shortName: '',
        email: '',
        institute: '',
        nationality: '',
        gender: Gender,
        moreInfo: '',
        pictureKey: '',
      },
      name: '',
      requestId: '',
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
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a populated student field', () => {
    const studentDetails: any = {
      studentProfile: {
        shortName: 'Ash',
        email: 'ayush@nus.com',
        institute: 'NUS',
        nationality: 'Indian',
        gender: Gender.MALE,
        moreInfo: '',
        pictureKey: '',
      },
      name: 'Ayush',
      requestId: '12',
    };
    component.student = studentDetails;
    component.editForm = new FormGroup({
      studentshortname: new FormControl('Ash'),
      studentprofileemail: new FormControl('ayush@nus.com'),
      studentprofileinstitute: new FormControl('NUS'),
      studentnationality: new FormControl('Indian'),
      existingNationality: new FormControl('Indian'),
      studentgender: new FormControl(Gender.MALE),
      studentprofilemoreinfo: new FormControl(''),
    });
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
