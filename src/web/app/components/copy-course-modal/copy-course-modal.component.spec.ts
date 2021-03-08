import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { CopyCourseModalComponent } from './copy-course-modal.component';

describe('CopyCourseModalComponent', () => {
  let component: CopyCourseModalComponent;
  let fixture: ComponentFixture<CopyCourseModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CopyCourseModalComponent],
      imports: [
        HttpClientTestingModule,
        FormsModule,
      ],
      providers: [NgbActiveModal],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CopyCourseModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should snap with default fields', () => {
    expect(fixture).toMatchSnapshot();
  });

  it('should snap with some course id', () => {
    component.newCourseId = 'Test02';
    fixture.detectChanges();
    expect(fixture).toMatchSnapshot();
  });

  it('should enable copy button after new courseId is provided', () => {
    component.newCourseId = 'Test02';
    component.newCourseName = 'TestName02';
    fixture.detectChanges();
    const copyButton: any = fixture.debugElement.query(By.css('#btn-confirm-copy-course'));
    expect(copyButton.nativeElement.disabled).toBeFalsy();
  });

  it('should disable copy if courseId is empty', () => {
    component.newCourseId = '';
    component.newCourseName = 'TestName02';
    fixture.detectChanges();
    const copyButton: any = fixture.debugElement.query(By.css('#btn-confirm-copy-course'));
    expect(copyButton.nativeElement.disabled).toBeTruthy();
  });

});
