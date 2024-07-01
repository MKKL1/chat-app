import {Component, signal} from '@angular/core';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {UserBasicInfoComponent} from "../../../core/components/user-basic-info/user-basic-info.component";
import {MatListModule} from "@angular/material/list";

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
    MatListModule
  ],
  templateUrl: './users-list-voice.component.html',
  styleUrl: './users-list-voice.component.scss'
})
export class UsersListVoiceComponent {
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
