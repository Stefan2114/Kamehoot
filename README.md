# Kamehoot - Multiplayer Quiz Game

**Kamehoot** is a real-time, multiplayer quiz game inspired by Kahoot. Players join a game using a unique code, answer timed questions, and compete on a live leaderboard — all enhanced with emoji reactions and WebSocket-powered interactivity.

---

# Features

- **Host-Only Controls**: The game creator (host) manages the game flow and cannot participate as a player.
- **Player Participation**: Players join using a unique game code and answer questions under a time limit.
- **Timed Questions**: Points are awarded based on how quickly and accurately players answer.
- **Live Leaderboard**: Shown after each question, with real-time updates.
- **Emoji Reactions**: Send fun emojis during and after questions via WebSockets.
- **Secure & Dockerized**: Runs over HTTPS using self-signed certificates and can be deployed with a single command.

---

## Tech Stack

### Backend
- Java 21 + Spring Boot
- WebSocket
- PostgreSQL
- JWT (RSA-based) authentication + 2FA
- Dockerized

### Frontend
- React + TypeScript
- CSS Modules for styling
- WebSocket client for real-time communication

---

## Getting Started

Update IP addresses in the following files:

  .env (inside frontend/kamehoot-frontend/)
  Replace VITE_SERVER_ADDRESS with your backend IP

  application.settings (inside backend/kamehoot-backend/)
  Replace any hardcoded host IPs or settings as needed

Run with Docker Compose inside the root (Kamehoot)

```bash
docker compose up --build
```



# Usage Flow

  Login / Register

  Create Questions

  Create a Quiz (as host)

  Start a Game based on a quiz

  Share Game Code with others

  Players Join using the code

  Host Starts Game

  Timed Questions Appear

  Leaderboard After Each Question

  Game Ends + Final Rankings


# Security

  Passwords hashed using BCrypt

  JWT authentication using RSA public/private keys

  WebSocket is secured over WSS

  Two-Factor Authentication (2FA) via code


#UI
![Screenshot From 2025-07-03 11-11-23](https://github.com/user-attachments/assets/e177c6e2-70c5-4b4e-829a-2685b84f02dd)

![Screenshot From 2025-07-03 11-11-55](https://github.com/user-attachments/assets/74cb545b-67aa-4fcc-8b42-0b9b62fa52ea)

![Screenshot From 2025-07-03 11-12-23](https://github.com/user-attachments/assets/91b20c31-01bc-4a73-9f57-ef48dbb8c5ac)

![Screenshot From 2025-07-03 11-15-58](https://github.com/user-attachments/assets/936ba1c5-8b20-44df-84eb-73ff1a87ccf3)

![Screenshot From 2025-07-03 11-14-31](https://github.com/user-attachments/assets/4d77b206-0521-4d15-93fc-ef58738eca7e)

![Screenshot From 2025-07-03 11-16-09](https://github.com/user-attachments/assets/87ebf897-8026-4fdb-9531-9c8bf1f21cc5)

![Screenshot From 2025-07-04 17-38-29](https://github.com/user-attachments/assets/52986537-099b-431d-a861-e0f5a6b76108)

![Screenshot From 2025-07-04 17-38-59](https://github.com/user-attachments/assets/9ee7b2a8-73c2-4e01-b37e-21465cd3c838)

![Screenshot From 2025-07-04 17-40-55](https://github.com/user-attachments/assets/6a2a89a6-8878-495c-9ccd-eda219e704fa)

