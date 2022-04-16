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
import { SortBy } from '../../../types/sort-properties';
import { LoadingRetryModule } from '../loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../panel-chevron/panel-chevron.module';
import { TeammatesCommonModule } from '../teammates-common/teammates-common.module';
import { UserNotificationsListComponent } from './user-notifications-list.component';

describe('UserNotificationsListComponent', () => {
  let component: UserNotificationsListComponent;
  let notificationService: NotificationService;
  let statusMessageService: StatusMessageService;
  let fixture: ComponentFixture<UserNotificationsListComponent>;

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
      declarations: [UserNotificationsListComponent],
      imports: [
        BrowserAnimationsModule,
        HttpClientTestingModule,
        PanelChevronModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        TeammatesCommonModule,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserNotificationsListComponent);
    notificationService = TestBed.inject(NotificationService);
    statusMessageService = TestBed.inject(StatusMessageService);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should mark notification as read when button is clicked', () => {
    jest.spyOn(notificationService, 'getAllNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne],
    }));
    jest.spyOn(notificationService, 'getReadNotifications').mockReturnValue(of({
      readNotifications: [],
    }));
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

    component.ngOnInit();
    fixture.detectChanges();
    expect(component.notificationTabs[0].hasTabExpanded).toBeTruthy();
    fixture.debugElement.query(By.css('#btn-mark-as-read')).nativeElement.click();
    expect(apiSpy).toHaveBeenCalledTimes(1);
    expect(messageSpy).toHaveBeenCalledTimes(1);
    // check that it is no longer expanded
    expect(component.notificationTabs[0].hasTabExpanded).toBeFalsy();
  });

  it('should collapse an expanded tab when the header is clicked', () => {
    jest.spyOn(notificationService, 'getAllNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne],
    }));
    component.ngOnInit();
    fixture.detectChanges();

    expect(component.notificationTabs[0].hasTabExpanded).toBeTruthy();
    fixture.debugElement.query(By.css('.card-header')).nativeElement.click();
    expect(component.notificationTabs[0].hasTabExpanded).toBeFalsy();
  });

  it('should snap with default fields when loading', () => {
    component.isLoadingNotifications = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it fails to load', () => {
    component.isLoadingNotifications = false;
    component.hasLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no notifications', () => {
    component.isLoadingNotifications = false;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it loads the provided notifications', () => {
    jest.spyOn(notificationService, 'getAllNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne, testNotificationTwo],
    }));
    component.loadNotifications();
    expect(component.notificationTabs.length).toEqual(2);
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when all loaded notifications are read', () => {
    jest.spyOn(notificationService, 'getAllNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne, testNotificationTwo],
    }));
    jest.spyOn(notificationService, 'getReadNotifications').mockReturnValue(of({
      readNotifications: [
        testNotificationOne.notificationId,
        testNotificationTwo.notificationId,
      ],
    }));
    component.loadNotifications();
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it sorts the notification by start time', () => {
    jest.spyOn(notificationService, 'getAllNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne, testNotificationTwo],
    }));
    component.loadNotifications();
    component.sortNotificationsBy(SortBy.NOTIFICATION_START_TIME);
    fixture.detectChanges();

    // Start time is descending
    expect(component.notificationTabs[0].notification.startTimestamp
        >= component.notificationTabs[1].notification.startTimestamp).toBeTruthy();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it sorts the notification by end time', () => {
    jest.spyOn(notificationService, 'getAllNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne, testNotificationTwo],
    }));
    component.loadNotifications();
    component.sortNotificationsBy(SortBy.NOTIFICATION_END_TIME);
    fixture.detectChanges();

    // End time is ascending
    expect(component.notificationTabs[0].notification.endTimestamp
        <= component.notificationTabs[1].notification.endTimestamp).toBeTruthy();
    expect(fixture).toMatchSnapshot();
  });
});
