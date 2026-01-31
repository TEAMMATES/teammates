import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { registerAllModules } from 'handsontable/registry';
import { NgxPageScrollCoreModule } from 'ngx-page-scroll-core';
import { InstructorCourseEnrollPageComponent } from './instructor-course-enroll-page.component';
import { AjaxLoadingModule } from '../../components/ajax-loading/ajax-loading.module';
import { AjaxPreloadModule } from '../../components/ajax-preload/ajax-preload.module';
import { LoadingRetryModule } from '../../components/loading-retry/loading-retry.module';
import { LoadingSpinnerModule } from '../../components/loading-spinner/loading-spinner.module';
import { PanelChevronModule } from '../../components/panel-chevron/panel-chevron.module';
import { ProgressBarModule } from '../../components/progress-bar/progress-bar.module';
import { StatusMessageModule } from '../../components/status-message/status-message.module';

registerAllModules();

/**
 * This package is ESM only since v14.3, which causes issues when importing this module.
 * As a workaround, we mock the module here until we can update the testing framework to support ESM properly.
 */
 jest.mock('@handsontable/angular', () => ({
   HotTableRegisterer: class {
     instances: Record<string, any> = {};
     getInstance(id: string): any {
       if (!this.instances[id]) {
         this.instances[id] = {
           getData: (): string[][] => [['', '', '', '', '']],
           getColHeader: (): string[] => ['Section', 'Team', 'Name', 'Email', 'Comments'],
           addHook: (): void => {},
           setCellMeta: (): void => {},
           render: (): void => {},
           loadData: (): void => {},
           alter: (): void => {},
         };
       }
       return this.instances[id];
     }
   },
 }));

describe('InstructorCourseEnrollPageComponent', () => {
  let component: InstructorCourseEnrollPageComponent;
  let fixture: ComponentFixture<InstructorCourseEnrollPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorCourseEnrollPageComponent],
      imports: [
        // HotTableModule // see comment above about why we don't import this module
        RouterModule.forRoot([]),
        NgxPageScrollCoreModule,
        AjaxPreloadModule,
        AjaxLoadingModule,
        StatusMessageModule,
        ProgressBarModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        PanelChevronModule,
      ],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstructorCourseEnrollPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
