/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */

// @ts-check
import type { SidebarsConfig } from "@docusaurus/plugin-content-docs";

const sidebars: SidebarsConfig = {
  tutorialSidebar: [
    {type: "doc", id: "intro"},
    {type: "doc", id: "install"},
    { 
      type: "category", 
      label: "Backend",
      items: [
        {
          type: "doc",
          id: "backend/events",
          label: "Events",
        },
        {
          type: "doc",
          id: "backend/rsocket",
          label: "RSocket",
        }
      ]
    },
    {
      type: "category",
      label: "Angular",
      items: [
        {
          type: "doc",
          id: "angular/store",
          label: "Store"
        }
      ]
    }
    // { type: "autogenerated", dirName: "developer" },
    // { type: "autogenerated", dirName: "tutorial-extras" },
  ],
  openApiSidebar: [
    {
      type: "category",
      label: "Basics",
      items: [
        {
          type: "doc",
          id: "api-additional/authentication",
          label: "Authentication",
        },
        {
          type: "doc",
          id: "api-additional/websocket",
          label: "WebSocket",
        },
        {
          type: "doc",
          id: "api-additional/permissions",
          label: "Permissions",
        }
      ],
    },
    {
      type: "category",
      label: "Endpoints",
      link: {
        type: "generated-index",
        title: "API",
        description:
          ".",
        slug: "/category/api"
      },
      items: require("./docs/api/sidebar.js")
    }
  ]
};

export default sidebars;