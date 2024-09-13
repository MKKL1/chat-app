import {Component, Input} from '@angular/core';
import {MatButton} from "@angular/material/button";

@Component({
  selector: 'app-button-danger',
  standalone: true,
  imports: [
    MatButton
  ],
  template: '<button mat-flat-button style="background: darkred; color: white;">' +
    '<ng-content></ng-content>' +
    '</button>'
})
export class ButtonDangerComponent {
}
