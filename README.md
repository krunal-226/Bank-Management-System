# 🏦 Smart ATM / Bank Management System

🌟 Key 3D Features
💳 Interactive 3D Holographic Card: Responds to mouse movements with real-time 3D tilt (pitch & yaw). Click anywhere on the card or the flip button to perform a 180° rotation revealing the CVV (842), magnetic stripe, and signature strip.
🔐 3D Mechanical Vault Door: On authentication, the combination dial spins, 6 heavy steel radial locking bolts retract, and the vault door swings open in 3D perspective to reveal gold bullion bars inside.
🪙 3D Coin Physics Engine:
Deposit: A cascade of rotating 3D gold coins rains into your balance with gravity and floor bounce.
Withdrawal: Coins blast outward toward the screen in an explosive particle burst.
📊 3D Isometric Cash-Flow Chart: Visualizes income vs. expenses as animated 3D isometric columns with hover elevation and exact amount tooltips.
⚖️ Minimum Balance Rule Enforcement: Strict Rs. 1,000 minimum balance policy on savings accounts with interactive error dialogs.
📜 Live Statement Feed: In-app real-time transaction table with colored badges, plus direct console statement printing.

Bank-Management-System/
├── Main.java             # Entry point & complete self-contained 3D Swing GUI engine
├── Account.java          # Core account business logic & transaction handling
├── SavingsAccount.java   # Subclass enforcing the Rs. 1,000 minimum balance rule
├── BankSystem.java       # User authentication and account registry
├── Transaction.java      # Transaction records, timestamps, and formatting
├── run.bat               # 1-click launch script for Windows
└── README.md             # Project documentation

![Java](https://img.shields.io/badge/Java-21%2B%20%7C%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![UI](https://img.shields.io/badge/GUI-Pure%20Java%20Swing%20(3D)-00F0FF?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Completed-10B981?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

A cutting-edge **Smart ATM & Bank Management System** desktop application built in **pure Java Swing (`Graphics2D`)**. It features an interactive **3D animated interface**, real-time perspective projection, a holographic debit card with mouse-tracking tilt, an animated mechanical bank safe vault, 3D coin particle physics, and glassmorphic cyberpunk styling—all with **zero external libraries or native dependencies**.

---

## 🚀 How to Clone & Run on Your PC
git clone https://github.com/krunal-226/Bank-Management-System.git

cd Bank-Management-System

javac Main.java

java Main

### Prerequisites
Make sure you have **Java (JDK 11 or higher)** installed:
```bash
java -version
javac -version
