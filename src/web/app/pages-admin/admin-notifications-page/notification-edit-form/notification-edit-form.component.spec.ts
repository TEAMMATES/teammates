import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import moment from 'moment-timezone';
import { NotificationEditFormModel } from './notification-edit-form-model';
import { NotificationEditFormComponent } from './notification-edit-form.component';
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';
import { NotificationStyle, NotificationTargetUser } from '../../../../types/api-output';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';

const testNotificationEditModel: NotificationEditFormModel = {
  notificationId: 'notification1',

  startTimestamp: 0,
  endTimestamp: 0,

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
      providers: [provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();

    fixture = TestBed.createComponent(NotificationEditFormComponent);
    component = fixture.componentInstance;
    timezoneService = TestBed.inject(TimezoneService);
    simpleModalService = TestBed.inject(SimpleModalService);
    vi.spyOn(timezoneService, 'guessTimezone').mockReturnValue('Asia/Singapore');
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
    const shownModel = { ...testNotificationEditModel };
    shownModel.startTimestamp = Date.UTC(2000, 0, 1, 0, 0);
    component.model = shownModel;
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should set up with testNotificationEditModel', () => {
    component.model = testNotificationEditModel;
    const model: NotificationEditFormModel = component.model;
    expect(model).toBe(testNotificationEditModel);
  });

  it('should triggerModelChange with testNotificationEditModel', () => {
    component.model = testNotificationEditModel;
    component.modelChange.subscribe((data: NotificationEditFormModel) => {
      component.model = data;
    });
    const testStr = 'Hello World';
    component.triggerModelChange('title', testStr);
    component.triggerModelChange('message', testStr);
    component.triggerModelChange('targetUser', NotificationTargetUser.GENERAL);
    component.triggerModelChange('endTimestamp', Date.UTC(2024, 0, 1, 0, 0));

    const model: NotificationEditFormModel = component.model;
    expect(model.title).toBe(testStr);
    expect(model.message).toBe(testStr);
    expect(model.targetUser).toBe(NotificationTargetUser.GENERAL);
    expect(model.endTimestamp).toBe(Date.UTC(2024, 0, 1, 0, 0));
  });

  it('should display warning when discarding edit to current notification', async () => {
    component.model = testNotificationEditModel;
    component.modelChange.subscribe((data: NotificationEditFormModel) => {
      component.model = data;
    });
    const promise: Promise<void> = Promise.resolve();
    const modalSpy = vi
      .spyOn(simpleModalService, 'openConfirmationModal')
      .mockReturnValue(createMockNgbModalRef({}, promise));
    component.cancelHandler();
    await promise;
    expect(modalSpy).toHaveBeenCalledTimes(1);
    expect(modalSpy).toHaveBeenLastCalledWith(
      'Discard unsaved edit?',
      SimpleModalType.WARNING,
      'Warning: Any unsaved changes will be lost.',
    );
  });
});
