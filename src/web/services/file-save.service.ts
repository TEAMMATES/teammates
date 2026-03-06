import { DOCUMENT } from '@angular/common';
import { Inject, Injectable } from '@angular/core';

/**
 * Triggers a file download using native browser APIs.
 */
@Injectable({
  providedIn: 'root',
})
export class FileSaveService {

  constructor(@Inject(DOCUMENT) private document: Document) {}

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
