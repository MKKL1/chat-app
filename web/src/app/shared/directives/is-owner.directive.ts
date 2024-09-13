import {Directive, TemplateRef, ViewContainerRef} from "@angular/core";
import {UserService} from "../../features/services/user.service";
import {CommunityQuery} from "../../features/store/community/community.query";

// Is it performant?

@Directive({
  standalone: true,
  selector: '[appIsOwner]'
})
export class IsOwnerDirective {
  userId: string = 'userId';
  ownerId: string = 'ownerId';

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private userService: UserService,
    private communityQuery: CommunityQuery
  ) {
    this.communityQuery.ownerId$.subscribe(id => {
      this.ownerId = id;
      this.userId = this.userService.getUser().id;
      this.updateView();
    })
  }

  private updateView(): void {
    if (this.ownerId === this.userId) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }

}