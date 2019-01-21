import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component, Input } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { InstructorSearchPageComponent } from './instructor-search-page.component';

@Component({ selector: 'tm-student-list', template: '' })
class StudentListStubComponent {
  @Input() courseId: string = '';
  @Input() useGrayHeading: boolean = true;
  @Input() sections: Object[] = [];
  @Input() enableRemindButton: boolean = false;
}

describe('InstructorSearchPageComponent', () => {
  let component: InstructorSearchPageComponent;
  let fixture: ComponentFixture<InstructorSearchPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        InstructorSearchPageComponent,
        StudentListStubComponent,
      ],
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule,
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
