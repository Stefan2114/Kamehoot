# 🎮 Kamehoot - Multiplayer Quiz Game

**Kamehoot** is a real-time, multiplayer quiz game inspired by Kahoot. Players join a game using a unique code, answer timed questions, and compete on a live leaderboard — all enhanced with emoji reactions and WebSocket-powered interactivity.

---

# 🚀 Features

- 🧑‍🏫 **Host-Only Controls**: The game creator (host) manages the game flow and cannot participate as a player.
- 👥 **Player Participation**: Players join using a unique game code and answer questions under a time limit.
- 🧠 **Timed Questions**: Points are awarded based on how quickly and accurately players answer.
- 📊 **Live Leaderboard**: Shown after each question, with real-time updates.
- 🎉 **Emoji Reactions**: Send fun emojis during and after questions via WebSockets.
- 🔒 **Secure & Dockerized**: Runs over HTTPS using self-signed certificates and can be deployed with a single command.

---

## 🛠️ Tech Stack

### 🔧 Backend
- Java 21 + Spring Boot
- WebSocket
- PostgreSQL
- JWT (RSA-based) authentication + 2FA
- Dockerized

### 🌐 Frontend
- React + TypeScript
- CSS Modules for styling
- WebSocket client for real-time communication

---

## ⚙️ Getting Started

Update IP addresses in the following files:

  .env (inside frontend/kamehoot-frontend/)
  Replace VITE_SERVER_ADDRESS with your backend IP

  application.settings (inside backend/kamehoot-backend/)
  Replace any hardcoded host IPs or settings as needed

Run with Docker Compose inside the root (Kamehoot)

```bash
docker compose up --build
```



# 🧪 Usage Flow

  ✅ Login / Register

  🤔 Create Questions

  🧠 Create a Quiz (as host)

  🕹️ Start a Game based on a quiz

  📲 Share Game Code with others

  🎮 Players Join using the code

  🚦 Host Starts Game

  ❓ Timed Questions Appear

  🏆 Leaderboard After Each Question

  🎉 Game Ends + Final Rankings


# 🔐 Security

  Passwords hashed using BCrypt

  JWT authentication using RSA public/private keys

  WebSocket is secured over WSS

  Two-Factor Authentication (2FA) via code
