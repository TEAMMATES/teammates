import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchStudentFaqComponent } from './search-student-faq.component';

describe('SearchStudentFaqComponent', () => {
  let component: SearchStudentFaqComponent;
  let fixture: ComponentFixture<SearchStudentFaqComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SearchStudentFaqComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SearchStudentFaqComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
