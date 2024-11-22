import {Component, Inject, OnDestroy, OnInit, signal} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatOption} from "@angular/material/core";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatSelect} from "@angular/material/select";
import {PaginatorModule} from "primeng/paginator";
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {RoleService} from "../../../../services/role.service";
import {Role} from "../../../../models/role";
import {RoleQuery} from "../../../../store/role/role.query";
import {MatListModule} from "@angular/material/list";
import {SelectButtonModule} from "primeng/selectbutton";
import {Permission} from "../../../../models/permission";
import {Channel} from "../../../../models/channel";
import {TextChannelQuery} from "../../../../store/textChannel/text.channel.query";
import {VoiceChannelQuery} from "../../../../store/voiceChannel/voice.channel.query";
import {formatRoleName} from "../../../../../shared/utils/utils";
import {clearBit, isBitSet, setBit} from "../../../../../shared/utils/binaryOperations";
import {ChannelService} from "../../../../services/channel.service";
import {MessageService} from "primeng/api";
import {Subscription} from "rxjs";

export interface RoleValue {
  label: string;
  bit: number;
}

@Component({
  selector: 'app-edit-permissions',
  standalone: true,
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    MatInput,
    MatLabel,
    MatOption,
    MatProgressSpinner,
    MatSelect,
    PaginatorModule,
    ReactiveFormsModule,
    MatListModule,
    SelectButtonModule
  ],
  templateUrl: './edit-permissions.component.html',
  styleUrl: './edit-permissions.component.scss'
})
export class EditPermissionsComponent implements OnInit, OnDestroy{
  channel = signal<Channel | null>(null);
  textChannelSub: Subscription;

  roles = signal<Role[]>([]);
  selectedRole = signal<Role | null>(null);
  permissions: Permission;

  channelPermissions: string[] = []

  roleForm: FormGroup;

  roleOptions: any[] = [
    {icon: 'pi pi-check', value: 'allow'},
    {icon: 'pi pi-minus', value: 'none'},
    {icon: 'pi pi-times', value: 'denied'}
  ];

  constructor(private roleService: RoleService,
              private channelService: ChannelService,
              private roleQuery: RoleQuery,
              private textChannelQuery: TextChannelQuery,
              private voiceChannelQuery: VoiceChannelQuery,
              private messageService: MessageService,
              @Inject(MAT_DIALOG_DATA) public data: {
                communityId: string,
                channelId: string
              },
              private fb: FormBuilder) {
    this.roleForm = this.fb.group({});
  }

  ngOnInit() {
    this.roles.set(
      this.roleQuery.getByCommunityId(this.data.communityId)
    );
    this.channel.set(this.getChannel(this.data.channelId));

    console.log(this.roles());
    console.log(this.channel());

    this.textChannelSub = this.textChannelQuery
      .selectEntity(this.channel()?.id!)
      .subscribe(channel => {
        console.log(channel);
        //this.channel.set(channel!);
        //this.selectRole(this.selectedRole()!);
    });
  }

  // get overwrites from overwrites in channel
  selectRole(role: Role){
    this.selectedRole.set(role);
    this.permissions = new Permission(role.permissionOverwrites);
    this.roleForm = this.fb.group({});
    this.channelPermissions = [];

    let overwrites;
    if(this.channel()?.overwrites){
      const roleOverwrite = this.channel()!.overwrites.find(r => r.roleId === role.id);
      if(roleOverwrite){
        overwrites = roleOverwrite.overwrites;
      }
    }

    const permissionKeys = this.permissions.getPermissionsNames();

    // assign basic values to form
    if(overwrites === undefined){
      // bits that can be modified 4-7
      // get those bits and build form based on them
      for(let i=4n; i<8n; i++) {
        const startValue = {icon: 'pi pi-minus', value: 'none'};
        this.roleForm.addControl(permissionKeys[Number(i)], new FormControl(startValue));
        this.channelPermissions.push(permissionKeys[Number(i)]);
      }
    } else {
      console.log(overwrites);
      // assign values from overwrite to form
      let allowBitValue: boolean;
      let denyBitValue: boolean;
      let startValue: any;

      // This is no good
      for(let i=4n; i<8n; i++) {
        allowBitValue = isBitSet(this.permissions.rawValue, i);
        denyBitValue = isBitSet(this.permissions.rawValue, i + 32n);

        if(allowBitValue && !denyBitValue){
          startValue = {icon: 'pi pi-check', value: 'allow'};
        } else if(!allowBitValue && denyBitValue){
          startValue = {icon: 'pi pi-times', value: 'denied'};
        } else {
          startValue = {icon: 'pi pi-minus', value: 'none'};
        }

        this.roleForm.addControl(permissionKeys[Number(i)], new FormControl(startValue));
        this.channelPermissions.push(permissionKeys[Number(i)]);
      }
    }
  }

  onSubmit(){
    let permissionOverride = this.permissions.rawValue;

    // TODO wrap in function
    if(this.roleForm.valid){
      let currentBit = 4n;
      Object.keys(this.roleForm.controls).forEach(controlName => {
        const control = this.roleForm.get(controlName);
        const status = control?.value.value;

        console.log(status);

        if(status === 'allow'){
          permissionOverride = setBit(permissionOverride, currentBit);
        } else if(status === 'denied'){
          permissionOverride = setBit(permissionOverride, currentBit + 32n);
          permissionOverride = clearBit(permissionOverride, currentBit);
        } else {
          permissionOverride = clearBit(permissionOverride, currentBit);
          permissionOverride = clearBit(permissionOverride, currentBit + 32n);
        }

        currentBit++;
      });
    }

    console.log(permissionOverride.toString(2));

    this.channelService.updatePermissions(
      this.channel()?.id!,
      this.selectedRole()?.id!,
      permissionOverride
    ).subscribe(_ => {
      // overwrites doesn't change
      this.messageService.add({severity: 'info', summary: `Updated channel permissions`});
      console.log(_);
    });
  }

  getChannel(id: string): Channel{
    let textChannel = this.textChannelQuery.getEntity(id);
    if(textChannel === undefined){
      return this.voiceChannelQuery.getEntity(id)!;
    }
    return textChannel;
  }

  ngOnDestroy() {
    this.textChannelSub.unsubscribe();
  }

  protected readonly Object = Object;
  protected readonly formatRoleName = formatRoleName;
}
