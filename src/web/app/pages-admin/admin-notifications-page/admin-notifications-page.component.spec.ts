import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { AdminNotificationsPageComponent } from './admin-notifications-page.component';
import { NotificationEditFormModel } from './notification-edit-form/notification-edit-form-model';
import { NotificationsTableRowModel } from './notifications-table/notifications-table-model';
import { NotificationService } from '../../../services/notification.service';
import { SimpleModalService } from '../../../services/simple-modal.service';
import { StatusMessageService } from '../../../services/status-message.service';
import { createMockNgbModalRef } from '../../../test-helpers/mock-ngb-modal-ref';
import { Notification, NotificationStyle, NotificationTargetUser } from '../../../types/api-output';
import { getDefaultDateFormat, getDefaultTimeFormat } from '../../../types/datetime-const';
import { SortBy, SortOrder } from '../../../types/sort-properties';
import { SortableEvent } from '../../components/sortable-table/sortable-table.component';
import { SimpleModalType } from '../../components/simple-modal/simple-modal-type';

const testNotificationEditModel: NotificationEditFormModel = {
  notificationId: 'notification1',
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
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

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
    vi.spyOn(notificationService, 'getNotifications').mockReturnValue(of());
    component.initNotificationEditFormModel();
    component.loadNotifications();
    fixture.detectChanges();

    const btn = fixture.debugElement.nativeElement.querySelector('#btn-add-notification');
    expect(btn.disabled).toBeFalsy();
    expect(btn.textContent).toEqual(' Add New Notification');
    btn.click();
    fixture.detectChanges();

    expect(component.isNotificationLoading).toBeFalsy();
    expect(component.isNotificationEditFormExpanded).toBeTruthy();
    expect(fixture).toMatchSnapshot();
  });

  it('should disable edit button when notification edit form expanded for existent notification', () => {
    vi.spyOn(notificationService, 'getNotifications').mockReturnValue(
      of({
        notifications: [testNotificationOne],
      }),
    );
    component.initNotificationEditFormModel();
    component.loadNotifications();
    component.loadNotificationEditForm(component.notificationsTableRowModels[0].notification);
    fixture.detectChanges();

    const btn = fixture.debugElement.nativeElement.querySelector('#btn-add-notification');
    expect(btn.disabled).toBeTruthy();
    expect(btn.textContent.trim()).toEqual('Edit Existing Notification');
  });

  it('should load correct notification for a given API output', () => {
    const getNotificationsSpy = vi.spyOn(notificationService, 'getNotifications').mockReturnValue(
      of({
        notifications: [testNotificationOne],
      }),
    );

    component.loadNotifications();

    expect(getNotificationsSpy).toHaveBeenCalledWith({
      targetUsers: [NotificationTargetUser.STUDENT, NotificationTargetUser.INSTRUCTOR, NotificationTargetUser.GENERAL],
      isFetchingActive: false,
    });
    expect(component.notificationsTableRowModels.length).toEqual(1);
    expect(component.notificationsTableRowModels[0].notification.notificationId).toEqual(
      testNotificationOne.notificationId,
    );
    expect(component.notificationsTableRowModels[0].notification.targetUser).toEqual(testNotificationOne.targetUser);
    expect(component.notificationsTableRowModels[0].notification.style).toEqual(testNotificationOne.style);
    expect(component.notificationsTableRowModels[0].notification.title).toEqual(testNotificationOne.title);
    expect(component.notificationsTableRowModels[0].notification.message).toEqual(testNotificationOne.message);
    expect(component.notificationsTableRowModels[0].notification.startTimestamp).toEqual(
      testNotificationOne.startTimestamp,
    );
    expect(component.notificationsTableRowModels[0].notification.endTimestamp).toEqual(
      testNotificationOne.endTimestamp,
    );
  });

  it('should add notification for all fields filled in', () => {
    vi.spyOn(notificationService, 'createNotification').mockReturnValue(of(testNotificationOne));
    const spy = vi.spyOn(statusMessageService, 'showSuccessToast');
    const previousRowModels = component.notificationsTableRowModels;

    component.addNewNotificationHandler();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(spy).toHaveBeenCalledWith('Notification created successfully.');
    expect(component.isNotificationEditFormExpanded).toBeFalsy();
    expect(component.notificationsTableRowModels).not.toBe(previousRowModels);
    expect(component.notificationsTableRowModels.length).toEqual(1);
    expect(component.notificationsTableRowModels[0].notification.notificationId).toEqual(
      testNotificationOne.notificationId,
    );
    expect(component.notificationsTableRowModels[0].notification.style).toEqual(testNotificationOne.style);
    expect(component.notificationsTableRowModels[0].notification.targetUser).toEqual(testNotificationOne.targetUser);
    expect(component.notificationsTableRowModels[0].notification.title).toEqual(testNotificationOne.title);
    expect(component.notificationsTableRowModels[0].notification.message).toEqual(testNotificationOne.message);
    expect(component.notificationsTableRowModels[0].notification.startTimestamp).toEqual(
      testNotificationOne.startTimestamp,
    );
    expect(component.notificationsTableRowModels[0].notification.endTimestamp).toEqual(
      testNotificationOne.endTimestamp,
    );
  });

  it('should display error message when failed to create notification', () => {
    component.notificationEditFormModel = testNotificationEditModel;
    vi.spyOn(notificationService, 'createNotification').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message.',
        },
      })),
    );
    const spy = vi.spyOn(statusMessageService, 'showErrorToast').mockImplementation((args: string) => {
      expect(args).toEqual('This is the error message.');
    });

    component.addNewNotificationHandler();

    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.notificationsTableRowModels.length).toEqual(0);
  });

  it('should display error message when notification failed to load', () => {
    component.hasNotificationLoadingFailed = false;
    vi.spyOn(notificationService, 'getNotifications').mockReturnValue(
      throwError(() => ({
        error: {
          message: 'This is the error message.',
        },
      })),
    );
    const spy = vi.spyOn(statusMessageService, 'showErrorToast').mockImplementation((args: string) => {
      expect(args).toEqual('This is the error message.');
    });

    component.loadNotifications();

    expect(spy).toHaveBeenCalled();
    expect(component.hasNotificationLoadingFailed).toBeTruthy();
  });

  it('should update notification immutably', () => {
    const updatedNotification: Notification = {
      ...testNotificationOne,
      title: 'updated title',
      message: 'updated message',
    };
    component.notificationsTableRowModels = [notificationTableRowModel1];
    component.notificationEditFormModel = testNotificationEditModel;
    const previousRowModels = component.notificationsTableRowModels;
    vi.spyOn(notificationService, 'updateNotification').mockReturnValue(of(updatedNotification));
    const spy = vi.spyOn(statusMessageService, 'showSuccessToast');

    component.editExistingNotificationHandler();

    expect(spy).toHaveBeenCalledWith('Notification updated successfully.');
    expect(component.notificationsTableRowModels).not.toBe(previousRowModels);
    expect(component.notificationsTableRowModels[0].notification).toEqual(updatedNotification);
    expect(component.notificationsTableRowModels[0].isHighlighted).toBeTruthy();
  });

  it('should display warning when attempts to edit another notification when form is open', async () => {
    const promise: Promise<void> = Promise.resolve();
    const modalSpy = vi
      .spyOn(simpleModalService, 'openConfirmationModal')
      .mockReturnValue(createMockNgbModalRef({}, promise));
    vi.spyOn(notificationService, 'getNotifications').mockReturnValue(
      of({
        notifications: [testNotificationOne, testNotificationTwo],
      }),
    );
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
    expect(component.notificationEditFormModel.message).toEqual(
      component.notificationsTableRowModels[1].notification.message,
    );

    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(
      'Discard unsaved edit?',
      SimpleModalType.WARNING,
      'Warning: If you choose to edit another notification, any unsaved changes will be lost.',
    );
  });

  it('should delete notfication', () => {
    component.notificationsTableRowModels = [notificationTableRowModel1];
    const previousRowModels = component.notificationsTableRowModels;
    expect(component.notificationsTableRowModels.length).toEqual(1);
    vi.spyOn(notificationService, 'deleteNotification').mockReturnValue(
      of({
        message: 'Successfully deleted',
      }),
    );
    const spy = vi.spyOn(statusMessageService, 'showSuccessToast').mockImplementation((args: string) => {
      expect(args).toEqual('Successfully deleted');
    });

    component.deleteNotificationHandler(testNotificationEditModel.notificationId);
    expect(spy).toHaveBeenCalledTimes(1);
    expect(component.notificationsTableRowModels).not.toBe(previousRowModels);
    expect(component.notificationsTableRowModels.length).toEqual(0);
  });

  it('should sort notification by start time', () => {
    component.notificationsTableRowModels = [notificationTableRowModel1, notificationTableRowModel2];
    const previousRowModels = component.notificationsTableRowModels;
    component.notificationsTableRowModelsSortBy = SortBy.NOTIFICATION_CREATE_TIME;
    const sortEvent: SortableEvent = {
      sortBy: SortBy.NOTIFICATION_START_TIME,
      sortOrder: SortOrder.DESC,
    };
    component.sortNotificationsTableRowModelsHandler(sortEvent);
    expect(component.notificationsTableRowModelsSortBy).toEqual(SortBy.NOTIFICATION_START_TIME);
    expect(component.notificationsTableRowModelsSortOrder).toEqual(SortOrder.DESC);
    expect(component.notificationsTableRowModels).not.toBe(previousRowModels);
    expect(
      component.notificationsTableRowModels.every((rowModel: NotificationsTableRowModel) => !rowModel.isHighlighted),
    ).toBeTruthy();

    const expected: NotificationsTableRowModel[] = [notificationTableRowModel2, notificationTableRowModel1].sort(
      component.getNotificationsTableRowModelsComparator(),
    );
    expect(component.notificationsTableRowModels[0].notification.notificationId).toBe(
      expected[0].notification.notificationId,
    );
    expect(component.notificationsTableRowModels[1].notification.notificationId).toBe(
      expected[1].notification.notificationId,
    );
  });
});
