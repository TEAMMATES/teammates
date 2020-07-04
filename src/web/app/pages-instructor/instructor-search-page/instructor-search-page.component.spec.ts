import { HttpClientTestingModule } from '@angular/common/http/testing';

import { async, ComponentFixture, TestBed } from '@angular/core/testing';
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
            student: {
              name: 'tester',
              teamName: 'Team 1',
              email: 'tester@tester.com',
              joinState: JoinState.JOINED,
              sectionName: 'Tutorial Group 1',
              courseId: 'test-exa.demo',
            },
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
          {
            student: {
              name: 'Benny Charles',
              teamName: 'Team 1',
              email: 'benny.c.tmms@gmail.tmt',
              joinState: JoinState.JOINED,
              sectionName: 'Tutorial Group 1',
              courseId: 'test-exa.demo',
            },
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
          {
            student: {
              name: 'Alice Betsy',
              teamName: 'Team 1',
              email: 'alice.b.tmms@gmail.tmt',
              joinState: JoinState.JOINED,
              sectionName: 'Tutorial Group 1',
              courseId: 'test-exa.demo',
            },
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
          {
            student: {
              name: 'Danny Engrid',
              teamName: 'Team 1',
              email: 'danny.e.tmms@gmail.tmt',
              joinState: JoinState.JOINED,
              sectionName: 'Tutorial Group 1',
              courseId: 'test-exa.demo',
            },
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
          },
        ],
      }];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
