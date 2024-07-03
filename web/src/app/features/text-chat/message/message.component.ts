import { Component } from '@angular/core';
import {AvatarComponent} from "../../../core/components/avatar/avatar.component";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatIconButton} from "@angular/material/button";
import {NgClass, NgStyle} from "@angular/common";

@Component({
  selector: 'app-message',
  standalone: true,
  imports: [
    AvatarComponent,
    MatIcon,
    MatMenuTrigger,
    MatIconButton,
    MatMenu,
    MatMenuItem,
    NgClass,
    NgStyle
  ],
  templateUrl: './message.component.html',
  styleUrl: './message.component.scss'
})
export class MessageComponent {

  fromClient: boolean = false;

  constructor() {
    // random value to visualize concept
    this.fromClient = Math.random() < 0.5;
  }

}
