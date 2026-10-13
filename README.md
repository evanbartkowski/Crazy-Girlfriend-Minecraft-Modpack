# Crazy Girlfriend Modpack (Minecraft Java Edition 1.20.1)

A fully functional, humorous, interactive multiplayer relationship and romance mod for Minecraft Java Edition.

Introduces a complete relationship lifecycle (asking someone out, dating, marriage, anniversaries, breakups, and divorces), synchronized player animations (hug, kiss, slap, straddle, piggyback, and romantic embrace), custom items (Red Rose, Diamond Engagement Ring, Flower Bouquets, Throwable Slipper, Makeup Kit, Disciplinary Paddle, Player Leash, Heart Chocolates, Teddy Bear), custom makeup rendering on player skins, and an interactive relationship menu GUI.

---

## 🌟 Key Features

### 1. ❤️ Girlfriend & Marriage System
* **Ask Someone to Be Your Girlfriend**: Right-click another player holding a **Red Romance Rose**.
  * Target receives an interactive chat notification: `❤️ [Player] is asking you to be their girlfriend! Type YES or click [ACCEPT] / [DECLINE]`.
  * On acceptance: romantic chime sound plays, heart particles burst around both players, and the relationship is announced server-wide.
  * Relationships persist across deaths, dimension switches, disconnects, and server restarts.
* **Propose Marriage**: Right-click your dating partner with a **Diamond Engagement Ring**.
  * On acceptance: wedding bell fanfare rings, sparkling fireworks and heart showers spawn, upgrading status to **Married 💍**.
* **Relationship Anniversaries**: Tracks the exact timestamp and day the relationship formed.
* **Consent & Safety**: Mutual consent is required for all relationships. Duplicate requests and invalid transitions are rejected.
* **Breakups & Divorces**: `/cg breakup` ends dating; `/cg divorce` dissolves marriage with heartbreak particles and sound effects.

### 2. 🎭 Synchronized Player Animations & Interactions
* `/cg hug <player>`: Both players smoothly draw together, wrap arms around each other with heart particles and sound.
* `/cg kiss <player>`: Characters lean in gently, tilt their heads, and play a sweet kiss sound.
* `/cg slap <player>`: Attacker's arm violently swings across; victim's head snaps back with star particles, cartoon slap sound, and knockback.
* `/cg straddle <player>`: Playful seated lap animation with floating hearts.
* `/cg piggyback <player>`: Target hops onto the player's back! Carrier walks around carrying the passenger, who sits on their shoulders holding on until dismounting.
* `/cg makelove`: A non-explicit, mutually accepted romantic embrace featuring a gentle fade-to-black cinematic vignette overlay, playful giggle sound, and a flurry of hearts.
* All animations smoothly transform limbs (`head`, `body`, `right_arm`, `left_arm`, `right_leg`, `left_leg`) through custom client-side bone interpolation and synchronize in real time across the server.

### 3. 🎁 Custom Relationship Items & Recipes

| Item | Crafting / Materials | Behavior |
|---|---|---|
| **Red Romance Rose** | Poppy + Rose Bush + Honeycomb + Gold Nugget | Right-click player to propose dating. |
| **Diamond Engagement Ring** | Diamond + 3 Gold Ingots | Right-click partner to propose marriage. |
| **Flower Bouquets** (Red, Pastel, Sunflower) | Poppies / Tulips / Sunflowers + String + Paper | Right-click player to gift the bouquet with flower petals and hearts. |
| **Emotional Damage Slipper** | Pink Wool + 3 Leather + String | Thrown projectile! Deals cartoon knockback, plays slap sound, drops item on impact to be retrieved. |
| **Glamour Makeup Kit** | Iron Ingot + Glass Pane + Dyes + Feather | Right-click player to open interactive Makeup Studio. |
| **Makeup Cleansing Sponge** | White Wool + Water Bucket + Paper | Right-click player or self to wipe off all face markings. |
| **Disciplinary Paddle** | Oak Planks + Stick | Playfully spanks consenting partner with comic smack sound and star particles. |
| **Romantic Player Leash** | Lead + Pink Wool + Gold Nugget | Right-click player to request leash. Pulls leashed player along; right-clicking toggles sitting/standing. |
| **Box of Heart Chocolates** | Cocoa Beans + Sugar + Paper | Edible confection granting Regeneration II (10s) and Speed I (15s). |
| **Cuddle Teddy Bear** | Brown Wool + Oak Button + String | Huggable plushie that emits heart particles and warm chimes when squeezed. |
| **Handwritten Love Letter** | Parchment Envelope with Wax Heart | Right-click to read romantic poetry in chat. |

### 4. 💄 Makeup Studio & Custom Face Markings
* Right-click another player with the **Makeup Kit** to open the **Makeup Studio GUI**.
* Customizations:
  * **Lipstick**: None, Ruby Red 💄, Soft Pink 🌸, Midnight Black 🖤, Berry Purple 🍇
  * **Blush**: None, Rosy Cheek 🌸, Anime Peach 🍑, Doll Red 🍎
  * **Eyeliner**: None, Winged Cat Eye 👁️, Smoky Eye ✨
  * **Face Markings**: None, Cat Whiskers 🐱, Cheek Hearts 💕, Clown Nose 🤡, Cute Star ⭐, French Mustache 🥸
* Renders directly onto the player's head via custom `MakeupFeatureRenderer` without modifying skin files.
* Consent-protected: Target player must accept the makeup request before it is applied.
* Easily washed off anytime with `/cg wipeface` or a **Makeup Cleansing Sponge**.

### 5. 🪢 Player Leash & Safety System
* **Consent First**: A player must accept the leash prompt before the leash is attached.
* **Dynamic Lead Rendering**: Smooth pink/gold silk rope connects the leash holder and the leashed player.
* **Following & Sitting**: Leashed player is pulled smoothly toward the holder when walking. The holder can right-click the leashed player to toggle sitting.
* **Server-Side Restrictions**: Leashed players cannot break blocks, place blocks, or attack entities.
* **Instant Escape**: Leashed players can break free at any second by running `/cg unleash`.
* **Automatic Safeguards**: Automatically unleashes if either player dies, disconnects, changes dimensions, or exceeds max distance.

### 6. 📱 Interactive Relationship Menu GUI
* **Sneak + Right-Click** any player (or press **K**) to open the **Relationship Menu**.
* Displays the target player's name and status badge (`Single`, `Dating ❤️ [Partner]`, or `Married 💍 [Partner]`).
* Quick-action buttons: Give Rose, Propose Marriage, Hug, Kiss, Slap, Straddle, Piggyback, Make Love, Gift Bouquet, Apply Makeup, Paddle Spank, Player Leash, Break Up, and Divorce.

---

## ⌨️ Command Reference

| Command | Description |
|---|---|
| `/cg accept` | Accept an incoming relationship, animation, makeup, or leash request. |
| `/cg decline` | Decline an incoming request. |
| `/cg status [player]` | View your own or another player's relationship status, partner, and anniversary date. |
| `/cg hug <player>` | Send a hug request. |
| `/cg kiss <player>` | Send a kiss request. |
| `/cg slap <player>` | Comically slap a player. |
| `/cg straddle <player>` | Send a straddle request. |
| `/cg piggyback <player>` | Send a piggyback ride request. |
| `/cg makelove` | Initiate romantic embrace with your partner. |
| `/cg breakup` | End your dating relationship. |
| `/cg divorce` | Dissolve your marriage. |
| `/cg unleash` | Instantly release yourself from a leash. |
| `/cg wipeface` | Wash all makeup off your face. |
| `/cg autoaccept [on\|off]` | Toggle auto-accepting requests from your romantic partner. |
| `/cg help` | Display full in-game help menu. |

---

## ⚙️ Configuration (`config/crazygirlfriend.json`)

```json
{
  "allowLeashRestrictions": true,
  "leashMaxDistance": 16.0,
  "interactionTimeoutSeconds": 30,
  "slapKnockback": 0.4,
  "slipperDamage": 1.0,
  "slipperKnockback": 0.5,
  "enableRelationshipHearts": true,
  "enableJealousyEffects": true,
  "autoAcceptForPartners": false
}
```

---

## 📦 Installation & Setup

### Requirements
* **Minecraft Version**: `1.20.1` (Java Edition)
* **Mod Loader**: Fabric Loader (`0.15.11` or newer)
* **Java Runtime**: Java 17 or Java 21

### Client Installation (CurseForge / Modrinth / Prism Launcher)
1. **Method A — One-Click Modpack Import**:
   * Drag and drop `Crazy-Girlfriend-Modpack-1.0.0.zip` into Prism Launcher, CurseForge App, Modrinth App, or ATLauncher.
2. **Method B — Manual Mods Folder**:
   * Place `CrazyGirlfriend-1.0.0.jar` and `fabric-api-0.92.2+1.20.1.jar` into your `.minecraft/mods` directory.

### Dedicated Server Installation
1. Install Fabric Loader on your Minecraft 1.20.1 server.
2. Copy `CrazyGirlfriend-1.0.0.jar` and `fabric-api-0.92.2+1.20.1.jar` into the server's `mods/` directory.
3. Start the server. The server automatically initializes `crazygirlfriend_relationships.dat` persistent storage.

---

## 🛠️ Building From Source

```bash
# Clone the repository
git clone https://github.com/crazygirlfriend/mod.git
cd "Crazy Girlfriend Minecraft Modpack"

# Run tests
./gradlew test

# Build the mod JAR
./gradlew build
```
Compiled mod jar will be in `build/libs/CrazyGirlfriend-1.0.0.jar`.
