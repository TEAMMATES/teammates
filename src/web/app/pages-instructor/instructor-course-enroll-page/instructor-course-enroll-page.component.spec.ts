import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HotTableModule } from '@handsontable/angular';
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
import { StudentEnrollRequest } from '../../../types/api-request';

registerAllModules();

describe('InstructorCourseEnrollPageComponent', () => {
  let component: InstructorCourseEnrollPageComponent;
  let fixture: ComponentFixture<InstructorCourseEnrollPageComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [InstructorCourseEnrollPageComponent],
      imports: [
        HttpClientTestingModule,
        HotTableModule,
        RouterTestingModule,
        NgxPageScrollCoreModule,
        AjaxPreloadModule,
        AjaxLoadingModule,
        StatusMessageModule,
        ProgressBarModule,
        LoadingSpinnerModule,
        LoadingRetryModule,
        PanelChevronModule,
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

  describe('checkCompulsoryFields - MC/DC Coverage', () => {
    beforeEach(() => {
      component.invalidRowsIndex = new Set<number>();
      component.enrollErrorMessage = '';
    });

    // Helper method to test private method
    function callCheckCompulsoryFields(requests: Map<number, StudentEnrollRequest>): void {
      // Access private method through component instance
      (component as any).checkCompulsoryFields(requests);
    }


    // MC/DC Test 1: All conditions false - should be valid
    it('should accept student when all fields are filled and less than 100 students', () => {
      const requests = new Map<number, StudentEnrollRequest>();
      requests.set(0, {
        section: 'SectionA',
        team: 'Team1',
        name: 'John Smith',
        email: 'john@email.com',
        comments: 'Dedicated student'
      });

      callCheckCompulsoryFields(requests);

      expect(component.invalidRowsIndex.has(0)).toBe(false);
      expect(component.enrollErrorMessage).not.toContain('Found empty compulsory fields');
    });


    // MC/DC Test 2: Empty email - should be invalid (tests condition E)
    it('should reject student when email is empty', () => {
      const requests = new Map<number, StudentEnrollRequest>();
      requests.set(0, {
        section: 'SectionA',
        team: 'Team1',
        name: 'John Smith',
        email: '',
        comments: 'Dedicated student'
      });

      callCheckCompulsoryFields(requests);

      expect(component.invalidRowsIndex.has(0)).toBe(true);
      expect(component.enrollErrorMessage).toContain('Found empty compulsory fields');
    });


    // MC/DC Test 3: Empty name - should be invalid (tests condition D)
    it('should reject student when name is empty', () => {
      const requests = new Map<number, StudentEnrollRequest>();
      requests.set(0, {
        section: 'SectionA',
        team: 'Team1',
        name: '',
        email: 'john@email.com',
        comments: 'Dedicated student'
      });

      callCheckCompulsoryFields(requests);

      expect(component.invalidRowsIndex.has(0)).toBe(true);
      expect(component.enrollErrorMessage).toContain('Found empty compulsory fields');
    });


    // MC/DC Test 4: Empty team - should be invalid (tests condition C)
    it('should reject student when team is empty', () => {
      const requests = new Map<number, StudentEnrollRequest>();
      requests.set(0, {
        section: 'SectionA',
        team: '',
        name: 'John Smith',
        email: 'john@email.com',
        comments: 'Dedicated student'
      });

      callCheckCompulsoryFields(requests);

      expect(component.invalidRowsIndex.has(0)).toBe(true);
      expect(component.enrollErrorMessage).toContain('Found empty compulsory fields');
    });


    // MC/DC Test 5: 100+ students with sections filled - should be valid (tests condition A)
    it('should accept 100+ students when sections are filled', () => {
      const requests = new Map<number, StudentEnrollRequest>();
      
      for (let i = 0; i < 105; i++) {
        requests.set(i, {
          section: `Section${i}`,
          team: 'Team1',
          name: `Student ${i}`,
          email: `student${i}@email.com`,
          comments: ''
        });
      }

      callCheckCompulsoryFields(requests);

      expect(component.invalidRowsIndex.size).toBe(0);
      expect(component.enrollErrorMessage).not.toContain('Found empty compulsory fields');
    });
    // MC/DC Test 6: 100+ students with empty sections - should be invalid (tests conditions A & B)
    it('should reject 100+ students when sections are empty', () => {
      const requests = new Map<number, StudentEnrollRequest>();
      
      for (let i = 0; i < 105; i++) {
        requests.set(i, {
          section: '',
          team: 'Team1',
          name: `Student ${i}`,
          email: `student${i}@email.com`,
          comments: ''
        });
      }

      callCheckCompulsoryFields(requests);

      expect(component.invalidRowsIndex.size).toBe(105);
      expect(component.invalidRowsIndex.has(0)).toBe(true);
      expect(component.invalidRowsIndex.has(104)).toBe(true);
      expect(component.enrollErrorMessage).toContain('Found empty compulsory fields');
    });
  });
});