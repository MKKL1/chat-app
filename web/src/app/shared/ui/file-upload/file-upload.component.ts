import {Component, EventEmitter, Output} from '@angular/core';
import {MatFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {ShorteningPipe} from "../../pipes/ShorteningPipe";

@Component({
  selector: 'app-file-upload',
  standalone: true,
  imports: [
    MatFabButton,
    MatIcon,
    ShorteningPipe
  ],
  templateUrl: './file-upload.component.html',
  styles: [
    '.file-input {display: none;}',
    '.upload-btn {margin-bottom: 1rem;}'
  ]
})
export class FileUploadComponent {
  @Output() fileSelected = new EventEmitter<File>();
  filename: string = '';
  file: File | undefined;

  onFileSelected(event: any) {
    event.stopPropagation();
    const file = event.target.files[0];
    if(file) {
      this.filename = file.name;
      this.fileSelected.emit(file);
    }
  }
}
