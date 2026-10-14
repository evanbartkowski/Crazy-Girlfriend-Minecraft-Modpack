# Crazy Girlfriend Modpack (Minecraft Java Edition 1.20.1 Fabric)

A hilarious, romantic, chaotic, and highly interactive multiplayer relationship mod for Minecraft Java Edition.

Expand your multiplayer adventures with full relationship lifecycles, **65+ synchronized player animations**, the **Crazy Girlfriend Mode & Crazy Meter (0-100%)**, **80 randomized couple events**, **10 interactive minigames**, **comedy weapons**, **interactive couple furniture**, an in-game **photo camera & memory album**, a context-sensitive **reaction engine**, and a complete **dreamy romantic visual & atmospheric overhaul**!

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

## 🎁 Complete Item & Block Guide

Every item and block in the modpack and exactly what it does:

### ❤️ Romance & Relationship Items
| Item | Description & What It Does |
|---|---|
| **Red Romance Rose** (`red_rose`) | Right-click another player to ask them to be your girlfriend! Displays an interactive chat prompt with `[ACCEPT]` and `[DECLINE]` clickable buttons, romantic chimes, rose particles, and server announcement. |
| **Diamond Engagement Ring** (`diamond_ring`) | Right-click your dating partner to propose marriage! Upgrades relationship status to Married 💍 with wedding bells, sparkles, and celebratory fireworks. |
| **Romantic Rose Bouquet** (`flower_bouquet`) | Gift to your partner to award Affection XP, boost their relationship mood, and soothe Crazy Meter. |
| **Pastel Blossom Bouquet** (`pastel_bouquet`) | Sweet arrangement of spring blossoms that cheers up your partner and grants a brief Speed buff. |
| **Golden Sunflower Bouquet** (`sun_bouquet`) | Radiant bouquet of sunflowers emitting golden sparkle particles that pacifies an angry or sad partner. |
| **Box of Heart Chocolates** (`chocolate_box`) | Delicious chocolates. Eating or gifting to a partner soothes anger, reduces Crazy Meter by 15%, and restores hunger and hearts. |
| **Handwritten Love Letter** (`love_letter`) | Express romantic feelings in writing. Right-click partner to grant Affection XP and display floating heart particles. |
| **Ceremonial Wedding Altar** (`wedding_altar`) | Right-click partner to hold a formal wedding ceremony complete with church bells, celebratory cheering, absorption/regeneration buffs, fireworks, and a Wedding Cake! Also triggers anniversary celebrations. |
| **Wedding Celebration Cake** (`wedding_cake`) | A multi-slice wedding cake featuring mini bride & groom toppers. Eating slices with your partner grants heart particles and affection boosts. |
| **Formal Divorce Papers** (`divorce_papers`) | Right-click partner to serve them in comical divorce court with sad violin music and judge gavel sound effects, dissolving the marriage. |

---

### ⚔️ Comedy Weapons & Slapstick Prank Arsenal
| Item | Description & What It Does |
|---|---|
| **Oversized Squeaky Hammer** (`squeaky_hammer`) | Slapstick toy mallet with **Knockback 200**! Hitting any player or mob plays a high-pitched squeak and launches them into orbit across the map. |
| **Cast Iron Frying Pan** (`frying_pan`) | Classic cartoon weapon that plays a loud metallic `BONK!` sound, creates cartoon dizzy stars, and sends targets stumbling backward. |
| **Emotional Damage Slipper** (`throwable_slipper`) | Throwable slipper that smacks targets for emotional damage, spinning them around in a dramatic 360° circle with dizzy spiral eyes. |
| **Boomerang Slipper** (`boomerang_slipper`) | Throw at friends to strike them with **Knockback 5** and vertical lift! Automatically loops back through the air and returns to your hand. |
| **Water Balloons** (`water_balloon`) | High-speed projectile throw (`2.4x` velocity) that bursts into water splashes on hit, inflicts **Slowness II** and **Knockback 2**, and makes players shake dry. |
| **Feather Battle Pillow** (`pillow`) | Soft battle pillow that deals **0 damage**, releases feather clouds, and causes victims to comically flop to the ground (ragdoll faint) taking ~1.5s to get back up. |
| **Disciplinary Paddle** (`paddle`) | Right-click another player to playfully discipline them! Forces the victim to bend over forward (90° pose) while you deliver a resounding paddle spank with crit sparks and audio. |
| **Aphrodisiac Love Potion** (`love_potion`) | Drinkable or splash potion giving **Speed V**, drunken screen wobble, and a pulsing pink euphoria overlay. Approaching any player (<2.8 blocks) triggers an uncontrollable pounce and playful romantic make-love sequence with hearts and giggles! |
| **Rolled Up Newspaper** (`newspaper`) | Swat naughty friends or partners with a crisp paper slap sound and harmless playful knockback. |

---

### 🛋️ Placeable Couple Furniture & 3D Plushies
*All furniture pieces are placeable on the ground as physical 3D blocks with custom models and textures!*

| Item / Block | Description & What It Does |
|---|---|
| **Checkered Picnic Blanket** (`couple_picnic_blanket`) | A red-and-white checkered picnic blanket block. Right-click to sit down and relax on the ground; your partner can right-click to cuddle up right beside you! |
| **Cozy Couple Loveseat** (`couple_sofa`) | A plush pastel pink 3D upholstered couch block. Right-click to sit; supports two players sitting side-by-side with heart particles and facing alignment. |
| **Couple Cuddle Double Bed** (`couple_double_bed`) | A romantic 3D double bed block with pillows and a duvet. Right-click to snuggle together in shared blankets, lie down, or sleep together. |
| **Romantic Garden Swing** (`couple_swing`) | A wooden porch swing with suspension ropes. Right-click to sit and gently sway together with your partner. |
| **Cuddle Teddy Bear** (`teddy_bear`) | An adorable 3D plush teddy bear block! Place it on the floor, table, or bed. Right-click to hug/squeeze it for squeaks and heart particles. Sneak + Right-click to pick it back up into your inventory. |

---

### 📸 Lifestyle, Camera, Custody & Apparel
| Item | Description & What It Does |
|---|---|
| **Instant Couple Camera** (`couple_camera`) | Handheld camera with real flash particles and shutter audio. Right-click to snap scenic or couple photos; **Sneak + Right-Click to take a cute selfie!** Directly prints an inspectable **Polaroid Photograph** into your inventory. |
| **Polaroid Photograph** (`photograph`) | A glossy printed polaroid photo item. Right-click to inspect the polaroid snapshot card showing memory title, photo type (Selfie or Couple Date), photographer name, date, time, and coordinates. Can be framed on walls or saved in albums! |
| **Romantic Photo Album** (`photo_album`) | Stores and displays precious memories captured by your camera. Right-click to flip through dates and partner milestones. |
| **Soulmate Heart Compass** (`partner_compass`) | Always tracks your partner's exact coordinates across any dimension. Right-click to check distance, partner mood, and live Crazy Meter tier. |
| **Joint Custody Pet Collar** (`shared_collar`) | Right-click your tamed dog or cat to grant joint pet custody to your partner so both players can command and lead the pet. |
| **Matching Soulmate Hoodie** (`couple_hoodie`) | Fully rendered wearable 3D pastel pink hoodie with drawstrings, pouch, and heart badge! Grants **Soulmate Warmth** (Speed & Resistance buffs) whenever standing within 16 blocks of your partner. |
| **Glamour Makeup Kit** (`makeup_kit`) | Right-click another player to open the full Makeup Studio GUI. Apply blush, lipstick, eyeliner, anime tears, or silly clown makeup! |
| **Makeup Cleansing Sponge** (`makeup_remover`) | Right-click a player or yourself to wash off all makeup and facial markings. |
| **Romantic Player Leash** (`player_leash`) | Tether a consenting friend and guide them around. Partner can break free anytime using `/cg unleash`. |

---

## 🌟 Major Systems & Features

### 1. ⚡ Crazy Girlfriend Mode & Crazy Meter (0–100%)
Track your partner's emotional state with a dynamic meter and 5 distinct tiers:
* **Tier 1: Sweetheart (0–24%) 💖**: Gentle affection, surprise gifts, sweet hugs, flower deliveries.
* **Tier 2: Suspicious (25–49%) 🤨**: Bombastic side-eye animations, checking chests, suspicious questioning about wandering traders.
* **Tier 3: Drama Queen (50–74%) 👑**: Dramatic weeping, foot stomping, falling to knees in telenovela despair, theatrical slipper throws.
* **Tier 4: Furious (75–94%) ⚡**: Angry jumping, tantrums, obsidian mining in rage, fiery sparks and comical chases!
* **Tier 5: Total Chaos (95–100%) 🌪️**: Full cartoon slapstick apocalypse! Frying pan duels, water balloon ambushes, bed launches, and pillow fort standoffs!
* **Soothe & Pacify**: Reduce the meter using chocolates, sweet treats, romantic dates, apologies, and couple cuddles.

### 2. 🎭 65+ Synchronized Player Animations & Interactive Combos
Procedurally animated across all character bones (`head`, `body`, `right_arm`, `left_arm`, `right_leg`, `left_leg`) with smooth limb interpolation, cubic easing, and idle micro-movements:
* **Romantic Interactions & Variations**: Warm Hug, Bear Hug, Spinning Hug, Jumping Hug, Romantic Sway Hug, Sweet Kiss, Passionate Make Out, Romantic Dip Kiss, Forehead Kiss, Cheek Kiss, Kiss Attack, Princess Carry Kiss, Face Nuzzle, Cuddle, Campfire Snuggle, Hand Holding, Rain Twirl, Slow Dance, Stargazing, Make Love (non-explicit romantic fade-to-black), and Flower Gifting.
* **Comedy & Chaos Animations**: Dramatic Slap, 360 Spinning Slap, Raw Fish Slap, Frying Pan BONK, Head Bonk Kiss, Paddle Spank (attacker swing & victim bend-over), Slipper Impact, Squeaky Hammer Hit, Foot Stomping, Angry Arms-Crossed, Dramatic Crying, Fall in Despair, Fake Fainting, Ragdoll Flop, Exaggerated Side-Eye, Cartoon Tantrum, Rolling Eyes, Uncontrollable Laughing, Playful Wrestling, Pillow Fighting, Ring Throwing, and Dizzy Swirl.
* **Carrying & Mount Actions**: Piggyback Ride, Passenger Waving, Heavy Armor Stumble Carry, Gentle Dismount, Bridal Carry, Shoulder Carry, Straddle Lap Seating.
* **💫 Interactive Combo System**: Active animations display an on-screen HUD prompt (`[1] Dip Kiss  [2] Spin  [3] Hold Hands  [4] Hug`). Press number keys to branch fluidly into follow-up sequences without interrupting gameplay!
* **😊 16 Dynamic Facial Expressions**: Non-destructive feature renderer overlays animated expressions onto player skins: Blushing, Smiling, Closed Eyes, Winking, Angry Eyebrows, Shocked, Embarrassed, Cartoon Tears, Sad, Pouting, Suspicious Side-Eye, Dizzy Spiral Eyes, Sleepy, Laughing, and Drama Crying! Trigger via `/cg expression <name>` or dynamically through actions and moods.
* **🎥 Cinematic Camera System**: Optional subtle cinematic camera movements (`ModConfig.enableCinematicCamera`): gentle camera roll during romantic dips, subtle screen shake on comedic bonks and slaps, and gentle FOV zoom during intimate moments.
* **Safety & Consent**: All actions respect partner auto-accept permissions (`/cg autoaccept`). Type `/cg cancel` at any moment to cancel an animation or dismount instantly.

### 3. 🎮 10 Interactive Couple Minigames & Dates
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

### 4. 🛏️ Bedroom Mechanics & Morning-After Scenes
* **Couple Double Beds**: Right-click to snuggle together in shared blankets.
* **Romantic Night & Morning-After**: Romantic embrace triggers a gentle vignette fade-to-black followed by randomized comedic morning outcomes:
  * Fresh coffee & pancakes with speed buffs!
  * Partner stole all the blankets, leaving you shivering!
  * Bed slat collapse with a comical creak sound!
  * Pillow ambush with feathers floating everywhere!
* **"Caught in 4K" Detection**: If a third player enters within 6 blocks during an intimate scene, characters jump apart in embarrassment, triggering comical blushed reactions and the "Caught in 4K!" advancement!

### 5. 📱 Categorized Interaction Dashboard & Status Bar
* **Sneak + Right-Click** any player (or press **K**) to open the **Crazy Girlfriend Hub**!
* Features 4 category tabs:
  * **❤️ Romance**: Hugs, kisses, proposals, stargazing, dancing.
  * **⚡ Chaos**: Slaps, bonks, tantrums, despair, slipper throws.
  * **🎮 Dates**: All 10 couple minigames.
  * **🛠️ Actions**: Carries, makeup studio, leashes, furniture seating.
* **Top Status Dashboard**: Displays an active **Crazy Meter bar** (color-coded by tier: Sweetheart -> Total Chaos), Affection Level & Stars, and live partner Mood!

### 6. 🏆 45 Themed Advancements
A complete custom advancement tree spanning Dating, Crazy Girlfriend Tiers, Comedy Weapons, Minigames, and Bedroom Intimacy.

---

## 🌸 Complete Minecraft Visual & Atmospheric Overhaul

The modpack features a comprehensive visual, celestial, and atmospheric overhaul designed to make the Minecraft world feel like a dreamy, romantic fantasy while preserving its blocky pixel-art identity.

### 🍃 Dynamic Biome Atmospheres & Particles
* **Cherry Groves & Blossoms**: Soft pink sakura blossom petals drifting gently across the breeze.
* **Forests & Flower Meadows**: Floating dandelion pollen, sun-dappled glades, and colorful fluttering butterflies.
* **Swamps & Mangroves**: Eerie low-lying ground mist with warm glowing fireflies emerging at twilight and dusk.
* **Oceans, Rivers & Beaches**: Clear animated turquoise water with dynamic shoreline ripples and foam splashes.
* **Deserts & Badlands**: Swirling dust flurries and blowing sand swept across dunes by the wind.
* **Snowy Taigas & Mountain Peaks**: Whipping blizzards with realistic wind-carried snowflakes.

### 💨 Deterministic Continuous Wind Simulation
* An integrated mathematical wind engine calculates dynamic wind direction, speed, and gust vectors every tick.
* Directly drives particle drift speeds, foliage sway, and cloud/weather trajectories.

### ✨ Celestial Night Sky, Shooting Stars & Auroras
* **Golden Hour & Sunsets**: High-resolution radiant sun with warm golden horizon gradients.
* **Deep Romantic Night Skies**: Scintillating starfields where stars twinkle realistically based on atmospheric depth.
* **Shooting Stars & Meteor Showers**: Random shooting stars streak across the night sky, with rare meteor showers accompanied by gentle celestial chimes.
* **Northern Lights / Auroras**: Shimmering ribbons of green, violet, and rose auroras light up cold biomes and snowy mountain nights.

### 🎨 Built-In Resource Pack & Sweetheart HUD
* Comes pre-packaged with **`Crazy-Girlfriend-Visuals.zip`** (automatically enabled in `options.txt`):
  * **Bushy Lush Grass & Foliage**: Fluffy leaf canopies and vibrant grass block textures.
  * **Seamless Clear Glass**: Borderless, crystal-clear connected-feel glass blocks.
  * **Vibrant Crystal Water**: High-clarity animated turquoise water textures.
  * **Sweetheart HUD**: Custom romantic heart health icons and golden-glazed hunger drumsticks.
  * **Romantic Main Menu**: Floating sakura blossom petals and custom title screen banners.

### ⚙️ Visual Settings Screen & Graphics Presets
* Open the **Crazy Girlfriend Hub** (Sneak + Right-Click or press **K**) and click the **🌸 Visuals** button!
* **4 Graphics Presets**:
  * **LOW**: Lightweight performance mode for low-end PCs (reduced particles, no wind sway).
  * **MEDIUM**: Balanced visual enhancement for standard gameplay.
  * **HIGH (Default)**: Full atmospheric particles, shooting stars, auroras, and ambient audio.
  * **ULTRA**: Maximum particle density, intense starfields, and full visual immersion.
* **Photosensitivity Mode**: One-click toggle that removes all camera screen shakes, white flash frames, and sudden particle bursts.
* **Granular Controls**: Sliders and toggles for Particle Density, Wind Sway, Custom Sky, Weather, and Ambient Sound Volume.

### 🎵 Procedural Environmental Ambient Audio
* **Morning Forest Birdsong**: Procedural birds chirping at dawn in lush biomes.
* **Night Crickets**: Soothing crickets chirping under clear starry skies.
* **Canopy Wind Breezes**: Gentle wind rustling through tree leaves.
* **Shoreline Ocean Waves**: Gentle surf sounds when standing near beaches and oceans.
* **Shooting Star Chimes**: Delicate celestial sparkle audio when wishing on a shooting star.

### 🌟 Shader Mod Compatibility (Iris + Sodium)
* The modpack looks stunning on pure Fabric with zero external mods required!
* For players who want ultra-realistic lighting, the visual overhaul is **100% compatible with [Iris Shaders](https://irisshaders.dev/) + [Sodium](https://modrinth.com/mod/sodium)**:
  * Recommended shaderpacks: **Complementary Reimagined**, **MakeUp-UltraFast**, or **BSL Shaders**.
  * Dynamic particles, custom UI, and sky renderers seamlessly adapt whether shaders are active or disabled.

---

## ⌨️ Command Reference

| Command | Description |
|---|---|
| `/cg status [player]` | View relationship status, partner, crazy meter %, tier, mood, and affection level. |
| `/cg anim <name> [player]` | Perform any of the 65+ animations with your partner. |
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

