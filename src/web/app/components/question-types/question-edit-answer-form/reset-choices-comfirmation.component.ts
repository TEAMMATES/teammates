import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'tm-reset-choices-comfirmation',
  templateUrl: './reset-choices-comfirmation.component.html',
  styleUrls: ['./reset-choices-comfirmation.component.scss']
})
export class ResetChoicesComfirmationComponent {
  @Output()
  confirmReset: EventEmitter<boolean> = new EventEmitter<boolean>();
  
  onConfirm(): void {
    this.confirmReset.emit(true);
  }
  
  onCancel(): void {
    this.confirmReset.emit(false);
  }
}



