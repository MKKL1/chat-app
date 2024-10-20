import {Component, Input} from '@angular/core';
import {Community} from "../../../models/community";

@Component({
  selector: 'app-community-card',
  standalone: true,
  imports: [],
  templateUrl: './community-card.component.html',
  styleUrl: './community-card.component.scss'
})
export class CommunityCardComponent {
  @Input() community: Community;
}
