import {Component, Inject, OnInit, signal} from '@angular/core';
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
import {clearBit, setBit} from "../../../../../shared/utils/binaryOperations";

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
export class EditPermissionsComponent implements OnInit{
  channel = signal<Channel | null>(null);

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
              private roleQuery: RoleQuery,
              private textChannelQuery: TextChannelQuery,
              private voiceChannelQuery: VoiceChannelQuery,
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
  }

  // TODO add patch for channel in api
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

    if(overwrites === undefined){
      // assign basic values to form

      // bits that can be modified 4-7
      // get those bits and build form based on them

      // on submit transform form into long
      // and send patch

      // what does permission mean here?
      const permissionKeys = this.permissions.getPermissionsNames();

      for(let i=4n; i<8n; i++) {
        const startValue = {icon: 'pi pi-minus', value: 'none'};
        this.roleForm.addControl(permissionKeys[Number(i)], new FormControl(startValue));
        this.channelPermissions.push(permissionKeys[Number(i)]);
      }
    } else {
      // assign values from overwrite to form
    }

    console.log(this.roleForm.value);
    console.log(this.roleForm.controls);
  }

  onSubmit(){
    let permissionOverride = 0n;

    // TODO wrap in function
    if(this.roleForm.valid){
      let currentBit = 0n;
      Object.keys(this.roleForm.controls).forEach(controlName => {
        const control = this.roleForm.get(controlName);
        //console.log(`Control Name: ${controlName}, Value: ${control?.value}, Status: ${control?.status}`);

        const status = control?.value.value;
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
    // TODO send permissionOverride to backend (need patch)
  }

  getChannel(id: string): Channel{
    let textChannel = this.textChannelQuery.getEntity(id);

    if(textChannel === undefined){
      return this.voiceChannelQuery.getEntity(id)!;
    }

    return textChannel;
  }

  protected readonly Object = Object;
  protected readonly formatRoleName = formatRoleName;
}
