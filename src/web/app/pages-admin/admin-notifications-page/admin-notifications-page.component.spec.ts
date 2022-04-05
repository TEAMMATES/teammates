import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import moment from 'moment-timezone';
import { of, throwError } from 'rxjs';
import SpyInstance = jest.SpyInstance;
import { NotificationService } from '../../../services/notification.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { TimezoneService } from '../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { Notification, NotificationStyle, NotificationTargetUser } from '../../../types/api-output';
import { SortBy } from '../../../types/sort-properties';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';
import { AdminNotificationsPageComponent } from './admin-notifications-page.component';
import { AdminNotificationsPageModule } from './admin-notifications-page.module';
import { NotificationEditFormModel } from './notification-edit-form/notification-edit-form-model';
import { NotificationsTableRowModel } from './notifications-table/notifications-table-model';

const exampleNotificationEditModel: NotificationEditFormModel = {
  notificationId: 'notification1',
  shown: false,

  startTime: { hour: 0, minute: 0 },
  startDate: { year: 0, month: 0, day: 0 },
  endTime: { hour: 0, minute: 0 },
  endDate: { year: 0, month: 0, day: 0 },

  style: NotificationStyle.SUCCESS,
  targetUser: NotificationTargetUser.INSTRUCTOR,

  title: 'valid title',
  message: 'valid message',

  isSaving: false,
  isDeleting: false,
};

const exampleNotificationOne: Notification = {
  notificationId: 'notification1',
  startTimestamp: moment('2017-09-15 09:30:00').valueOf(),
  endTimestamp: moment('2050-09-15 09:30:00').valueOf(),
  createdAt: moment('2017-09-15 09:30:00').valueOf(),
  updatedAt: moment('2017-09-15 09:30:00').valueOf(),
  style: NotificationStyle.SUCCESS,
  targetUser: NotificationTargetUser.INSTRUCTOR,
  title: 'valid title 1',
  message: 'valid message 1',
  shown: false,
};

const exampleNotificationTwo: Notification = {
  notificationId: 'notification2',
  startTimestamp: moment('2018-12-15 09:30:00').valueOf(),
  endTimestamp: moment('2050-09-15 09:30:00').valueOf(),
  createdAt: moment('2018-11-15 09:30:00').valueOf(),
  updatedAt: moment('2018-11-15 09:30:00').valueOf(),
  style: NotificationStyle.DANGER,
  targetUser: NotificationTargetUser.GENERAL,
  title: 'valid title 2',
  message: 'valid message 2',
  shown: false,
};

const notificationTableRowModel1: NotificationsTableRowModel = {
  isHighlighted: true,
  notification: exampleNotificationOne,
};

const notificationTableRowModel2: NotificationsTableRowModel = {
  isHighlighted: false,
  notification: exampleNotificationTwo,
};

describe('AdminNotificationsPageComponent', () => {
  let component: AdminNotificationsPageComponent;
  let fixture: ComponentFixture<AdminNotificationsPageComponent>;
  let notificationService: NotificationService;
  let statusMessageService: StatusMessageService;
  let simpleModalService: SimpleModalService;

  /**
   * Verifies the row model is updated as expected.
   */
  function verifyFirstRowModelEqualToExample(expected: Notification): void {
    expect(component.notificationsTableRowModels[0].notification.notificationId).toEqual(expected.notificationId);
    expect(component.notificationsTableRowModels[0].notification.shown).toBeFalsy();
    expect(component.notificationsTableRowModels[0].notification.targetUser).toEqual(expected.targetUser);
    expect(component.notificationsTableRowModels[0].notification.title).toEqual(expected.title);
    expect(component.notificationsTableRowModels[0].notification.message).toEqual(expected.message);
  }

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
      notifications: [exampleNotificationOne],
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
      notifications: [exampleNotificationOne],
    }));

    component.loadNotifications();

    expect(component.notificationsTableRowModels.length).toEqual(1);
    verifyFirstRowModelEqualToExample(exampleNotificationOne);
  });

  it('should add notification for all fields filled in', () => {
    jest.spyOn(notificationService, 'createNotification').mockReturnValue(of(exampleNotificationOne));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showSuccessToast')
    .mockImplementation((args: string) => {
      expect(args).toEqual('Successfully created');
    });

    component.addNewNotificationHandler();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.isNotificationEditFormExpanded).toBeFalsy();
    expect(component.notificationsTableRowModels.length).toEqual(1);
    verifyFirstRowModelEqualToExample(exampleNotificationOne);
  });

  it('should display error message when failed to create notification', () => {
    component.notificationEditFormModel = exampleNotificationEditModel;
    jest.spyOn(notificationService, 'createNotification').mockReturnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));
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
    jest.spyOn(notificationService, 'getNotifications').mockReturnValue(throwError({
      error: {
        message: 'This is the error message.',
      },
    }));
    const spy: SpyInstance = jest.spyOn(statusMessageService, 'showErrorToast')
      .mockImplementation((args: string) => {
        expect(args).toEqual('This is the error message.');
      });

    component.loadNotifications();

    expect(spy).toBeCalled();
    expect(component.hasNotificationLoadingFailed).toBeTruthy();
  });

  it('should display warning when attempts to edit another notification when form is open', async () => {
    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));
    jest.spyOn(notificationService, 'getNotifications').mockReturnValue(of({
      notifications: [exampleNotificationOne, exampleNotificationTwo],
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
    expect(component.notificationsTableRowModels[0].notification.message).toEqual(expectedMessage);
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

    component.deleteNotificationHandler(exampleNotificationEditModel.notificationId);
    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.notificationsTableRowModels.length).toEqual(0);
  });

  it('should sort notification by start time', () => {
    component.notificationsTableRowModels = [notificationTableRowModel1, notificationTableRowModel2];
    component.notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;
    component.sortNotificationsTableRowModelsHandler(SortBy.NOTIFICATION_START_TIME);
    expect(component.notificationsTableRowModelsSortBy).toEqual(SortBy.NOTIFICATION_START_TIME);

    const expected : NotificationsTableRowModel[] = [notificationTableRowModel1, notificationTableRowModel2]
        .sort(component.getNotificationsTableRowModelsComparator());
    expect(expected[0]).toBe(component.notificationsTableRowModels[0]);
    expect(expected[1]).toBe(component.notificationsTableRowModels[1]);
  });
});
