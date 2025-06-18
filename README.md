# Pac-Man Clone in Java 🟡👻

A classic **Pac-Man** clone built from scratch in Java using **Swing**. Navigate Pac-Man through a maze, avoid ghosts, eat pellets, and survive as long as you can!

---

## 🎮 Features

* ✅ Grid-based movement and map rendering
* ✅ Smart ghost AI using Manhattan distance
* ✅ Power pellets & frightened mode
* ✅ Score and lives system
* ✅ Wrap-around tunnels
* ✅ Start screen, pause, and Game Over UI

---

## 📷 Screenshots

> *(You can include screenshots here later by adding image links or using Markdown)*

---

## 🛠️ Tech Stack

* Java 17+
* Java Swing for GUI

---

## 🚀 How to Run

### 1. Clone this repository

```bash
git clone https://github.com/your-username/pacman-java.git
cd pacman-java
```

### 2. Compile the game

```bash
javac Main.java PacMan.java
```

### 3. Run the game

```bash
java Main
```

Make sure all image assets (`.png` files) are in the same directory or accessible via classpath.

---

## 🎮 Controls

| Key        | Action               |
| ---------- | -------------------- |
| Arrow Keys | Move Pac-Man         |
| ENTER      | Start / Restart Game |
| P          | Pause / Unpause      |

---

## 📁 Assets Used

* Custom or classic Pac-Man sprites:

    * `pacmanUp.png`, `pacmanDown.png`, `pacmanLeft.png`, `pacmanRight.png`
    * `blueGhost.png`, `redGhost.png`, `pinkGhost.png`, `orangeGhost.png`
    * `wall.png`

Place them in the same directory as your `.java` files or adjust the `getResource()` paths accordingly.

---

## ✨ Future Enhancements (Optional)

* Add sound effects (using `Clip` and `.wav` files)
* High score saving system
* Multiple levels or increasing difficulty

---

