import {Component, Input} from '@angular/core';
import {Community} from "../../../models/community";
import {ShorteningPipe} from "../../../../shared/pipes/ShorteningPipe";
import {NgIf, NgStyle} from "@angular/common";

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
    const sanitizedName = this.community.name.toLowerCase().replace(/\s+/g, '');
    let hash = 0;
    for (let i = 0; i < sanitizedName.length; i++) {
      hash = sanitizedName.charCodeAt(i) + ((hash << 5) - hash);
    }

    return `#${((hash >> 16) & 0xFFFFFF).toString(16).padStart(6, '0')}`;
  }
}
