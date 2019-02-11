import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RouterTestingModule } from '@angular/router/testing';
import { InstructorHelpGettingStartedComponent } from './instructor-help-getting-started.component';

describe('InstructorHelpGettingStartedComponent', () => {
  let component: InstructorHelpGettingStartedComponent;
  let fixture: ComponentFixture<InstructorHelpGettingStartedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorHelpGettingStartedComponent],
      imports: [
        RouterTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorHelpGettingStartedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
