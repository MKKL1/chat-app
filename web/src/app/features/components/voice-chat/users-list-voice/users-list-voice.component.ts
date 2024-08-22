import {Component, signal} from '@angular/core';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {MatListModule} from "@angular/material/list";
import {disableDebugTools} from "@angular/platform-browser";
import {RouterLink} from "@angular/router";
import {UserBasicInfoComponent} from "../../../../core/components/user-basic-info/user-basic-info.component";

@Component({
  selector: 'app-users-list-voice',
  standalone: true,
  imports: [
    MatAccordion,
    MatChip,
    MatChipSet,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    UserBasicInfoComponent,
    MatListModule,
    RouterLink
  ],
  templateUrl: './users-list-voice.component.html',
  styleUrl: './users-list-voice.component.scss'
})
export class UsersListVoiceComponent {
  readonly panelOpenState = signal(false);

  channels: any[] = [
    {
      name: "Channel 1",
      users: [
        {username: "User 1"},
        {username: "User 2"},
        {username: "User 3"}
      ]
    },
    {name: "Channel 2"},
    {name: "Channel 3"}
  ];

}
