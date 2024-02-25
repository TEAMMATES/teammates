import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import moment from 'moment-timezone';
import SpyInstance = jest.SpyInstance;
import { NotificationEditFormModel } from './notification-edit-form-model';
import { NotificationEditFormComponent } from './notification-edit-form.component';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';
import { NotificationStyle, NotificationTargetUser } from '../../../../types/api-output';
import { getDefaultDateFormat, getDefaultTimeFormat } from '../../../../types/datetime-const';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { AdminNotificationsPageModule } from '../admin-notifications-page.module';

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

describe('NotificationEditFormComponent', () => {
  let component: NotificationEditFormComponent;
  let fixture: ComponentFixture<NotificationEditFormComponent>;
  let timezoneService: TimezoneService;
  let simpleModalService: SimpleModalService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        AdminNotificationsPageModule,
        HttpClientTestingModule,
      ],
      providers: [
        TimezoneService,
        SimpleModalService,
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationEditFormComponent);
    component = fixture.componentInstance;
    timezoneService = TestBed.inject(TimezoneService);
    simpleModalService = TestBed.inject(SimpleModalService);
    jest.spyOn(timezoneService, 'guessTimezone').mockReturnValue('Asia/Singapore');
    moment.tz.setDefault('SGT');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default view', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with notification', () => {
    component.model = testNotificationEditModel;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with notification that has been shown to users', () => {
    testNotificationEditModel.shown = true;
    component.model = testNotificationEditModel;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should set up with testNotificationEditModel', () => {
    testNotificationEditModel.shown = false;
    component.model = testNotificationEditModel;
    const model: NotificationEditFormModel = component.model;
    expect(model).toBe(testNotificationEditModel);
  });

  it('should triggerModelChange with testNotificationEditModel', () => {
    component.model = testNotificationEditModel;
    component.modelChange.subscribe((data: NotificationEditFormModel) => {
      component.model = data;
    });
    const testStr: string = 'Hello World';
    component.triggerModelChange('title', testStr);
    component.triggerModelChange('message', testStr);
    component.triggerModelChange('targetUser', NotificationTargetUser.GENERAL);
    component.triggerModelChange('endDate', { year: 1, month: 0, day: 0 });

    const model: NotificationEditFormModel = component.model;
    expect(model.title).toBe(testStr);
    expect(model.message).toBe(testStr);
    expect(model.targetUser).toBe(NotificationTargetUser.GENERAL);
    expect(model.endDate.year).toBe(1);
  });

  it('should display warning when discarding edit to current notification', async () => {
    component.model = testNotificationEditModel;
    component.modelChange.subscribe((data: NotificationEditFormModel) => {
      component.model = data;
    });
    const promise: Promise<void> = Promise.resolve();
    const modalSpy: SpyInstance = jest.spyOn(simpleModalService, 'openConfirmationModal')
        .mockReturnValue(createMockNgbModalRef({}, promise));
    component.cancelHandler();
    await promise;
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith('Discard unsaved edit?',
        SimpleModalType.WARNING, 'Warning: Any unsaved changes will be lost.');
  });
});
