import {Component, Input} from '@angular/core';
import {AvatarComponent} from "../../../shared/ui/avatar/avatar.component";
import {User} from "../../../features/models/user";

@Component({
  selector: 'app-user-basic-info',
  standalone: true,
  imports: [
    AvatarComponent
  ],
  templateUrl: './user-basic-info.component.html',
  styleUrl: './user-basic-info.component.scss'
})
export class UserBasicInfoComponent {
  @Input() user: User;
}
