import {Component, OnInit, signal} from '@angular/core';
import {MatAccordion, MatExpansionModule} from '@angular/material/expansion';
import {MatNavList} from "@angular/material/list";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {UserBasicInfoComponent} from "../../../../core/components/user-basic-info/user-basic-info.component";
import {AvatarComponent} from "../../../../shared/ui/avatar/avatar.component";
import {MemberQuery} from "../../../store/member/member.query";


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
export class UsersListComponent implements OnInit{
  readonly panelOpenState = signal(false);

  user = '';

  constructor(private memberQuery: MemberQuery) {
  }

  ngOnInit() {
    this.memberQuery.selectAll().subscribe(member => {
      console.log(member);
    });
  }

  users: any[] = [
    {id: 1, name: "test1", roles: ["Owner", "Administator"]},
    {id: 2, name: "test2"},
    {id: 3, name: "test3"}
  ];
}
