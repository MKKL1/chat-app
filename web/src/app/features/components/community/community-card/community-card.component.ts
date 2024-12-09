import {Component, Input} from '@angular/core';
import {Community} from "../../../models/community";
import {ShorteningPipe} from "../../../../shared/pipes/ShorteningPipe";
import {NgStyle} from "@angular/common";
import {getRandomColorFromName} from "../../../../shared/utils/utils";

@Component({
  selector: 'app-community-card',
  standalone: true,
  imports: [
    ShorteningPipe,
    NgStyle,
  ],
  templateUrl: './community-card.component.html',
  styleUrl: './community-card.component.scss'
})
export class CommunityCardComponent {
  @Input() community: Community;

  // displaying random color instead of community image
  // color is generated based on community name
  getRandomColor(): string {
    return getRandomColorFromName(this.community.name);
  }
}
