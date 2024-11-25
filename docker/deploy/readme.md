This example uses traefik as reverse proxy and a self generated certificate.

# Prerequisites 
## Certificates
Generate certificate for local deployment
```
mkcert -cert-file certs/local-cert.crt -key-file certs/local-key.key "szampchat.com" "*.szampchat.com"
```

## Livekit server

Run livekit locally, can't get it to work properly with docker. Make sure it uses config file from this directory (`chat-app/docker/deploy/livekit.yml`)
```
./livekit-server.exe --config livekit.yaml
```

## Building server
You have to manually build server with maven:
```
mvn -B package
```
And rename it's artifact to `szampchat.jar` (TODO)

`mv chatapp/server/target/szampchat-server-0.0.1-SNAPSHOT.jar chatapp/server/target/szampchat.jar`

This step would not be necessary in future. Preferably server image should be available at docker hub

## Building frontend
Make sure your environment.ts has proper paths:

```ts
export const environment = {
  production: false, //true?
  giphyKey: 'GO6Q2NWnawI61Zdkb11Rv60cijsXPHEw',
  domain: 'https://szampchat.com',
  api: 'https://szampchat.com/api/',
  keycloackUrl: 'https://auth.szampchat.com',
  websocketUrl: 'wss://events.szampchat.com',
  livekitUrl: 'ws://localhost:7880',
  snowflakeEpoch: 1714867200000
}
```

Then run `ng build` in `chat-app/web` directory

## Dns
As we don't own szampchat.com domain, we have to route requests going to szampchat.com to localhost

modify `Windows\Systems32\drivers\etc\hosts\` or `/etc/hosts` on linux (?)

```
# Add at the end of file
127.0.0.1 auth.szampchat.com
127.0.0.1 szampchat.com
127.0.0.1 livekit.szampchat.com
127.0.0.1 whoami.szampchat.com
127.0.0.1 events.szampchat.com
```

# Start
Run with `docker compose -f "docker-compose-deploy.yml" up -d --build`
Remember to build server and frontend before!