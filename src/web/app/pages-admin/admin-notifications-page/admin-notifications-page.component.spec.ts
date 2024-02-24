import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { AdminNotificationsPageComponent } from './admin-notifications-page.component';
import { AdminNotificationsPageModule } from './admin-notifications-page.module';
import { NotificationEditFormModel } from './notification-edit-form/notification-edit-form-model';
import { NotificationsTableRowModel } from './notifications-table/notifications-table-model';
import { NotificationService } from '../../../services/notification.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { Notification, NotificationStyle, NotificationTargetUser } from '../../../types/api-output';
import { getDefaultDateFormat, getDefaultTimeFormat } from '../../../types/datetime-const';
import { SortBy } from '../../../types/sort-properties';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';

const testNotificationEditModel: NotificationEditFormModel = {
  notificationId: 'notification1',
  shown: false,

  startTime: getDefaultTimeFormat(),
  startDate: getDefaultDateFormat(),
  endTime: getDefaultTimeFormat(),
  endDate: getDefaultDateFormat(),

  style: NotificationStyle.SUCCESS,
  targetUser: NotificationTargetUser.INSTRUCTOR,

  title: 'valid title',
  message: 'valid message',

  isSaving: false,
  isDeleting: false,
};

const testNotificationOne: Notification = {
  notificationId: 'notification1',
  startTimestamp: new Date('2017-09-15T09:30+00:00').getTime(),
  endTimestamp: new Date('2050-09-15T09:30+00:00').getTime(),
  createdAt: new Date('2017-09-15T09:30+00:00').getTime(),
  style: NotificationStyle.SUCCESS,
  targetUser: NotificationTargetUser.INSTRUCTOR,
  title: 'valid title 1',
  message: 'valid message 1',
  shown: false,
};

const testNotificationTwo: Notification = {
  notificationId: 'notification2',
  startTimestamp: new Date('2018-12-15T09:30+00:00').getTime(),
  endTimestamp: new Date('2050-09-15T09:30+00:00').getTime(),
  createdAt: new Date('2018-11-15T09:30+00:00').getTime(),
  style: NotificationStyle.DANGER,
  targetUser: NotificationTargetUser.GENERAL,
  title: 'valid title 2',
  message: 'valid message 2',
  shown: false,
};

const notificationTableRowModel1: NotificationsTableRowModel = {
  isHighlighted: true,
  notification: testNotificationOne,
};

const notificationTableRowModel2: NotificationsTableRowModel = {
  isHighlighted: false,
  notification: testNotificationTwo,
};

describe('AdminNotificationsPageComponent', () => {
  let component: AdminNotificationsPageComponent;
  let fixture: ComponentFixture<AdminNotificationsPageComponent>;
  let notificationService: NotificationService;
  let statusMessageService: StatusMessageService;
  let simpleModalService: SimpleModalService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AdminNotificationsPageModule,
        HttpClientTestingModule,
        BrowserAnimationsModule,
      ],
      providers: [
        TimezoneService,
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AdminNotificationsPageComponent);
    notificationService = TestBed.inject(NotificationService);
    statusMessageService = TestBed.inject(StatusMessageService);
    simpleModalService = TestBed.inject(SimpleModalService);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.guessTimezone = 'UTC';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when notifications are loading', () => {
    component.isNotificationLoading = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when notifications failed to load', () => {
    component.hasNotificationLoadingFailed = true;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap when notification edit form expanded for adding notification', () => {
    jest.spyOn(notificationService, 'getNotifications').mockReturnValue(of());
    component.initNotificationEditFormModel();
    component.loadNotifications();
    fixture.detectChanges();

    const btn: any = fixture.debugElement.nativeElement.querySelector('#btn-add-notification');
    expect(btn.disabled).toBeFalsy();
    expect(btn.textContent).toEqual(' Add New Notification');
    btn.click();
    fixture.detectChanges();

    expect(component.isNotificationLoading).toBeFalsy();
    expect(component.isNotificationEditFormExpanded).toBeTruthy();
    expect(fixture).toMatchSnapshot();
  });

  it('should disable edit button when notification edit form expanded for existent notification', () => {
    jest.spyOn(notificationService, 'getNotifications').mockReturnValue(of({
      notifications: [testNotificationOne],
    }));
    component.initNotificationEditFormModel();
    component.loadNotifications();
    component.loadNotificationEditForm(component.notificationsTableRowModels[0].notification);
    fixture.detectChanges();

    const btn: any = fixture.debugElement.nativeElement.querySelector('#btn-add-notification');
    expect(btn.disabled).toBeTruthy();
    expect(btn.textContent).toEqual(' Edit Existing Notification');
  });

  it('should load correct notification for a given API output', () => {
    jest.spyOn(notificationService, 'getNotifications').mockReturnValue(of({
      notifications: [testNotificationOne],
    }));

    component.loadNotifications();

    expect(component.notificationsTableRowModels.length).toEqual(1);
    expect(component.notificationsTableRowModels[0].notification.notificationId)
      .toEqual(testNotificationOne.notificationId);
    expect(component.notificationsTableRowModels[0].notification.shown).toBeFalsy();
    expect(component.notificationsTableRowModels[0].notification.targetUser).toEqual(testNotificationOne.targetUser);
    expect(component.notificationsTableRowModels[0].notification.style).toEqual(testNotificationOne.style);
    expect(component.notificationsTableRowModels[0].notification.title).toEqual(testNotificationOne.title);
    expect(component.notificationsTableRowModels[0].notification.message).toEqual(testNotificationOne.message);
    expect(component.notificationsTableRowModels[0].notification.startTimestamp)
      .toEqual(testNotificationOne.startTimestamp);
    expect(component.notificationsTableRowModels[0].notification.endTimestamp)
      .toEqual(testNotificationOne.endTimestamp);
  });

  it('should add notification for all fields filled in', () => {
    jest.spyOn(notificationService, 'createNotification').mockReturnValue(of(testNotificationOne));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Successfully created');
      });

    component.addNewNotificationHandler();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.isNotificationEditFormExpanded).toBeFalsy();
    expect(component.notificationsTableRowModels.length).toEqual(1);
    expect(component.notificationsTableRowModels[0].notification.notificationId)
      .toEqual(testNotificationOne.notificationId);
    expect(component.notificationsTableRowModels[0].notification.shown).toBeFalsy();
    expect(component.notificationsTableRowModels[0].notification.style).toEqual(testNotificationOne.style);
    expect(component.notificationsTableRowModels[0].notification.targetUser).toEqual(testNotificationOne.targetUser);
    expect(component.notificationsTableRowModels[0].notification.title).toEqual(testNotificationOne.title);
    expect(component.notificationsTableRowModels[0].notification.message).toEqual(testNotificationOne.message);
    expect(component.notificationsTableRowModels[0].notification.startTimestamp)
      .toEqual(testNotificationOne.startTimestamp);
    expect(component.notificationsTableRowModels[0].notification.endTimestamp)
      .toEqual(testNotificationOne.endTimestamp);
  });

  it('should display error message when failed to create notification', () => {
    component.notificationEditFormModel = testNotificationEditModel;
    jest.spyOn(notificationService, 'createNotification').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    component.addNewNotificationHandler();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.notificationsTableRowModels.length).toEqual(0);
  });

  it('should display error message when notification failed to load', () => {
    component.hasNotificationLoadingFailed = false;
    jest.spyOn(notificationService, 'getNotifications').mockReturnValue(throwError(() => ({
      error: {
        message: 'This is the error message.',
      },
    })));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    component.loadNotifications();

    expect(spy).toHaveBeenCalled();
    expect(component.hasNotificationLoadingFailed).toBeTruthy();
  });

  it('should display warning when attempts to edit another notification when form is open', async () => {
    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
      .mockReturnValue(createMockNgbModalRef({}, promise));
    jest.spyOn(notificationService, 'getNotifications').mockReturnValue(of({
      notifications: [testNotificationOne, testNotificationTwo],
    }));
    component.initNotificationEditFormModel();
    component.loadNotifications();
    component.loadNotificationEditForm(component.notificationsTableRowModels[0].notification);
    fixture.detectChanges();
    expect(component.isNotificationEditFormExpanded).toBeTruthy();

    const expectedMessage: string = component.notificationsTableRowModels[0].notification.message;

    component.loadNotificationEditFormHandler(component.notificationsTableRowModels[0].notification);
    component.notificationEditFormModel.message = 'new message';
    expect(component.notificationEditFormModel.message).toEqual('new message');

    // attempts to load edit form for another notification
    component.loadNotificationEditFormHandler(component.notificationsTableRowModels[1].notification);
    await promise;
    // the first loaded notification message should not be updated to new message
    expect(component.notificationsTableRowModels[0].notification.message).toEqual(expectedMessage);
    // notification edit form model should be updated to the newly loaded notification
    expect(component.notificationEditFormModel.message)
        .toEqual(component.notificationsTableRowModels[1].notification.message);

    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith('Discard unsaved edit?',
        SimpleModalType.WARNING,
        'Warning: If you choose to edit another notification, any unsaved changes will be lost.');
  });

  it('should delete notfication', () => {
    component.notificationsTableRowModels = [notificationTableRowModel1];
    expect(component.notificationsTableRowModels.length).toEqual(1);
    jest.spyOn(notificationService, 'deleteNotification').mockReturnValue(of({
      message: 'Successfully deleted',
    }));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('Successfully deleted');
      });

    component.deleteNotificationHandler(testNotificationEditModel.notificationId);
    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.notificationsTableRowModels.length).toEqual(0);
  });

  it('should sort notification by start time', () => {
    component.notificationsTableRowModels = [notificationTableRowModel1, notificationTableRowModel2];
    component.notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;
    component.sortNotificationsTableRowModelsHandler(SortBy.NOTIFICATION_START_TIME);
    expect(component.notificationsTableRowModelsSortBy).toEqual(SortBy.NOTIFICATION_START_TIME);

    const expected : NotificationsTableRowModel[] = [notificationTableRowModel2, notificationTableRowModel1]
        .sort(component.getNotificationsTableRowModelsComparator());
    expect(expected[0]).toBe(component.notificationsTableRowModels[0]);
    expect(expected[1]).toBe(component.notificationsTableRowModels[1]);
  });
});
