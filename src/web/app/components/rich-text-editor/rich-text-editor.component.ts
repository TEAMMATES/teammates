import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

/**
 * A rich text editor.
 */
@Component({
  selector: 'tm-rich-text-editor',
  templateUrl: './rich-text-editor.component.html',
  styleUrls: ['./rich-text-editor.component.scss'],
})
export class RichTextEditorComponent implements OnInit {

  @Input()
  isDisabled: boolean = false;

  @Input()
  isInlineMode: boolean = true;

  @Input()
  minHeightInPx: number = 120;

  @Input()
  placeholderText: string = '';

  @Input()
  richText: string = '';

  @Output()
  richTextChange: EventEmitter<string> = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }
}
