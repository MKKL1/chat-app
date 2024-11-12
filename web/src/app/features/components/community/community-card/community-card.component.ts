import {Component, Input} from '@angular/core';
import {Community} from "../../../models/community";
import {ShorteningPipe} from "../../../../shared/pipes/ShorteningPipe";

@Component({
  selector: 'app-community-card',
  standalone: true,
  imports: [
    ShorteningPipe
  ],
  templateUrl: './community-card.component.html',
  styleUrl: './community-card.component.scss'
})
export class CommunityCardComponent {
  @Input() community: Community;
}
