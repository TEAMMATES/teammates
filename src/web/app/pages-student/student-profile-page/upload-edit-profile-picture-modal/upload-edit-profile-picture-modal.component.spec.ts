import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { NgbActiveModal, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { ImageCropperModule } from 'ngx-image-cropper';
import { UploadEditProfilePictureModalComponent } from './upload-edit-profile-picture-modal.component';

describe('UploadEditProfilePictureModalComponent', () => {

  let component: UploadEditProfilePictureModalComponent;
  let fixture: ComponentFixture<UploadEditProfilePictureModalComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        UploadEditProfilePictureModalComponent,
      ],
      imports: [
        HttpClientTestingModule,
        ImageCropperModule,
        NgbTooltipModule,
      ],
      providers: [
        NgbActiveModal,
      ],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UploadEditProfilePictureModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
