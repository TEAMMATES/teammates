import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { StudentNotificationsPageComponent } from './student-notifications-page.component';
import { UserNotificationsListModule } from '../../components/user-notifications-list/user-notifications-list.module';

describe('StudentNotificationsPageComponent', () => {
  let component: StudentNotificationsPageComponent;
  let fixture: ComponentFixture<StudentNotificationsPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [StudentNotificationsPageComponent],
      imports: [
        RouterModule.forRoot([]),
        UserNotificationsListModule,
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentNotificationsPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    component.timezone = 'UTC';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
