import {Component, Input} from '@angular/core';
import {AvatarComponent} from "../../../shared/ui/avatar/avatar.component";
import {User} from "../../../features/models/user";

@Component({
  selector: 'app-user-basic-info',
  standalone: true,
  imports: [
    AvatarComponent
  ],
  templateUrl: './user-basic-info.component.html'
})
export class UserBasicInfoComponent {
  @Input() user: User;
}
