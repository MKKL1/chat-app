import {Component, Input} from '@angular/core';
import {AvatarComponent} from "../avatar/avatar.component";

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
  @Input() username: string = "Username";
  image: string | null = null;
}
