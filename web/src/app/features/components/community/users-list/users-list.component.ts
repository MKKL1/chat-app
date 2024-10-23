import {Component, OnInit, signal} from '@angular/core';
import {MatAccordion, MatExpansionModule} from '@angular/material/expansion';
import {MatList, MatListItem, MatNavList} from "@angular/material/list";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {UserBasicInfoComponent} from "../../../../core/components/user-basic-info/user-basic-info.component";
import {AvatarComponent} from "../../../../shared/ui/avatar/avatar.component";
import {MemberQuery} from "../../../store/member/member.query";
import {Member} from "../../../models/member";
import {MatCardModule} from '@angular/material/card';
import {RoleQuery} from "../../../store/role/role.query";


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
    MatNavList,
    MatList,
    MatListItem,
    MatCardModule
  ],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.scss'
})
export class UsersListComponent implements OnInit{

  members = signal<Member[]>([]);

  constructor(private memberQuery: MemberQuery, private roleQuery: RoleQuery) {
  }

  ngOnInit() {
    this.memberQuery.selectAll().subscribe(members => {
      console.log(members);
      this.members.set(members);
    });
  }

  getRoleName(id: string){
    return this.roleQuery.getEntity(id)?.name;
  }

}
