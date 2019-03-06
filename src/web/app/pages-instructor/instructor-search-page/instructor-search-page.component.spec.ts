import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
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
});
