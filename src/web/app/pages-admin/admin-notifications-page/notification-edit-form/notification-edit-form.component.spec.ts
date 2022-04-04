import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import moment from 'moment-timezone';
import SpyInstance = jest.SpyInstance;
import { SimpleModalService } from '../../../../services/simple-modal.service';
import { TimezoneService } from '../../../../services/timezone.service';
import { createMockNgbModalRef } from '../../../../test-helpers/mock-ngb-modal-ref';
import { NotificationTargetUser } from '../../../../types/api-output';
import { SimpleModalType } from '../../../components/simple-modal/simple-modal-type';
import { EXAMPLE_NOTIFICATION_EDIT_MODEL } from '../admin-notifications-page-data';
import { AdminNotificationsPageModule } from '../admin-notifications-page.module';
import { NotificationEditFormModel } from './notification-edit-form-model';
import { NotificationEditFormComponent } from './notification-edit-form.component';

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

  it('should set up with EXAMPLE_NOTIFICATION_EDIT_MODEL', () => {
    component.model = EXAMPLE_NOTIFICATION_EDIT_MODEL;
    const model: NotificationEditFormModel = component.model;
    expect(model).toBe(EXAMPLE_NOTIFICATION_EDIT_MODEL);
  });

  it('should triggerModelChange with EXAMPLE_NOTIFICATION_EDIT_MODEL', () => {
    component.model = EXAMPLE_NOTIFICATION_EDIT_MODEL;
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
    component.model = EXAMPLE_NOTIFICATION_EDIT_MODEL;
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
