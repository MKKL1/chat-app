import {Component, signal} from '@angular/core';
import {MatAccordion, MatExpansionModule} from '@angular/material/expansion';
import {AvatarComponent} from "../../../core/components/avatar/avatar.component";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {UserBasicInfoComponent} from "../../../core/components/user-basic-info/user-basic-info.component";
import {MatNavList} from "@angular/material/list";


@Component({
  selector: 'app-users-list',
  standalone: true,
  imports: [
    MatAccordion,
    MatExpansionModule,
    AvatarComponent,
    MatChipSet,
    MatChip,
    UserBasicInfoComponent,
    MatNavList
  ],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss'
})
export class UsersListComponent {
  readonly panelOpenState = signal(false);

  users: any[] = [
    {id: 1, name: "twoja stara", roles: ["Owner", "Administator"]},
    {id: 2, name: "twoj stary"},
    {id: 3, name: "twoje stare"}
  ];
}
