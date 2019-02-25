import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GenderFormatPipe } from './student-profile-gender.pipe';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { TeammatesCommonModule } from '../../components/teammates-common/teammates-common.module';
import { StudentProfilePageComponent } from './student-profile-page.component';

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
});
