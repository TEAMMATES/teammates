import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { InstructorSessionNoResponsePanelComponent } from './instructor-session-no-response-panel.component';
import { NavigationService } from '../../../services/navigation.service';

describe('InstructorSessionNoResponsePanelComponent', () => {
  let component: InstructorSessionNoResponsePanelComponent;
  let fixture: ComponentFixture<InstructorSessionNoResponsePanelComponent>;
  let navigationService: NavigationService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(InstructorSessionNoResponsePanelComponent);
    component = fixture.componentInstance;
    navigationService = TestBed.inject(NavigationService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to the send reminders page', () => {
    const navigateSpy = vi.spyOn(navigationService, 'navigateByURL').mockResolvedValue(true);
    component.session.feedbackSessionId = 'session-id';

    component.openSendReminderPage(new Event('click'));

    expect(navigateSpy).toHaveBeenCalledWith('/web/instructor/sessions/session-id/send-reminders', {
      preselectNonSubmitters: 'true',
    });
  });
});
