import { Directive, HostListener, HostBinding, Output, EventEmitter } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { FileHandle } from './file-handle';

@Directive({
  selector: '[appDrag]'
})
export class DragDirective {

  @Output() files: EventEmitter<FileHandle> = new EventEmitter();

  @HostBinding("style.background") private background = "#eee"; 
  constructor(private sanitizer:DomSanitizer){

  }

  @HostListener("dragover", ["$event"])
  public onDragOver(evt: DragEvent){
      evt.preventDefault();
      evt.stopPropagation();
      this.background = "#999";
  }

  @HostListener("dragleave", ["$event"])
  public onDragLeave(evt: DragEvent){
      evt.preventDefault();
      evt.stopPropagation();
      this.background = "#eee";
  }

  @HostListener("drop", ["$event"])
  public onDrop(evt: DragEvent){
      evt.preventDefault();
      evt.stopPropagation();
      this.background = "#eee";

      const file:File = evt.dataTransfer!.files[0];
      const url = this.sanitizer.bypassSecurityTrustUrl(window.URL.createObjectURL(file));
      let fileHandle: FileHandle = {file, url}
      this.files.emit(fileHandle);
  }

}
