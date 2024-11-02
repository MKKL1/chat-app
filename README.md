# Chat app
[Documentation](https://mkkl1.github.io/chat-app/)

## How to run
### Docker
```
docker compose up -d
```
### Livekit
**Temporary solution**

Remember to run livekit server.

Steps:

- Download release:
https://github.com/livekit/livekit/releases/tag/v1.8.0

- Place livekit-server.exe in here `.../chat-app/livekit-server.exe`

- Run:
```
cd chat-app
./livekit-server.exe --config livekit.yaml
```