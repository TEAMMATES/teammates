import { DOCUMENT } from '@angular/common';
import { Injectable, inject } from '@angular/core';

/**
 * Triggers a file download using native browser APIs.
 */
@Injectable({
  providedIn: 'root',
})
export class FileSaveService {
  private document = inject<Document>(DOCUMENT);

  saveFile(blob: Blob, filename: string): void {
    const url: string = URL.createObjectURL(blob);
    const anchor: HTMLAnchorElement = this.document.createElement('a');
    anchor.href = url;
    anchor.download = filename;
    this.document.body.appendChild(anchor);
    anchor.click();
    anchor.remove();
    URL.revokeObjectURL(url);
  }
}
