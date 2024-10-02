# Web

# How to run

Run `cd web` to go to angular app directory

Run `npm install` to download all dependencies

Run `ng serve` to run application on `http://localhost:4200`

# Environment

Before running app create file called environment.ts in the same directory where
environment-example.ts is created and copy it content to new environment file

# RSocket

All logic connected to websockets is placed in service in /app/src/core/services/rsocket.connection.ts
This service is called after creating MainComponent which can be found in /app/src/features/components/pages/main/main.component.ts
MainComponent is created after signing in to app with keycloak
To run websockets without singing in, copy and paste MainComponent body into AppComponent

I encountered issues with properly installing dependencies for rsocket. Run
`npm install -D @types/rsocket-core`
`npm install -D @types/rsocket-websocket-client`
to try fixing those issues

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 18.0.1.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice. To use this command, you need to first add a package that implements end-to-end testing capabilities.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
