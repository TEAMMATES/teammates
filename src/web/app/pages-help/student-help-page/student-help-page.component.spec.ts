import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { StudentHelpPageComponent } from './student-help-page.component';
import { RouterModule } from '@angular/router';

describe('StudentHelpPageComponent', () => {
  let component: StudentHelpPageComponent;
  let fixture: ComponentFixture<StudentHelpPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentHelpPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
