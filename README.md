# Crazy Girlfriend Modpack (Minecraft Java Edition 1.20.1 Fabric)

A hilarious, romantic, chaotic, and highly interactive multiplayer relationship mod for Minecraft Java Edition.

Expand your multiplayer adventures with full relationship lifecycles, **65+ synchronized player animations**, the **Crazy Girlfriend Mode & Crazy Meter (0-100%)**, **80 randomized couple events**, **10 interactive minigames**, **comedy weapons**, **interactive couple furniture**, an in-game **photo camera & memory album**, and a context-sensitive **reaction engine**!

---

## 📦 Installation & Setup Guide

### 📋 Prerequisites
* **Minecraft Version**: `1.20.1` (Java Edition)
* **Mod Loader**: [Fabric Loader 0.15.11+](https://fabricmc.net/use/installer/)
* **Java Runtime**: Java 17 or Java 21 (64-bit)

---

### Option 1: Prism Launcher / MultiMC (Recommended)
1. Download [`Crazy-Girlfriend-Modpack-1.0.0.zip`](file:///c:/Users/evan/Documents/Crazy%20Girlfriend%20Minecraft%20Modpack/Crazy-Girlfriend-Modpack-1.0.0.zip) from this repository.
2. In Prism Launcher / MultiMC, click **Add Instance** ➔ **Import from zip**.
3. Select `Crazy-Girlfriend-Modpack-1.0.0.zip` and click **OK**.
4. The instance will immediately configure Minecraft 1.20.1, Fabric Loader, and load all mods and configs automatically. Launch and play!

---

### Option 2: Modrinth App
1. Download [`Crazy-Girlfriend-Modpack-1.0.0.mrpack`](file:///c:/Users/evan/Documents/Crazy%20Girlfriend%20Minecraft%20Modpack/Crazy-Girlfriend-Modpack-1.0.0.mrpack) from this repository.
2. In Modrinth App, click the **+** (Create Profile) icon ➔ **Import from file**.
3. Select `Crazy-Girlfriend-Modpack-1.0.0.mrpack`.
4. Launch the profile and enjoy!

---

### Option 3: CurseForge / ATLauncher
1. Download [`Crazy-Girlfriend-Modpack-1.0.0.zip`](file:///c:/Users/evan/Documents/Crazy%20Girlfriend%20Minecraft%20Modpack/Crazy-Girlfriend-Modpack-1.0.0.zip).
2. In CurseForge, click **Create Custom Profile** ➔ **Import a previously created profile**, then select the `.zip`.
3. In ATLauncher, go to **Instances** ➔ **Add Instance** ➔ **Import** and choose the `.zip`.

---

### Option 4: Manual Installation (Standard Minecraft Launcher)
1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for **Minecraft 1.20.1**.
2. Open your Minecraft directory:
   * **Windows**: `Win + R` ➔ `%appdata%\.minecraft` ➔ Enter
   * **macOS**: `~/Library/Application Support/minecraft`
   * **Linux**: `~/.minecraft`
3. Place into `.minecraft/mods/`:
   * `CrazyGirlfriend-1.0.0.jar` (from `build/libs/` or extracted from modpack)
   * `fabric-api-0.92.2+1.20.1.jar`
4. Select the **Fabric Loader 1.20.1** profile in the Minecraft Launcher and click Play.

---

### Option 5: Dedicated Multiplayer Server Setup
1. Run the [Fabric 1.20.1 Server](https://fabricmc.net/use/server/) installer in a dedicated folder.
2. Copy `CrazyGirlfriend-1.0.0.jar` and `fabric-api-0.92.2+1.20.1.jar` into the server's `mods/` directory.
3. Accept the Minecraft EULA (`eula.txt -> eula=true`).
4. Start the server:
   ```bash
   java -Xmx4G -jar fabric-server-launch.jar nogui
   ```
5. All connecting players must also have `CrazyGirlfriend-1.0.0.jar` installed on their clients for synchronized animations, expressions, and menus.

---

### Option 6: Building from Source (Developers)
```powershell
# Clone the repository
git clone https://github.com/evanbartkowski/Crazy-Girlfriend-Minecraft-Modpack.git
cd "Crazy-Girlfriend-Minecraft-Modpack"

# Run test suite
./gradlew test

# Build production JAR (output in build/libs/)
./gradlew build
```

---

## 🌟 Major Systems & Features

### 1. ❤️ Complete Relationship Lifecycle
* **Propose Dating**: Right-click someone with the **Red Romance Rose**.
  * Chat prompt: `❤️ [Player] is asking you to be their girlfriend! Type YES to accept or NO to decline.`
  * Interactive clickable `[ACCEPT]` and `[DECLINE]` buttons with chimes, rose particles, and server announcement.
* **Propose Marriage**: Right-click your dating partner with a **Diamond Engagement Ring**.
  * Upgrades relationship to **Married 💍**, ringing wedding bells with fireworks and golden sparkles.
* **Ceremonial Wedding Altar**: Right-click partner with the **Wedding Altar** for a formal ceremony that summons fireworks, plays celebratory cheering, grants absorption/regeneration buffs, and awards the couple a **Wedding Cake**!
* **Anniversary Celebrations**: Tracks the exact timestamp and anniversary date. Celebrating with the altar triggers anniversary fireworks and buffs!
* **Divorce Court & Breakups**: Right-click partner with comical **Divorce Papers** to serve them in divorce court with sad violin sound and gavel strikes, or use `/cg breakup`.

### 2. ⚡ Crazy Girlfriend Mode & Crazy Meter (0–100%)
Track your partner's emotional state with a dynamic meter and 5 distinct tiers:
* **Tier 1: Sweetheart (0–24%) 💖**: Gentle affection, surprise gifts, sweet hugs, flower deliveries.
* **Tier 2: Suspicious (25–49%) 🤨**: Bombastic side-eye animations, checking chests, suspicious questioning about wandering traders.
* **Tier 3: Drama Queen (50–74%) 👑**: Dramatic weeping, foot stomping, falling to knees in telenovela despair, theatrical slipper throws.
* **Tier 4: Furious (75–94%) ⚡**: Angry jumping, tantrums, obsidian mining in rage, fiery sparks and comical chases!
* **Tier 5: Total Chaos (95–100%) 🌪️**: Full cartoon slapstick apocalypse! Frying pan duels, water balloon ambushes, bed launches, and pillow fort standoffs!
* **Soothe & Pacify**: Reduce the meter using chocolates, sweet treats, romantic dates, apologies, and couple cuddles.

### 3. 🎭 65+ Synchronized Player Animations & Interactive Combos
Procedurally animated across all character bones (`head`, `body`, `right_arm`, `left_arm`, `right_leg`, `left_leg`) with smooth limb interpolation, cubic easing, and idle micro-movements:
* **Romantic Interactions & Variations**: Warm Hug, Bear Hug, Spinning Hug, Jumping Hug, Romantic Sway Hug, Sweet Kiss, Passionate Make Out, Romantic Dip Kiss, Forehead Kiss, Cheek Kiss, Kiss Attack, Princess Carry Kiss, Face Nuzzle, Cuddle, Campfire Snuggle, Hand Holding, Rain Twirl, Slow Dance, Stargazing, Make Love (non-explicit romantic fade-to-black), and Flower Gifting.
* **Comedy & Chaos Animations**: Dramatic Slap, 360 Spinning Slap, Raw Fish Slap, Frying Pan BONK, Head Bonk Kiss, Paddle Spank (attacker swing & victim bend-over), Slipper Impact, Squeaky Hammer Hit, Foot Stomping, Angry Arms-Crossed, Dramatic Crying, Fall in Despair, Fake Fainting, Ragdoll Flop, Exaggerated Side-Eye, Cartoon Tantrum, Rolling Eyes, Uncontrollable Laughing, Playful Wrestling, Pillow Fighting, Ring Throwing, and Dizzy Swirl.
* **Carrying & Mount Actions**: Piggyback Ride, Passenger Waving, Heavy Armor Stumble Carry, Gentle Dismount, Bridal Carry, Shoulder Carry, Straddle Lap Seating.
* **💫 Interactive Combo System**: Active animations display an on-screen HUD prompt (`[1] Dip Kiss  [2] Spin  [3] Hold Hands  [4] Hug`). Press number keys to branch fluidly into follow-up sequences without interrupting gameplay!
* **😊 16 Dynamic Facial Expressions**: Non-destructive feature renderer overlays animated expressions onto player skins: Blushing, Smiling, Closed Eyes, Winking, Angry Eyebrows, Shocked, Embarrassed, Cartoon Tears, Sad, Pouting, Suspicious Side-Eye, Dizzy Spiral Eyes, Sleepy, Laughing, and Drama Crying! Trigger via `/cg expression <name>` or dynamically through actions and moods.
* **🎥 Cinematic Camera System**: Optional subtle cinematic camera movements (`ModConfig.enableCinematicCamera`): gentle camera roll during romantic dips, subtle screen shake on comedic bonks and slaps, and gentle FOV zoom during intimate moments.
* **Safety & Consent**: All actions respect partner auto-accept permissions (`/cg autoaccept`). Type `/cg cancel` at any moment to cancel an animation or dismount instantly.

### 4. 🎮 10 Interactive Couple Minigames & Dates
Start games via command (`/cg minigame <id>`) or the Interaction GUI:
1. **Couple Trivia (`trivia`)**: Humorous multiple-choice questions testing how well you know each other!
2. **Rock-Paper-Scissors (`rps`)**: Best-of-3 comedy hand duel with animations.
3. **Pillow War Arena (`pillow_fight`)**: 30-second feather frenzy challenge!
4. **Wrestling Match (`wrestling`)**: Living room slapstick wrestling on the carpet!
5. **Love Roulette (`roulette`)**: Spin the wheel for random funny buffs, speed boosts, or comical tickle attacks!
6. **Scavenger Hunt (`scavenger`)**: 60-second race to collect a surprise romantic item!
7. **Secret Gift Challenge (`gift_challenge`)**: Exchange mystery presents for rewards and affection XP!
8. **Kiss Cam (`kiss_cam`)**: 10-second countdown to locate each other and share a kiss!
9. **Dance-Off (`dance_challenge`)**: Synchronized rhythmic dance duel!
10. **The Apology Challenge (`apology_challenge`)**: Deliver a dramatic apology to wipe the Crazy Meter clean!

### 5. 🛏️ Bedroom Mechanics & Morning-After Scenes
* **Couple Double Beds**: Right-click to snuggle together in shared blankets.
* **Romantic Night & Morning-After**: Romantic embrace triggers a gentle vignette fade-to-black followed by randomized comedic morning outcomes:
  * Fresh coffee & pancakes with speed buffs!
  * Partner stole all the blankets, leaving you shivering!
  * Bed slat collapse with a comical creak sound!
  * Pillow ambush with feathers floating everywhere!
* **"Caught in 4K" Detection**: If a third player enters within 6 blocks during an intimate scene, characters jump apart in embarrassment, triggering comical blushed reactions and the "Caught in 4K!" advancement!

### 6. 🍳 Comedy Weapons & Prank Arsenal
* **Cast Iron Frying Pan**: Deals comic knockback and plays loud metallic `BONK` sound with cartoon stars!
* **Throwable Slipper**: Inflicts Emotional Damage, spinning the victim in a dramatic 360° circle with dizzy eyes!
* **Boomerang Slipper**: Smacks targets with **Knockback 5** and vertical lift, dealing emotional damage and looping back into your hand!
* **Water Balloons**: High-speed projectile throw (`2.4x` velocity) that applies **Slowness II** and **Knockback 2**, bursting into splashing water particles and causing hit targets to shake themselves dry!
* **Feather Battle Pillow**: Soft melee weapon that deals **0 damage**, launches feather clouds, and causes victims to comically flop to the ground (ragdoll faint) taking ~1 second to get back up!
* **Disciplinary Paddle**: Direct right-click on another player forces them to bend over forward (90° pose) while you deliver a resounding paddle spank! Also swats mobs with crit particles and knockback.
* **Aphrodisiac Love Potion**: Drinkable or splash potion giving **Speed V**, a drunk screen wobble, and a pulsing pink euphoria overlay. Getting close to another player (< 2.8 blocks) triggers an uncontrollable pounce and comedic romance!
* **Squeaky Hammer**: Toy hammer that emits squeaks on every hit and sends enemies bouncing!
* **Rolled Up Newspaper**: Swat naughty friends with comic paper swats!

### 7. 📸 Memories, Shared Custody & Lifestyle
* **Soulmate Heart Compass**: Tracks your partner's coordinates in real time across dimensions; right-click displays distance, mood, and status!
* **Instant Couple Camera**: Snaps photos with real flash and shutter sound, adding memories to your photo album and reducing Crazy Meter!
* **Romantic Photo Album**: Stores and displays memorable dates with locations, dates, and partner names!
* **Joint Custody Pet Collar**: Use on tamed dogs or cats to share pet custody with your partner!
* **Matching Soulmate Hoodie**: Wearable chestplate granting **Soulmate Warmth** (Speed & Resistance buffs) when standing near your partner!
* **Interactive Couple Furniture**: Loveseats, Double Beds, Garden Swings, and Picnic Blankets that spawn seats for two players to sit side-by-side!

### 8. 📱 Categorized Interaction Dashboard & Status Bar
* **Sneak + Right-Click** any player (or press **K**) to open the **Crazy Girlfriend Hub**!
* Features 4 category tabs:
  * **❤️ Romance**: Hugs, kisses, proposals, stargazing, dancing.
  * **⚡ Chaos**: Slaps, bonks, tantrums, despair, slipper throws.
  * **🎮 Dates**: All 10 couple minigames.
  * **🛠️ Actions**: Carries, makeup studio, leashes, furniture seating.
* **Top Status Dashboard**: Displays an active **Crazy Meter bar** (color-coded by tier: Sweetheart -> Total Chaos), Affection Level & Stars, and live partner Mood!

### 9. 🏆 45 Themed Advancements
A complete custom advancement tree spanning Dating, Crazy Girlfriend Tiers, Comedy Weapons, Minigames, and Bedroom Intimacy.

---

## ⌨️ Command Reference

| Command | Description |
|---|---|
| `/cg status [player]` | View relationship status, partner, crazy meter %, tier, mood, and affection level. |
| `/cg anim <name> [player]` | Perform any of the 63 animations with your partner. |
| `/cg minigame <game>` | Challenge partner to any of the 10 minigames. |
| `/cg minigame_ans <game> <ans>` | Submit answer for active minigames. |
| `/cg event trigger [id]` | Trigger a randomized relationship event. |
| `/cg eventchoice <id> <A\|B>` | Choose your response in a relationship event. |
| `/cg mood <mood>` | Set relationship mood (HAPPY, LOVING, EXCITED, SHY, ANNOYED, JEALOUS, FURIOUS, EMBARRASSED, SLEEPY, SAD). |
| `/cg cancel` | Instantly cancel any ongoing animation, piggyback ride, or carry. |
| `/cg autoaccept [on\|off]` | Toggle auto-accepting requests from your romantic partner. |
| `/cg unleash` | Instantly break free from a leash. |
| `/cg wipeface` | Wash all makeup off your face. |
| `/cg breakup` | End dating relationship. |
| `/cg divorce` | Dissolve marriage. |
| `/cg help` | View in-game command help guide. |
