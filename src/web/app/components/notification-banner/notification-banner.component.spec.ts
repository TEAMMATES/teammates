import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { NotificationBannerComponent } from './notification-banner.component';
import { NotificationService } from '../../../services/notification.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Notification, NotificationStyle, NotificationTargetUser } from '../../../types/api-output';
import { MarkNotificationAsReadRequest } from '../../../types/api-request';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';

describe('NotificationBannerComponent', () => {
  let component: NotificationBannerComponent;
  let statusMessageService: StatusMessageService;
  let notificationService: NotificationService;
  let fixture: ComponentFixture<NotificationBannerComponent>;

  const testNotificationOne: Notification = {
    notificationId: 'notification1',
    startTimestamp: new Date('2017-09-15T09:30+00:00').getTime(),
    endTimestamp: new Date('2050-09-15T09:30+00:00').getTime(),
    createdAt: new Date('2017-09-15T09:30+00:00').getTime(),
    style: NotificationStyle.SUCCESS,
    targetUser: NotificationTargetUser.GENERAL,
    title: 'valid title 1',
    message: 'valid message 1',
    shown: false,
  };

  const testNotificationTwo: Notification = {
    notificationId: 'notification2',
    startTimestamp: new Date('2018-12-15T09:30+00:00').getTime(),
    endTimestamp: new Date('2050-11-15T09:30+00:00').getTime(),
    createdAt: new Date('2018-11-15T09:30+00:00').getTime(),
    style: NotificationStyle.DANGER,
    targetUser: NotificationTargetUser.GENERAL,
    title: 'valid title 2',
    message: 'valid message 2',
    shown: false,
  };

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [NotificationBannerComponent],
      imports: [
        BrowserAnimationsModule,
        CommonModule,
        TeammatesCommonModule,
        HttpClientTestingModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationBannerComponent);
    notificationService = TestBed.inject(NotificationService);
    statusMessageService = TestBed.inject(StatusMessageService);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.notificationTargetUser = NotificationTargetUser.STUDENT;
    component.isShown = true;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load data correctly', () => {
    const spy = jest.spyOn(notificationService, 'getUnreadNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne, testNotificationTwo],
    }));
    component.ngOnInit();
    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.notifications).toEqual([testNotificationOne, testNotificationTwo]);
  });

  it('should close after clicking X', () => {
    component.notifications = [testNotificationOne];
    fixture.detectChanges();

    expect(component.isShown).toBeTruthy();
    const button = fixture.debugElement.query(By.css('#btn-close-notif')).nativeElement;
    button.click();
    expect(component.isShown).toBeFalsy();
  });

  it('should close after clicking mark as read', () => {
    const apiSpy: SpyInstance = jest.spyOn(notificationService, 'markNotificationAsRead')
      .mockImplementation((request: MarkNotificationAsReadRequest) => {
        expect(request.notificationId).toEqual(testNotificationOne.notificationId);
        return of({
          readNotifications: [request.notificationId],
        });
      });
    const messageSpy: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Notification marked as read.');
      });
    component.notifications = [testNotificationOne];
    fixture.detectChanges();

    expect(component.isShown).toBeTruthy();
    const button = fixture.debugElement.query(By.css('#btn-mark-as-read')).nativeElement;
    button.click();
    expect(apiSpy).toHaveBeenCalledTimes(1);
    expect(messageSpy).toHaveBeenCalledTimes(1);
    expect(component.isShown).toBeFalsy();
  });

  it('should snap with no unread notifications', () => {
    component.notifications = [];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with 1 unread notification', () => {
    component.notifications = [testNotificationOne];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with multiple unread notifications', () => {
    component.notifications = [testNotificationOne, testNotificationTwo];
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
