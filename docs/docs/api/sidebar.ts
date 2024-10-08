import type { SidebarsConfig } from "@docusaurus/plugin-content-docs";

const sidebar: SidebarsConfig = {
  apisidebar: [
    {
      type: "doc",
      id: "api/szampchat-server-api",
    },
    {
      type: "category",
      label: "channel-controller",
      items: [
        {
          type: "doc",
          id: "api/edit-channel",
          label: "editChannel",
          className: "api-method put",
        },
        {
          type: "doc",
          id: "api/delete-channel",
          label: "deleteChannel",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/create-channel",
          label: "createChannel",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/get-channels-for-community",
          label: "getChannelsForCommunity",
          className: "api-method get",
        },
      ],
    },
    {
      type: "category",
      label: "user-controller",
      items: [
        {
          type: "doc",
          id: "api/create-user",
          label: "createUser",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/delete-user",
          label: "deleteUser",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/edit-description",
          label: "editDescription",
          className: "api-method patch",
        },
        {
          type: "doc",
          id: "api/edit-avatar",
          label: "editAvatar",
          className: "api-method patch",
        },
        {
          type: "doc",
          id: "api/get-user",
          label: "getUser",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/get-me",
          label: "getMe",
          className: "api-method get",
        },
      ],
    },
    {
      type: "category",
      label: "role-controller",
      items: [
        {
          type: "doc",
          id: "api/create-role",
          label: "createRole",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/get-role",
          label: "getRole",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/delete-role",
          label: "deleteRole",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/edit-role",
          label: "editRole",
          className: "api-method patch",
        },
        {
          type: "doc",
          id: "api/get-roles-for-community",
          label: "getRolesForCommunity",
          className: "api-method get",
        },
      ],
    },
    {
      type: "category",
      label: "Community",
      items: [
        {
          type: "doc",
          id: "api/get-user-communities",
          label: "getUserCommunities",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/create-community",
          label: "createCommunity",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/get-community",
          label: "Get detailed info about community",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/add-member",
          label: "addMember",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/delete-community",
          label: "deleteCommunity",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/edit-community",
          label: "editCommunity",
          className: "api-method patch",
        },
        {
          type: "doc",
          id: "api/join-community",
          label: "joinCommunity",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/invite-to-community",
          label: "inviteToCommunity",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/get-community-members",
          label: "getCommunityMembers",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/get-full-community-info",
          label: "getFullCommunityInfo",
          className: "api-method get",
        },
      ],
    },
    {
      type: "category",
      label: "message-controller",
      items: [
        {
          type: "doc",
          id: "api/get-messages",
          label: "Get messages for given channel",
          className: "api-method get",
        },
        {
          type: "doc",
          id: "api/create-message",
          label: "createMessage",
          className: "api-method post",
        },
        {
          type: "doc",
          id: "api/delete-message",
          label: "deleteMessage",
          className: "api-method delete",
        },
        {
          type: "doc",
          id: "api/edit-message",
          label: "editMessage",
          className: "api-method patch",
        },
      ],
    },
  ],
};

export default sidebar.apisidebar;
