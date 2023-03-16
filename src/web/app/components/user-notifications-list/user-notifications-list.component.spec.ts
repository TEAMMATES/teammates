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
import { NotificationTab, UserNotificationsListComponent } from './user-notifications-list.component';

describe('UserNotificationsListComponent', () => {
  let component: UserNotificationsListComponent;
  let notificationService: NotificationService;
  let statusMessageService: StatusMessageService;
  let fixture: ComponentFixture<UserNotificationsListComponent>;

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

  const timezone: string = 'UTC';
  const DATE_FORMAT: string = 'DD MMM YYYY';

  const getNotificationTabs = (notifications: Notification[], readNotifications: string[] = []): NotificationTab[] => {
    const notificationTabs: NotificationTab[] = [];
    notifications.forEach((notification) => {
      const notificationTab: NotificationTab = {
        notification,
        hasTabExpanded: !readNotifications.includes(notification.notificationId),
        isRead: readNotifications.includes(notification.notificationId),
        startDate: moment(notification.startTimestamp).tz(timezone).format(DATE_FORMAT),
        endDate: moment(notification.endTimestamp).tz(timezone).format(DATE_FORMAT),
      };
      notificationTabs.push(notificationTab);
    });
    return notificationTabs;
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
    component.timezone = timezone;
    component.isLoadingNotifications = false;
    component.hasLoadingFailed = false;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load notifications from APIs correctly', () => {
    // two notifications, only first one has been read
    const getNotificationSpy = jest.spyOn(notificationService, 'getAllNotificationsForTargetUser').mockReturnValue(of({
      notifications: [testNotificationOne, testNotificationTwo],
    }));
    const getReadNotificationSpy = jest.spyOn(notificationService, 'getReadNotifications').mockReturnValue(of({
      readNotifications: [
        testNotificationOne.notificationId,
      ],
    }));
    component.loadNotifications();
    expect(getNotificationSpy).toHaveBeenCalledTimes(1);
    expect(getReadNotificationSpy).toHaveBeenCalledTimes(1);
    expect(component.notificationTabs).toEqual(getNotificationTabs(
      [testNotificationOne, testNotificationTwo],
      [testNotificationOne.notificationId],
    ));
  });

  it('should mark notification as read when button is clicked', () => {
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

    component.notificationTabs = getNotificationTabs([testNotificationOne]);
    fixture.detectChanges();
    expect(component.notificationTabs[0].hasTabExpanded).toBeTruthy();
    fixture.debugElement.query(By.css('.btn-mark-as-read')).nativeElement.click();
    expect(apiSpy).toHaveBeenCalledTimes(1);
    expect(messageSpy).toHaveBeenCalledTimes(1);
    // check that it is no longer expanded
    expect(component.notificationTabs[0].hasTabExpanded).toBeFalsy();
  });

  it('should collapse an expanded tab when the header is clicked', () => {
    component.notificationTabs = getNotificationTabs([testNotificationOne]);
    fixture.detectChanges();

    expect(component.notificationTabs[0].hasTabExpanded).toBeTruthy();
    fixture.debugElement.query(By.css('.card-header')).nativeElement.click();
    expect(component.notificationTabs[0].hasTabExpanded).toBeFalsy();
  });

  it('should sort notifications in correct order', () => {
    component.notificationTabs = getNotificationTabs([testNotificationOne, testNotificationTwo]);
    fixture.detectChanges();

    // default order already checked above
    component.sortNotificationsBy(SortBy.NOTIFICATION_START_TIME);
    expect(component.notificationTabs[0].notification.startTimestamp
      >= component.notificationTabs[1].notification.startTimestamp).toBeTruthy();

    component.sortNotificationsBy(SortBy.NOTIFICATION_END_TIME);
    expect(component.notificationTabs[0].notification.endTimestamp
      <= component.notificationTabs[1].notification.endTimestamp).toBeTruthy();
  });

  it('should snap with default fields when loading', () => {
    component.isLoadingNotifications = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it fails to load', () => {
    component.hasLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with no notifications', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it loads the provided notifications', () => {
    component.notificationTabs = getNotificationTabs([testNotificationOne, testNotificationTwo]);
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when all loaded notifications are read', () => {
    component.notificationTabs = getNotificationTabs(
      [testNotificationOne, testNotificationTwo],
      [
        testNotificationOne.notificationId,
        testNotificationTwo.notificationId,
      ],
    );
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it sorts the notification by start time', () => {
    component.notificationTabs = getNotificationTabs([testNotificationTwo, testNotificationOne]);
    component.notificationsSortBy = SortBy.NOTIFICATION_START_TIME;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when it sorts the notification by end time', () => {
    component.notificationTabs = getNotificationTabs([testNotificationOne, testNotificationTwo]);
    component.notificationsSortBy = SortBy.NOTIFICATION_END_TIME;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });
});
