package com.crazygirlfriend.event;

import com.crazygirlfriend.animation.AnimationType;
import com.crazygirlfriend.relationship.CrazyTier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RelationshipEventRegistry {
    private static final List<RelationshipEvent> EVENTS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        registerAll();
    }

    public static List<RelationshipEvent> getEvents() {
        return Collections.unmodifiableList(EVENTS);
    }

    public static RelationshipEvent getRandomEvent(CrazyTier tier, boolean crazyModeEnabled) {
        List<RelationshipEvent> candidates = new ArrayList<>();
        for (RelationshipEvent e : EVENTS) {
            if (e.isCrazyEvent()) {
                if (crazyModeEnabled && tier != null && tier.ordinal() >= e.getMinimumTier().ordinal()) {
                    candidates.add(e);
                }
            } else {
                candidates.add(e);
            }
        }
        if (candidates.isEmpty()) return EVENTS.get(0);
        return candidates.get(RANDOM.nextInt(candidates.size()));
    }

    private static void reg(String id, String title, String prompt,
                            String optA, String optB, String outA, String outB,
                            int cA, int cB, int affA, int affB,
                            AnimationType aA, AnimationType aB,
                            boolean isCrazy, CrazyTier tier) {
        EVENTS.add(new RelationshipEvent(id, title, prompt, optA, optB, outA, outB, cA, cB, affA, affB, aA, aB, isCrazy, tier));
    }

    private static void registerAll() {
        // === GENERAL ROMANTIC & COUPLE EVENTS (45+) ===
        reg("flowers_demand", "Flower Emergency!", "Your partner stands before you with hands on hips demanding fresh flowers!",
            "Offer fresh poppies and compliments", "Say 'I forgot, my bad'",
            "Your partner beams with joy and hugs you!", "Your partner pouts and rolls their eyes.",
            -10, 15, 20, 5, AnimationType.HUG, AnimationType.POUTING, false, null);

        reg("anniversary_check", "Pop Quiz: Anniversary!", "Your partner suddenly asks: 'Do you remember what day we started dating?!'",
            "Of course! Every day with you is unforgettable!", "Uh... was it a Tuesday?",
            "Your partner blushes brightly and gives you a sweet kiss!", "Your partner shakes their head in dramatic disbelief!",
            -15, 25, 30, 0, AnimationType.MAKE_OUT, AnimationType.DRAMATIC_REJECTION, false, null);

        reg("pillow_war", "Pillow War Declared!", "A soft pillow suddenly flies past your head! A pillow war has begun!",
            "Engage in epic pillow combat!", "Surrender and offer cuddles",
            "Feathers fly everywhere as you both laugh hysterically!", "You both tumble onto the bed cuddling warmly.",
            5, -10, 25, 25, AnimationType.PILLOW_FIGHT_ATTACKER, AnimationType.CUDDLE, false, null);

        reg("ten_kisses", "Kiss Quota!", "Your partner demands an immediate tax of 10 rapid-fire kisses!",
            "Deliver a barrage of kisses!", "Offer one tiny cheek peck",
            "Exaggerated hearts shower you both in dizzy affection!", "Your partner crosses their arms and demands more effort!",
            -10, 10, 25, 10, AnimationType.KISS_ATTACK, AnimationType.ARMS_CROSSED, false, null);

        reg("stole_blanket", "Blanket Thief in the Night!", "At 2 AM, your partner rolls over, completely stealing all the blankets!",
            "Gently spoon and share the warmth", "Tug the blanket back aggressively!",
            "You cuddle close under the shared blanket, sleeping peacefully.", "A hilarious tug-of-war ensues, leaving both shivering!",
            -10, 15, 20, 5, AnimationType.SPOONING, AnimationType.SHIVERING, false, null);

        reg("villager_witness", "Caught by a Villager!", "A wandering librarian stares awkwardly at your romantic moment!",
            "Wink and blow the villager a kiss", "Jump apart in flustered embarrassment!",
            "The villager makes a satisfied 'HRRNGH' sound and nods in approval.", "You both blush intensely as record scratch sounds play!",
            0, 5, 15, 10, AnimationType.FLIRTING, AnimationType.JUMP_APART, false, null);

        reg("burnt_dinner", "Culinary Disaster!", "Your partner proudly serves you a piece of completely charred porkchop.",
            "Eat it happily: 'Best meal ever!'", "Tease: 'Did a Creeper cook this?'",
            "Your partner blushes warmly and rewards your loyalty!", "Your partner comically chases you with a frying pan!",
            -15, 20, 25, 5, AnimationType.COMFORT, AnimationType.BONK_ATTACKER, false, null);

        reg("dance_off", "Sudden Dance-Off!", "A jukebox starts playing Pigstep! Your partner challenges you to a dance-off!",
            "Bust out romantic slow dance moves", "Show off goofy wild dancing!",
            "You sweep your partner into a graceful romantic dance.", "You both end up on the floor laughing uncontrollably!",
            -5, -5, 30, 30, AnimationType.SLOW_DANCE, AnimationType.LAUGHING_HARD, false, null);

        reg("diamond_sharing", "The Sole Diamond!", "While mining, you find a single shiny diamond vein. Who gets it?",
            "Craft a ring and gift it to your partner", "Keep it to craft a diamond pickaxe",
            "Your partner gasps in delight and gives you a forehead kiss!", "Your partner gives you the legendary side-eye.",
            -20, 15, 40, 5, AnimationType.FOREHEAD_KISS, AnimationType.SIDE_EYE, false, null);

        reg("stargazing_night", "Shooting Star Wish!", "A clear night sky reveals falling stars above your cabin.",
            "Lie side-by-side and hold hands", "Point at the moon and say you love them to the moon and back",
            "You watch constellations together in cozy serenity.", "Your partner rests their head gently on your shoulder.",
            -10, -5, 30, 35, AnimationType.STARGAZING, AnimationType.HEAD_ON_SHOULDER, false, null);

        reg("tickle_ambush", "Sneak Tickle Attack!", "Your partner sneaks up behind you with wiggling fingers ready to strike!",
            "Tickle them right back!", "Feign fainting in dramatic defeat",
            "A tickle war erupts with joyful squeals and laughter!", "You dramatically faint onto the floor as they giggle lovingly.",
            0, -10, 25, 20, AnimationType.TICKLE_FIGHT, AnimationType.FAKE_FAINTING, false, null);

        reg("cold_feet", "Subzero Toes!", "Your partner slides ice-cold feet under your legs to warm them up!",
            "Warm their feet with a gentle laugh", "Yelp and jump across the bed!",
            "Your partner cuddles in with a contented sigh.", "Your partner laughs mischievously at your reaction!",
            -10, 5, 20, 15, AnimationType.CUDDLE, AnimationType.LAUGHING_HARD, false, null);

        reg("nether_scare", "Ghast Screech Panic!", "A distant Ghast wails! Your partner jumps into your arms in fear!",
            "Hold them in a protective bridal carry", "Say 'Relax, I've got this!' and face the Ghast",
            "You carry your partner safely while hearts protect you both.", "Your brave stance earns an impressed cheer!",
            -15, -5, 35, 25, AnimationType.BRIDAL_CARRY, AnimationType.HUG, false, null);

        reg("pet_naming", "The Puppy Dilemma!", "You tamed an adorable baby wolf together! What should you name it?",
            "Let your partner pick any cute name they want", "Suggest 'Sir Barkington the Third'",
            "Your partner hugs the puppy and showers you with love!", "Your partner chuckles at your goofy naming taste.",
            -10, 0, 20, 15, AnimationType.HUG, AnimationType.GIGGLE, false, null);

        reg("camp_smores", "Campfire Romance", "The wood crackles in the campfire as the sunset dips beneath the horizon.",
            "Roast sweet marshmallows and feed your partner", "Sing a silly campfire love song",
            "A sweet bite followed by a gentle nuzzle warms the evening.", "Your partner giggles and sings along with you.",
            -10, -5, 25, 25, AnimationType.NUZZLE, AnimationType.FAST_DANCE, false, null);

        reg("thunder_cuddle", "Thunderclap Startle!", "A massive flash of lightning cracks right over your roof!",
            "Wrap them in a tight reassuring embrace", "Jump under the blankets together",
            "You comfort each other as the storm rages outside.", "You shiver together under the blankets with sweet giggles.",
            -10, -10, 30, 30, AnimationType.COMFORT, AnimationType.CUDDLE_SLEEPING, false, null);

        reg("fishing_date", "The Big Catch!", "While fishing on the dock, your partner reels in an old leather boot.",
            "Put the boot on your head and salute!", "Hand over your enchanted fishing rod",
            "Your partner bursts out laughing at your goofy joke!", "Your partner feels touched by your generosity and kisses your cheek.",
            -5, -15, 20, 30, AnimationType.LAUGHING_HARD, AnimationType.CHEEK_KISS, false, null);

        reg("boat_trip", "Rowboat Cruise", "A tranquil lake boat ride with water lilies drifting by.",
            "Row gently and sing a serenading ballad", "Offer a surprise flower bouquet mid-lake",
            "Your partner gazes into your eyes with fond adoration.", "Petals scatter over the water as your partner gasps in delight!",
            -15, -20, 35, 40, AnimationType.LONG_HUG, AnimationType.MAKE_OUT, false, null);

        reg("breakfast_bed", "Morning Delight", "Golden sunlight streams through the window. Breakfast is served in bed!",
            "Serve sweet berries and freshly baked cookies", "Wake them up with gentle forehead kisses",
            "Your partner eats happily and declares you the best partner in Minecraft!", "Your partner wakes up smiling with soft morning blushes.",
            -20, -15, 35, 35, AnimationType.HUG, AnimationType.FOREHEAD_KISS, false, null);

        reg("armor_heavy", "Netherite Armor Hug", "Your partner is wearing bulky full Netherite and asks for a piggyback ride!",
            "Bravely carry them despite the immense weight!", "Say: 'Let me grab my forklift first!'",
            "You stagger under the heavy weight but triumphantly march on!", "Your partner playfully slaps your arm with a grin.",
            -5, 10, 20, 15, AnimationType.STRUGGLE_CARRY, AnimationType.SLAP_ATTACKER, false, null);

        reg("mud_splash", "Rain Puddle Prank!", "After a heavy rain, your partner splashes right into a giant mud puddle!",
            "Splash in together and laugh!", "Offer a towel and a gentle clean-up",
            "You both end up messy and joyful, shaking off water!", "Your partner appreciates your chivalrous care and kisses you.",
            -5, -15, 25, 30, AnimationType.WET_SHAKE, AnimationType.QUICK_KISS, false, null);

        reg("house_blueprint", "Home Renovations!", "Your partner wants to replace the stone floor with pink glazed terracotta.",
            "Agree immediately: 'Your aesthetic is perfection!'", "Debate: 'What about dark oak planks?'",
            "Your partner jumps with excitement and gives you hugs!", "A comical design argument begins over color swatches!",
            -15, 10, 30, 10, AnimationType.HUG, AnimationType.ARMS_CROSSED, false, null);

        reg("cave_lost", "Deep Dark Whispers", "You both took a wrong turn in a deep cave and Warden shrieks echo nearby!",
            "Hold hands tightly and sneak together", "Whisper sweet jokes to keep morale up",
            "Connected footsteps and silent hand-holding guide you to safety.", "Your partner giggles quietly, feeling safe by your side.",
            -10, -5, 35, 30, AnimationType.HAND_HOLDING, AnimationType.COMFORT, false, null);

        reg("secret_letter", "Hidden Love Note", "A crumpled parchment falls out of your partner's chest with your name on it.",
            "Read it aloud romantically with flair", "Keep it in your inventory as a precious keepsake",
            "Your partner covers their face blushing in delightful embarrassment!", "A warm knowing look passes between you both.",
            -10, -15, 25, 35, AnimationType.BLUSHING, AnimationType.BACK_HUG, false, null);

        reg("cookie_tax", "The Cookie Toll!", "You just baked 8 chocolate chip cookies. Your partner demands half as tax!",
            "Hand over the entire stack with a smile", "Feed them the cookies one by one",
            "Your partner squeals in victory and munches happily!", "Sweet crumbs and sweeter kisses follow each bite.",
            -15, -20, 30, 40, AnimationType.HUG, AnimationType.MAKE_OUT, false, null);

        reg("witch_potion", "Mystery Brew!", "You brewed an experimental pink potion. Who tests it first?",
            "Drink it together on three!", "Let your partner pour it on both of you",
            "Glowing heart particles bubble around you both in a goofy pink aura!", "You both float with silly walking strides and laughing smiles.",
            -5, -5, 25, 25, AnimationType.LAUGHING_HARD, AnimationType.FAST_DANCE, false, null);

        reg("sleepy_head", "Sleepy Head Droop", "During a long minecart ride, your partner dozes off onto your shoulder.",
            "Rest your head on theirs and let them sleep", "Wrap your jacket around them gently",
            "The rhythmic rails click by in cozy harmony.", "Your partner stirs awake later, thanking you with a warm snuggle.",
            -15, -15, 35, 35, AnimationType.HEAD_ON_SHOULDER, AnimationType.CUDDLE, false, null);

        reg("reconciliation_talk", "After the Squabble", "After a silly disagreement over who lost the diamond sword, silence falls.",
            "Offer a gentle reconciliation hug and apologize", "Present a fresh flower bouquet silently",
            "The tension melts as your partner melts into your arms.", "Your partner smiles softly and whispers 'I forgive you'.",
            -25, -20, 45, 40, AnimationType.RECONCILIATION_HUG, AnimationType.LONG_HUG, false, null);

        // === CRAZY GIRLFRIEND TIER EVENTS (30+) ===
        // --- TIER: SUSPICIOUS (25-49) ---
        reg("suspicious_sunset", "Where Were You At Sunset?", "Your partner narrows their eyes: 'You said you were mining, but your pickaxe has no wear!'",
            "Show the shiny red roses you gathered for them!", "Sweat nervously and change the subject",
            "Suspicion immediately turns into a radiant, forgiving smile!", "Your partner gives you an exaggerated, skeptical side-eye.",
            -20, 15, 25, 5, AnimationType.HUG, AnimationType.SIDE_EYE, true, CrazyTier.SUSPICIOUS);

        reg("suspicious_trader", "Wandering Trader Secret?", "Your partner spotted you trading with a wandering trader: 'What did you buy?!'",
            "Reveal a surprise nautilus shell for a couple conduit!", "Mutter: 'Just some sea pickles...'",
            "Your partner gasps: 'For our ocean base?!' and hugs you tight!", "Your partner taps their foot suspiciously.",
            -15, 10, 25, 10, AnimationType.LONG_HUG, AnimationType.FOOT_STOMPING, true, CrazyTier.SUSPICIOUS);

        reg("suspicious_enderchest", "Locked Vault Mystery", "Your partner notices you locked your Ender Chest with private items inside!",
            "Unlock it to reveal a chest full of named love notes!", "Say: 'A person needs their privacy!'",
            "Your partner blushes to the ears reading the heartfelt messages.", "Your partner crosses their arms and raises an eyebrow.",
            -25, 20, 40, 0, AnimationType.BLUSHING, AnimationType.ARMS_CROSSED, true, CrazyTier.SUSPICIOUS);

        reg("suspicious_look", "Who Were You Looking At?", "Your partner catches you glancing at a villager librarian's enchanted book.",
            "Say: 'I was looking for Mending to protect your armor!'", "Shrug: 'Just browsing books...'",
            "Your partner melts: 'Aww, you care about my gear!'", "Your partner rolls their eyes dramatically.",
            -15, 10, 20, 5, AnimationType.CHEEK_KISS, AnimationType.ROLLING_EYES, true, CrazyTier.SUSPICIOUS);

        reg("suspicious_trail", "Midnight Footprints", "Your partner tracked sand footprints leading from the desert back to your base.",
            "Hand over a cactus flower you hiked 1,000 blocks to find!", "Claim it was an Enderman wandering around",
            "Your partner holds the rare desert blossom in joyful shock!", "Your partner looks completely unconvinced.",
            -20, 15, 30, 5, AnimationType.SURPRISE_REACTION, AnimationType.SIDE_EYE, true, CrazyTier.SUSPICIOUS);

        reg("suspicious_armor_polish", "Too Dressed Up!", "Your partner asks: 'Why are you wearing your best polished armor for farming potatoes?!'",
            "Reply: 'Because I wanted to look handsome for you!'", "Mutter: 'Just felt like wearing it...'",
            "Your partner giggles and gives you an affectionate forehead kiss.", "Your partner checks your pockets for suspicious wheat.",
            -15, 10, 25, 5, AnimationType.FOREHEAD_KISS, AnimationType.SIDE_EYE, true, CrazyTier.SUSPICIOUS);

        // --- TIER: DRAMA QUEEN (50-74) ---
        reg("drama_slipper_throw", "Incoming Slipper!", "Your partner pulls off a fluffy pink slipper and brandishes it with deadly aim!",
            "Catch the slipper mid-air and kiss their hand!", "Duck behind a crafting table in terror!",
            "Your partner pauses, flustered by your smooth reflexes, and giggles.", "The slipper ricochets off the wall with a comical BONK!",
            -20, 10, 30, 10, AnimationType.QUICK_KISS, AnimationType.SLAP_VICTIM, true, CrazyTier.DRAMA_QUEEN);

        reg("drama_hair_unnoticed", "You Didn't Notice My Hair!", "Your partner changed their helmet and haircut: 'Do you notice NOTHING about me?!'",
            "Exclaim: 'You look breathtaking! I was stunned into silence!'", "Blink: 'Did you change your boots?'",
            "Your partner wipes away dramatic tears and beams with pride.", "Your partner dramatically falls to their knees in tragic despair!",
            -25, 25, 35, 0, AnimationType.HUG, AnimationType.FALL_DESPAIR, true, CrazyTier.DRAMA_QUEEN);

        reg("drama_dog_priority", "The Dog Over Me?!", "Your partner pouts: 'You patted our pet dog before greeting me when you came home!'",
            "Scoop them up in a giant princess carry!", "Explain: 'The dog barked first!'",
            "Your partner squeals as you carry them, forgetting all jealousy.", "Your partner performs an exaggerated weeping monologue.",
            -20, 20, 30, 5, AnimationType.BRIDAL_CARRY, AnimationType.DRAMATIC_CRYING, true, CrazyTier.DRAMA_QUEEN);

        reg("drama_silent_treatment", "The 5-Minute Wall of Silence", "Your partner crosses their arms and stares at the wall, refusing to speak.",
            "Wrap arms around them from behind and whisper sweet apologies", "Try waving a golden apple in front of their eyes",
            "Your partner leans back into the hug, letting out a soft forgiving sigh.", "Your partner swats the apple away and pouts even harder.",
            -25, 15, 35, 5, AnimationType.BACK_HUG, AnimationType.POUTING, true, CrazyTier.DRAMA_QUEEN);

        reg("drama_lava_threat", "Ring Over The Lava Pit!", "Your partner hovers a gold nugget over a bubbling lava pool: 'Do you still love me?!'",
            "Leap forward and swear your eternal love with all your heart!", "Yell: 'Careful, gold is expensive!'",
            "Your partner safely pockets the nugget and bursts into happy tears.", "Your partner dramatically drops a potato into the lava with a hiss!",
            -30, 25, 40, 0, AnimationType.COMFORT, AnimationType.RING_THROW, true, CrazyTier.DRAMA_QUEEN);

        reg("drama_stomping_garden", "Carrot Patch Tantrum!", "Your partner stomps furiously across the carrot field: 'I asked for yellow tulips, not red!'",
            "Immediately harvest a yellow tulip and bow like a courtier", "Complain: 'Hey, watch my crops!'",
            "Your partner takes the yellow blossom and forgives your royal blunder.", "Your partner kicks an extra carrot into orbit!",
            -20, 20, 25, 5, AnimationType.HUG, AnimationType.FOOT_STOMPING, true, CrazyTier.DRAMA_QUEEN);

        reg("drama_fainting_couch", "Swoon of Tragedy!", "After hearing you mined iron instead of gold, your partner collapses onto the couch!",
            "Rush with smelling salts (or sweet cookies) to revive them!", "Applaud the Shakespearean acting performance",
            "Your partner sits up, grabs the cookies, and laughs fondly.", "Your partner peeks with one eye open and throws a pillow!",
            -20, 15, 30, 10, AnimationType.CUDDLE, AnimationType.FAKE_FAINTING, true, CrazyTier.DRAMA_QUEEN);

        // --- TIER: FURIOUS (75-94) ---
        reg("furious_thundercloud", "Thundercloud Tantrum!", "Dark storm clouds metaphorically hover over your partner's head! Pure fury!",
            "Brave the storm, step close, and pull them into a tight embrace", "Run outside into the rain and wait it out",
            "The storm breaks into soft sunshine and heartfelt tears of relief.", "Lightning cracks as your partner shakes angry fists at the sky!",
            -35, 20, 45, 0, AnimationType.RECONCILIATION_HUG, AnimationType.ANGRY_JUMPING, true, CrazyTier.FURIOUS);

        reg("furious_chase", "Kitchen Pan Pursuit!", "Your partner grabs a cast iron frying pan and chases you around the dining table!",
            "Stop, turn around, and catch them in a gentle bear hug!", "Vault over the counter yelling 'I surrender!'",
            "The frying pan drops with a clang as they are enveloped in warm hugs.", "You take a comical BONK to the helmet with spinning cartoon stars!",
            -30, 15, 40, 10, AnimationType.LONG_HUG, AnimationType.BONK_VICTIM, true, CrazyTier.FURIOUS);

        reg("furious_door_lockout", "The Iron Door Slam!", "CLANG! The iron door slams shut and the lever is flicked from inside!",
            "Slip a sweet handwritten love letter under the door gap", "Stand outside in the rain looking like a sad puppy",
            "The door slowly clicks open and two arms pull you inside warmly.", "Your partner opens the window just to make an angry face and close it!",
            -35, 15, 45, 5, AnimationType.HUG, AnimationType.ARMS_CROSSED, true, CrazyTier.FURIOUS);

        reg("furious_bed_eviction", "Couch Sentence!", "Your partner points firmly at the living room couch: 'You sleep THERE tonight!'",
            "Bring your partner warm tea and an honest sincere apology", "Comply sadly and wrap yourself in a lonely small rug",
            "Your partner relents with a soft sigh: 'Come back to bed, silly.'", "You spend the night shivering on the couch while snoring echoes.",
            -30, 20, 40, 5, AnimationType.CUDDLE, AnimationType.SHIVERING, true, CrazyTier.FURIOUS);

        reg("furious_tantrum", "Full Cartoon Meltdown!", "Your partner jumps up and down flailing arms in an epic cartoon tantrum!",
            "Scoop them up off their feet so they can't jump anymore!", "Stand back and wait for the energy bar to deplete",
            "Your partner giggles out of breath, all anger completely forgotten.", "Your partner stomps one final dramatic stomp and pouts.",
            -30, 15, 35, 10, AnimationType.BRIDAL_CARRY, AnimationType.TANTRUM, true, CrazyTier.FURIOUS);

        reg("furious_obsidian_mine", "Angry Pickaxe Hacking!", "Your partner furiously hacks away at an obsidian block with sheer rage!",
            "Gently place your hands over theirs and take the pickaxe", "Hand them a diamond pickaxe to speed up the process",
            "Your partner collapses against your chest, breathing softly.", "The obsidian breaks and they look around wondering what just happened.",
            -25, 10, 35, 15, AnimationType.COMFORT, AnimationType.HUG, true, CrazyTier.FURIOUS);

        // --- TIER: TOTAL CHAOS (95-100) ---
        reg("chaos_slapstick_whirl", "Total Slapstick Whirlwind!", "The Crazy Meter hits 100! Complete cartoon slapstick madness erupts in the house!",
            "Initiate the Emergency Romantic Counter-Attack: 100 Hugs!", "Duck under the table while slippers and pillows fly!",
            "The chaos implodes into a pile of giggling couple cuddles on the floor!", "Furniture shakes as cartoon stars, feathers, and slippers bounce around!",
            -50, 0, 50, 25, AnimationType.CUDDLE, AnimationType.LAUGHING_HARD, true, CrazyTier.TOTAL_CHAOS);

        reg("chaos_pan_duel", "Frying Pan Clang-Off!", "Your partner tosses you a second frying pan: 'DEFEND YOURSELF, DARLING!'",
            "Clang pans rhythmically together like cymbals in a funny song!", "Try to parry the incoming comedic BONK!",
            "You turn the brawl into a hilarious kitchen musical duet!", "CLANG! Double knockback sends you both flying onto the carpet laughing!",
            -45, 10, 45, 30, AnimationType.FAST_DANCE, AnimationType.BONK_VICTIM, true, CrazyTier.TOTAL_CHAOS);

        reg("chaos_balloon_ambush", "Water Balloon Tsunami!", "Your partner reveals a chest stocked with 64 water balloons! AMBUSH!",
            "Charge forward and tackle them into a giant wet hug!", "Throw balloons back in an all-out water war!",
            "Soaked in water, you both slip and fall into a fit of laughter.", "The room turns into an indoor pool as rainbow splashes fly!",
            -40, 5, 40, 35, AnimationType.WET_SHAKE, AnimationType.PLAYFUL_WRESTLE, true, CrazyTier.TOTAL_CHAOS);

        reg("chaos_apology_roulette", "The Ultimate Apology Trial!", "Your partner sets up a courtroom podium: 'State your defense before the tribunal!'",
            "Recite a ridiculously dramatic, over-the-top Shakespearean love speech!", "Offer a chest full of shiny emeralds and diamonds as tribute",
            "The tribunal dissolves into tears of laughter: 'CASE DISMISSED WITH KISSES!'", "The tribunal accepts the bribe: 'You may live another day!'",
            -55, -20, 50, 30, AnimationType.MAKE_OUT, AnimationType.CHEEK_KISS, true, CrazyTier.TOTAL_CHAOS);

        reg("chaos_love_potion_tsunami", "Love Potion Overdose!", "Your partner splashes a giant splash potion of Love onto the floor!",
            "Embrace the pink heart storm and spin your partner in the air!", "Try to run as your legs do goofy wobbles!",
            "You spin in a dazzling vortex of hearts, resetting the meter completely!", "You both wobble around like dizzy ducks quacking with laughter.",
            -60, -30, 60, 40, AnimationType.PRINCESS_KISS, AnimationType.DIZZY, true, CrazyTier.TOTAL_CHAOS);

        reg("chaos_fort_standoff", "The Pillow Fort Alamo!", "Your partner built a pillow fort barricade and is hurling slippers through the slits!",
            "Wave a white napkin on a stick and bring warm cookies as a peace treaty", "Storm the fortress with feather tickle weapons!",
            "The treaty is signed! The fortress becomes an intimate couple cuddle cave.", "The fort collapses into a giant pile of pillows with you both inside!",
            -45, -15, 45, 35, AnimationType.CUDDLE, AnimationType.PILLOW_FIGHT_VICTIM, true, CrazyTier.TOTAL_CHAOS);

        // --- ADDITIONAL EVENTS ---
        reg("couple_netflix_chill", "Minecraft & Chill", "You both sit on the couch deciding what to build tonight.",
            "Suggest building an enormous pink castle", "Suggest going into the deep dark caves",
            "A 10-hour castle building spree begins!", "You both scream and run from wardens holding hands!",
            -10, 5, 25, 20, AnimationType.CUDDLE, AnimationType.BACKING_AWAY, false, null);

        reg("couple_lost_diamonds", "Who Stole the Diamonds?", "Your partner discovers 3 diamonds missing from the vault!",
            "Confess you made a Diamond Engagement Ring with them", "Blame the neighborhood wandering trader",
            "Your partner gasps with joy and hugs you tightly!", "The wandering trader receives suspicious glares all afternoon.",
            -20, 10, 35, 15, AnimationType.PRINCESS_KISS, AnimationType.SIDE_EYE, false, null);

        reg("couple_cat_argument", "Name the Kitten!", "You tamed a tiny ginger kitten, but disagree on the name!",
            "Name it after your partner's favorite food", "Name it Lord Fluffington the Third",
            "Your partner melts: Aww, that's adorable!", "The cat meows royally and demands tuna.",
            -10, -5, 20, 15, AnimationType.NUZZLE, AnimationType.LAUGHING_HARD, false, null);

        reg("couple_burnt_porkchop", "Dinner Disaster!", "Smoke billows from the furnace! Dinner has been thoroughly incinerated.",
            "Order takeout by catching fresh salmon at the dock", "Pretend it is blackened gourmet steak and eat it anyway",
            "You both have a fun seaside sushi date!", "You crunch into coal and share an amused chuckle.",
            -15, 0, 30, 20, AnimationType.SIT_TOGETHER, AnimationType.LAUGHING_HARD, false, null);

        reg("couple_creeper_jump", "Creeper Jumpscare!", "A hissing creeper appears behind you while building!",
            "Dramatically push your partner behind you and raise your shield", "Both jump and scream synchronously before leaping into the moat",
            "Your partner blushes at your heroics!", "You both splash into the water soaking wet and laughing!",
            -20, 0, 40, 25, AnimationType.BRIDAL_CARRY, AnimationType.WET_SHAKE, false, null);

        reg("couple_boat_adventure", "Waterfall Rapids!", "Your shared wooden boat is heading straight for a 30-block waterfall drop!",
            "Hold on tight and yell WHEEEEE together", "Row furiously backward with all your might",
            "You survive the splashdown and cheer in triumph!", "You barely halt on the ledge, panting with excitement.",
            -15, 10, 30, 20, AnimationType.FAST_DANCE, AnimationType.HUG, false, null);

        reg("couple_stolen_blanket_night", "The Midnight Blanket War", "It is 3 AM in game, and your partner has rolled up all the blankets like a burrito!",
            "Gently tuck yourself into the burrito beside them", "Tickle their feet until they roll over",
            "You share the warm burrito embrace snugly.", "They giggle and kick the blanket open for you.",
            -15, 5, 30, 25, AnimationType.CUDDLE_SLEEPING, AnimationType.TICKLE_FIGHT, false, null);

        reg("couple_hoodie_theft", "The Stolen Hoodie Mystery", "Your favorite hoodie mysteriously appeared on your partner avatar!",
            "Tell them: It looks much better on you anyway", "Try to playfully swap clothes while they giggle",
            "Your partner blushes deeply and hugs you tight.", "A goofy wardrobe struggle ensues with happy laughter.",
            -15, 0, 35, 25, AnimationType.FOREHEAD_KISS, AnimationType.PLAYFUL_WRESTLE, false, null);

        reg("couple_bed_bounce", "Late Night Trampoline", "Your partner is bouncing repeatedly on the double bed singing goofy songs!",
            "Climb up and join the synchronized bouncing routine", "Gently sweep them into a bridal carry off the bed",
            "You bounce higher and higher like acrobats!", "You catch them mid-air into an affectionate embrace.",
            -15, -10, 30, 35, AnimationType.ANGRY_JUMPING, AnimationType.BRIDAL_CARRY, false, null);

        reg("couple_snore_symphony", "Log Sawing Champion", "Your partner fell asleep in-game and their character is snoring rhythmically!",
            "Cover them with a fluffy pillow and whisper sweet dreams", "Record their snore rhythm as a new disc remix track",
            "They smile softly in their sleep and roll closer.", "You hum along to the funny little snore beats.",
            -10, 0, 25, 20, AnimationType.HEAD_ON_SHOULDER, AnimationType.LAUGHING_HARD, false, null);

        reg("couple_piggyback_express", "Lazy Feet Express", "Your partner stops walking: My legs ran out of hunger bars. Carry me!",
            "Offer your back cheerfully: Hop on, VIP rider!", "Offer to feed them a golden carrot instead",
            "They ride comfortably on your back while you explore.", "They chew happily and jog beside you holding hands.",
            -15, -5, 30, 25, AnimationType.PIGGYBACK, AnimationType.HAND_HOLDING, false, null);

        reg("couple_rain_stroll", "Singing in the Rain", "A sudden gentle rainstorm washes over your cherry grove base.",
            "Take their hand and twirl them under the pink cherry petals", "Open a makeshift oak trapdoor umbrella over both your heads",
            "You share a breathtaking movie-worthy rain dance!", "You stand close together under the tiny roof, eyes locked.",
            -25, -10, 45, 35, AnimationType.SLOW_DANCE, AnimationType.NUZZLE, false, null);

        reg("couple_midnight_snack", "The 2 AM Cake Raid", "You catch your partner sneaking a slice of cake from the larder at midnight!",
            "Sneak up behind them and take a bite from the other side", "Dab a little frosting on their nose playfully",
            "You share the cake slice bite by bite with soft giggles.", "They look surprised with frosting on their nose and boop yours back!",
            -15, -5, 30, 30, AnimationType.KISS, AnimationType.FLIRTING, false, null);

        reg("couple_flower_crown", "Crowning the Monarch", "Your partner has gathered 9 red and pink blossoms into a delicate crown.",
            "Bow down gracefully to let them place it on your head", "Craft a matching crown and place it on their head too",
            "They beam with pride: My royal beloved!", "You are crowned the Modded King & Queen of the realm!",
            -20, -10, 40, 45, AnimationType.FOREHEAD_KISS, AnimationType.PRINCESS_KISS, false, null);

        reg("couple_villager_gossip", "Village Rumor Mill", "A local librarian villager claims you two are the most sickeningly sweet couple in history.",
            "Give the librarian an emerald tip: You got that right!", "Slap the librarian playfully with a flower bouquet",
            "The villager emits happy green sparkles and nods approvingly.", "The librarian sneezes on petals while you both laugh.",
            -15, 0, 30, 20, AnimationType.HAND_HOLDING, AnimationType.LAUGHING_HARD, false, null);

        reg("couple_elytra_glide", "Soaring Across the Clouds", "You both dive off a mountain peak using elytra gliders together!",
            "Glide right next to each other matching every banking curve", "Do a loop-de-loop around your partner mid-flight",
            "You soar in majestic symmetry beneath the sunset clouds.", "Your partner cheers as you loop around them with pink fireworks!",
            -25, -10, 45, 35, AnimationType.SLOW_DANCE, AnimationType.FAST_DANCE, false, null);

        reg("couple_secret_notes", "Hidden Love Letter in Chest", "You open your tool chest and find a handwritten note: I love you more than netherite!",
            "Write an even sweeter note and hide it in their armor locker", "Run over immediately and squeeze them from behind",
            "A secret pen-pal romance blossoms in your shared storage room.", "They gasp happily as your arms wrap around them warmly.",
            -20, -15, 40, 40, AnimationType.BACK_HUG, AnimationType.HUG, false, null);

        reg("couple_fishing_sunset", "Old Married Couple Vibes", "You both sit on a wooden dock with fishing rods at sunset, feet dangling.",
            "Rest your head against their shoulder while the bobbers drift", "Compete for who catches the biggest fish first",
            "The twilight sky reflects on the water as you sit peacefully.", "You pull up a clownfish and tease each other playfully.",
            -25, -5, 40, 25, AnimationType.HEAD_ON_SHOULDER, AnimationType.LAUGHING_HARD, false, null);

        reg("suspicious_secret_vault", "The Locked Iron Door!", "Your partner noticed a new iron door with a password lock in your basement!",
            "Open the door to reveal a secret candlelit spa built just for them", "Say it is a top-secret surprise for tomorrow and wink",
            "Their eyes sparkle with wonder: You built this for ME?!", "They cross their arms with a curious grin: I will be waiting!",
            -25, 10, 40, 20, AnimationType.NUZZLE, AnimationType.ARMS_CROSSED, true, CrazyTier.SUSPICIOUS);

        reg("suspicious_wandering_trader_gift", "Suspicious Flower Purchase!", "Your partner spots you trading emeralds with a wandering trader for orchids!",
            "Present the orchids: I saw these and instantly thought of you", "Claim you were buying leashes to restrain them later",
            "Their suspicion evaporates into a warm loving smile.", "A playful giggle breaks out: Oh really now, is that so?",
            -20, 5, 35, 25, AnimationType.FLIRTING, AnimationType.FLIRTING, true, CrazyTier.SUSPICIOUS);

        reg("drama_diamond_hoard", "You Built What Out of Diamonds?!", "Your partner finds out you crafted a diamond hoe instead of a ring upgrade!",
            "Promise to farm a 1000-crop heart garden with it", "Quickly craft the Diamond Ring right in front of them",
            "They laugh in disbelief at your dedication.", "They gasp with joy and show off their new shiny ring!",
            -25, -15, 35, 45, AnimationType.LAUGHING_HARD, AnimationType.PRINCESS_KISS, true, CrazyTier.DRAMA_QUEEN);

        reg("drama_telenovela_slap", "The Ultimate Melodrama Rehearsal!", "Your partner drops their cup and gasps dramatically: How COULD you?!",
            "Drop to your knees in theatrical agony: Forgive me, mi amor!", "Step forward and silence them with a passionate cinematic kiss",
            "The living room transforms into a hilarious Spanish soap opera!", "You sweep them back into a dramatic movie make-out pose!",
            -30, -35, 40, 50, AnimationType.FALL_DESPAIR, AnimationType.MAKE_OUT, true, CrazyTier.DRAMA_QUEEN);

        reg("furious_fire_charge", "Literal Sparks Flying!", "Your partner Crazy Meter is raging! They shoot a fire charge into the air in fury!",
            "Bravely extinguish the embers and envelop them in a warm embrace", "Offer a cold bucket of water: To cool down my fiery queen?",
            "The fire subsides as they cling to your jacket, breathing softly.", "They splash the water on you playfully: Now we are both cool!",
            -35, 5, 45, 25, AnimationType.HUG, AnimationType.WET_SHAKE, true, CrazyTier.FURIOUS);

        reg("furious_cookie_crush", "The Cookie Crushing Rampage!", "Your partner furiously crushes baked cookies on the kitchen counter!",
            "Feed them a fresh strawberry chocolate instead", "Brush cookie crumbs off their cheek with gentle thumbs",
            "The sweetness melts their anger immediately into a sheepish pout.", "Their pout turns into a cute blush as they lean into your hand.",
            -40, -10, 50, 40, AnimationType.POUTING, AnimationType.NUZZLE, true, CrazyTier.FURIOUS);

        reg("chaos_bed_ejection", "Super Spring Bed Launch!", "Your partner installed piston springs under the bed and launches you to the ceiling!",
            "Stick the landing on top of the wardrobe and take a theatrical bow", "Tackle them onto the mattress into a tickle counter-offensive!",
            "You pose like an olympic gymnast as applause echoes!", "The bed becomes a battleground of squirming laughter and squeals!",
            -50, -20, 50, 45, AnimationType.LAUGHING_HARD, AnimationType.TICKLE_FIGHT, true, CrazyTier.TOTAL_CHAOS);

        reg("chaos_confetti_canon", "TNT Confetti Cannon!", "Your partner lights a cannon loaded with flower petals, fireworks, and confetti!",
            "Run under the rainbow petal blast with open arms!", "Pull your partner with you into the middle of the shower!",
            "The entire room bursts into a magnificent wonderland of colors!", "You both stand drenched in pink rose petals cheering wildly!",
            -55, -25, 60, 50, AnimationType.FAST_DANCE, AnimationType.FAST_DANCE, true, CrazyTier.TOTAL_CHAOS);

        reg("chaos_final_surrender", "Unconditional Surrender of Love!", "At the apex of chaos, your partner drops their weapons: I surrender... to your kisses!",
            "Accept the surrender with a cascade of 50 kisses!", "Offer a golden trophy: Best and Craziest Partner in Minecraft!",
            "Kisses shower everywhere until they are completely dizzy and happy!", "They wear the trophy like a royal crown with unbounded pride!",
            -70, -40, 75, 60, AnimationType.KISS_ATTACK, AnimationType.DIZZY, true, CrazyTier.TOTAL_CHAOS);
    }
}
