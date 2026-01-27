import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { InstructorNotificationsPageComponent } from './instructor-notifications-page.component';
import { UserNotificationsListModule } from '../../components/user-notifications-list/user-notifications-list.module';

describe('InstructorNotificationsPageComponent', () => {
  let component: InstructorNotificationsPageComponent;
  let fixture: ComponentFixture<InstructorNotificationsPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorNotificationsPageComponent],
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
    fixture = TestBed.createComponent(InstructorNotificationsPageComponent);
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
