import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarModule } from '@angular/material';
import { RouterTestingModule } from '@angular/router/testing';
import { JoinState } from '../../../types/api-output';
import { InstructorSearchPageComponent, SearchStudentsTable } from './instructor-search-page.component';

@Component({ selector: 'tm-instructor-search-bar', template: '' })
class InstructorSearchBarStubComponent {
  @Input() searchKey: string = '';
}
@Component({ selector: 'tm-student-result-table', template: '' })
class StudentResultTableStubComponent {
  @Input() studentTables: SearchStudentsTable[] = [];
}

describe('InstructorSearchPageComponent', () => {
  let component: InstructorSearchPageComponent;
  let fixture: ComponentFixture<InstructorSearchPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorSearchPageComponent,
        InstructorSearchBarStubComponent,
        StudentResultTableStubComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        MatSnackBarModule,
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
    component.searchKey = 'TEST';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with a student table', () => {
    component.studentTables = [
      {
        courseId: 'test.tes-demo',
        sections: [
          {
            sectionName: 'Tutorial Group 1',
            isAllowedToViewStudentInSection: true,
            isAllowedToModifyStudent: true,
            students: [
              {
                name: 'Alice Betsy',
                email: 'alice.b.tmms@gmail.tmt',
                status: JoinState.JOINED,
                team: 'Team 1',
              },
              {
                name: 'Benny Charles',
                email: 'benny.c.tmms@gmail.tmt',
                status: JoinState.JOINED,
                team: 'Team 1',
              },
              {
                name: 'Danny Engrid',
                email: 'danny.e.tmms@gmail.tmt',
                status: JoinState.JOINED,
                team: 'Team 1',
              },
              {
                name: 'Emma Farrell',
                email: 'emma.f.tmms@gmail.tmt',
                status: JoinState.JOINED,
                team: 'Team 1',
              },
            ],
          },
        ],
      },
    ];

    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
