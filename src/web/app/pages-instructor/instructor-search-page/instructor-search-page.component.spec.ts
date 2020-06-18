import { HttpClientTestingModule } from '@angular/common/http/testing';

import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { JoinState } from '../../../types/api-output';
import { InstructorSearchPageComponent } from './instructor-search-page.component';
import { InstructorSearchPageModule } from './instructor-search-page.module';

describe('InstructorSearchPageComponent', () => {
  let component: InstructorSearchPageComponent;
  let fixture: ComponentFixture<InstructorSearchPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        MatSnackBarModule,
        InstructorSearchPageModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorSearchPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a search key', () => {
    component.searchParams.searchKey = 'TEST';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a student table', () => {
    component.studentsListRowTables = [
      {
        courseId: 'test-exa.demo',
        students: [
          {
            name: 'tester',
            team: 'Team 1',
            email: 'tester@tester.com',
            status: JoinState.JOINED,
            sectionName: 'Tutorial Group 1',
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
          {
            name: 'Benny Charles',
            team: 'Team 1',
            email: 'benny.c.tmms@gmail.tmt',
            status: JoinState.JOINED,
            sectionName: 'Tutorial Group 1',
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
          {
            name: 'Alice Betsy',
            team: 'Team 1',
            email: 'alice.b.tmms@gmail.tmt',
            status: JoinState.JOINED,
            sectionName: 'Tutorial Group 1',
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
          {
            name: 'Danny Engrid',
            team: 'Team 1',
            email: 'danny.e.tmms@gmail.tmt',
            status: JoinState.JOINED,
            sectionName: 'Tutorial Group 1',
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
        ],
      }];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
