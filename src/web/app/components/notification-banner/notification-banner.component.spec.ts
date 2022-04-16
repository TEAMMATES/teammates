import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import moment from 'moment-timezone';
import { of } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { NotificationService } from '../../../services/notification.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { Notification, NotificationStyle, NotificationTargetUser } from '../../../types/api-output';
import { MarkNotificationAsReadRequest } from '../../../types/api-request';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';

import { NotificationBannerComponent } from './notification-banner.component';

describe('NotificationBannerComponent', () => {
  let component: NotificationBannerComponent;
  let statusMessageService: StatusMessageService;
  let notificationService: NotificationService;
  let fixture: ComponentFixture<NotificationBannerComponent>;

  const testNotificationOne: Notification = {
    notificationId: 'notification1',
    startTimestamp: moment('2017-09-15 09:30:00').valueOf(),
    endTimestamp: moment('2050-09-15 09:30:00').valueOf(),
    createdAt: moment('2017-09-15 09:30:00').valueOf(),
    style: NotificationStyle.SUCCESS,
    targetUser: NotificationTargetUser.GENERAL,
    title: 'valid title 1',
    message: 'valid message 1',
    shown: false,
  };

  const testNotificationTwo: Notification = {
    notificationId: 'notification2',
    startTimestamp: moment('2018-12-15 09:30:00').valueOf(),
    endTimestamp: moment('2050-11-15 09:30:00').valueOf(),
    createdAt: moment('2018-11-15 09:30:00').valueOf(),
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
    component.notificationTargetUser = NotificationTargetUser.STUDENT;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close after clicking X', () => {
    jest.spyOn(notificationService, 'getUnreadNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne],
    }));
    component.ngOnInit();
    fixture.detectChanges();

    expect(component.isShown).toBeTruthy();
    const button = fixture.debugElement.query(By.css('#btn-close-notif')).nativeElement;
    button.click();
    expect(component.isShown).toBeFalsy();
  });

  it('should close after clicking mark as read', () => {
    jest.spyOn(notificationService, 'getUnreadNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne],
    }));
    jest.spyOn(notificationService, 'markNotificationAsRead').mockReturnValue(of({
      readNotifications: [testNotificationOne.notificationId],
    }));
    const spy1: SpyInstance = jest.spyOn(notificationService, 'markNotificationAsRead')
    .mockImplementation((request: MarkNotificationAsReadRequest) => {
      expect(request.notificationId).toEqual(testNotificationOne.notificationId);
      return of({
        readNotifications: [request.notificationId],
      });
    });
    const spy2: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
    .mockImplementation((args: string) => {
      expect(args).toEqual('Notification marked as read.');
    });
    component.ngOnInit();
    fixture.detectChanges();

    expect(component.isShown).toBeTruthy();
    const button = fixture.debugElement.query(By.css('#btn-mark-as-read')).nativeElement;
    button.click();
    expect(spy1).toBeCalledTimes(1);
    expect(spy2).toBeCalledTimes(1);
    expect(component.isShown).toBeFalsy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no unread notifications', () => {
    jest.spyOn(notificationService, 'getUnreadNotificationsForTargetUser').mockReturnValue(of({
      notifications: [],
    }));
    component.ngOnInit();
    expect(component.notifications.length).toEqual(0);
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with 1 unread notification', () => {
    jest.spyOn(notificationService, 'getUnreadNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne],
    }));
    component.ngOnInit();
    expect(component.notifications.length).toEqual(1);
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with multiple unread notifications', () => {
    jest.spyOn(notificationService, 'getUnreadNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne, testNotificationTwo],
    }));
    component.ngOnInit();
    expect(component.notifications.length).toEqual(2);
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
