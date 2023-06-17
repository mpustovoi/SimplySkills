package net.sweenus.simplyskills.util;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.puffish.skillsmod.SkillsAPI;
import net.spell_engine.SpellEngineMod;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellCast;
import net.spell_engine.internals.SpellHelper;
import net.spell_power.api.MagicSchool;
import net.spell_power.api.SpellPower;
import net.sweenus.simplyskills.network.KeybindPacket;

import java.util.ArrayList;
import java.util.List;

public class Abilities {


    //Ability methods for our Mixins


    public static void passiveInitiateNullification(PlayerEntity player) {
        if (player.age % 80 == 0) {
            int radius = 12;

            Box box = new Box(player.getX() + radius, player.getY() + (float) radius / 3, player.getZ() + radius,
                    player.getX() - radius, player.getY() - (float) radius / 3, player.getZ() - radius);
            for (Entity entities : player.world.getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {

                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                        for (StatusEffectInstance statusEffect : le.getStatusEffects()) {
                            if (statusEffect != null && statusEffect.getEffectType().isBeneficial()) {
                                le.removeStatusEffect(statusEffect.getEffectType());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void passiveAreaCleanse(PlayerEntity player) {
        if (player.age % 80 == 0) {
            int radius = 12;

            Box box = new Box(player.getX() + radius, player.getY() + (float) radius / 3, player.getZ() + radius,
                    player.getX() - radius, player.getY() - (float) radius / 3, player.getZ() - radius);
            for (Entity entities : player.world.getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {

                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && !HelperMethods.checkFriendlyFire(le, player)) {
                        for (StatusEffectInstance statusEffect : le.getStatusEffects()) {
                            if (statusEffect != null && !statusEffect.getEffectType().isBeneficial()) {
                                le.removeStatusEffect(statusEffect.getEffectType());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void passiveSelfCleanse(PlayerEntity player) {
        if (player.age % 120 == 0) {
            for (StatusEffectInstance statusEffect : player.getStatusEffects()) {
                if (statusEffect != null && !statusEffect.getEffectType().isBeneficial()) {
                    player.removeStatusEffect(statusEffect.getEffectType());
                    break;
                }
            }
        }
    }

    public static void passiveRangerReveal(PlayerEntity player) {
        if (player.age % 80 == 0) {
            int radius = 12;

            Box box = new Box(player.getX() + radius, player.getY() + (float) radius / 3, player.getZ() + radius,
                    player.getX() - radius, player.getY() - (float) radius / 3, player.getZ() - radius);
            for (Entity entities : player.world.getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {

                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                        for (StatusEffectInstance statusEffect : le.getStatusEffects()) {
                            if (statusEffect != null && statusEffect.getEffectType().equals(StatusEffects.INVISIBILITY)) {
                                le.removeStatusEffect(statusEffect.getEffectType());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void passiveRogueBackstab(Entity target, PlayerEntity player) {
        if (target instanceof LivingEntity livingTarget) {
            if (livingTarget.getBodyYaw() < (player.getBodyYaw() + 32) &&
                    livingTarget.getBodyYaw() > (player.getBodyYaw() - 32)) {
                livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60));
            }
        }
    }

    public static void passiveWarriorArmorMastery(PlayerEntity player) {
        if (player.getRandom().nextInt(100) < 25) {
            if (player.getArmor() > 9 && SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                    "combat").get().contains(SkillReferencePosition.warriorHeavyArmorMastery)) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 100));
            } else if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                    "combat").get().contains(SkillReferencePosition.warriorMediumArmorMastery)){
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100));
            }
        }
    }

    public static void passiveBerserkerSwordMastery(PlayerEntity player) {
        if (player.age % 20 == 0) {
            if (player.getMainHandStack() != null) {
                if (player.getMainHandStack().getItem() instanceof SwordItem) {
                    int mastery = 0;

                    if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                            "combat").get().contains(SkillReferencePosition.berserkerSwordMasterySkilled))
                        mastery = 2;
                    else if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                            "combat").get().contains(SkillReferencePosition.berserkerSwordMasteryProficient))
                        mastery = 1;

                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 25, mastery));
                }
            }
        }
    }

    public static void passiveBerserkerAxeMastery(PlayerEntity player) {
        if (player.age % 20 == 0) {
            if (player.getMainHandStack() != null) {
                if (player.getMainHandStack().getItem() instanceof AxeItem) {

                    int mastery = 0;

                    if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                            "combat").get().contains(SkillReferencePosition.berserkerAxeMasterySkilled))
                        mastery = 2;
                    else if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                            "combat").get().contains(SkillReferencePosition.berserkerAxeMasteryProficient))
                        mastery = 1;

                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 25, mastery));
                }
            }
        }
    }

    public static void passiveBerserkerIgnorePain(PlayerEntity player) {
        if (player.age % 20 == 0) {
            int resistanceStacks = 0;
            if (player.getHealth() <= (0.4 * player.getMaxHealth())) {

                if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                        "combat").get().contains(SkillReferencePosition.berserkerIgnorePainSkilled))
                    resistanceStacks = 2;
                else if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                        "combat").get().contains(SkillReferencePosition.berserkerIgnorePainProficient))
                    resistanceStacks = 1;

                player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 25, resistanceStacks));
            }
        }
    }

    public static void passiveBerserkerRecklessness(PlayerEntity player) {
        if (player.age % 20 == 0) {
            if (player.getHealth() >= (0.7 * player.getMaxHealth())) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 25));
            }
        }
    }

    public static void passiveBerserkerChallenge(PlayerEntity player) {
        if (player.age % 20 == 0) {
            int radius = 2;

            Box box = new Box(player.getX() + radius, player.getY() + radius, player.getZ() + radius,
                    player.getX() - radius, player.getY() - radius, player.getZ() - radius);
            for (Entity entities : player.world.getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {

                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                        if (player.hasStatusEffect(StatusEffects.HASTE)) {
                            int amplify = (player.getStatusEffect(StatusEffects.HASTE).getAmplifier() + 1);
                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, amplify));
                        } else {
                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE));
                        }
                    }
                }
            }
        }
    }

    public static void passiveBulwarkShieldMastery(PlayerEntity player) {
        if (player.age % 20 == 0) {
            if (player.getOffHandStack() != null) {
                if (player.getOffHandStack().getItem() instanceof ShieldItem) {

                    int mastery = 0;

                    if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                            "combat").get().contains(SkillReferencePosition.bulwarkShieldMasterySkilled))
                        mastery = 2;
                    else if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                            "combat").get().contains(SkillReferencePosition.bulwarkShieldMasteryProficient))
                        mastery = 1;

                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 25, mastery));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 25));
                }
            }
        }
    }

    public static void passiveBulwarkRebuke(PlayerEntity player, LivingEntity attacker) {
        if (player.getRandom().nextInt(100) < 25) {
            attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80));
        }
    }

    public static void passiveWayfarerSlender(PlayerEntity player) {
        if (player.age % 20 == 0) {
            if (player.getArmor() > 14 && SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                    "combat").get().contains(SkillReferencePosition.wayfarerSlender)){
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 25));
            }
            if (player.getArmor() > 9 && (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                    "combat").get().contains(SkillReferencePosition.initiateFrail))){
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 25));
            }
        }
    }

    public static void passiveRogueSmokeBomb(PlayerEntity player) {
        if (player.getRandom().nextInt(100) < 25) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 100));
            Box box = HelperMethods.createBox((LivingEntity) player, 6);
            for (Entity entities : player.world.getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {
                if (entities != null) {
                    if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {

                        le.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60));

                    }
                }
            }
        }
    }

    public static void passiveRogueEvasionMastery(PlayerEntity player) {

        int mastery = 15;

        if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                "combat").get().contains(SkillReferencePosition.rogueEvasionMasterySkilled))
            mastery = 35;
        else if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                "combat").get().contains(SkillReferencePosition.rogueEvasionMasteryProficient))
            mastery = 25;

        if (player.getRandom().nextInt(100) < mastery) {
            if (player.getEquippedStack(EquipmentSlot.HEAD).isEmpty()
                    && player.getEquippedStack(EquipmentSlot.CHEST).isEmpty()
                    && player.getEquippedStack(EquipmentSlot.LEGS).isEmpty()
                    && player.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {

                player.timeUntilRegen = 15;
                player.world.playSoundFromEntity(null, player, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME,
                        SoundCategory.PLAYERS, 1, 1);


            }
        }
    }

    public static void passiveRogueOpportunisticMastery(Entity target, PlayerEntity player) {

        int mastery = 40;

        if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                "combat").get().contains(SkillReferencePosition.rogueOpportunisticMasterySkilled))
            mastery = 120;
        else if (SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                "combat").get().contains(SkillReferencePosition.rogueOpportunisticMasteryProficient))
            mastery = 80;

        if ((target instanceof LivingEntity livingTarget) && player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
            livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, mastery));
            player.removeStatusEffect(StatusEffects.INVISIBILITY);
        }

    }

    public static void passiveInitiateFrail(PlayerEntity player) {
        if (player.age % 20 == 0) {
            if (HelperMethods.getAttackDamage(player.getMainHandStack()) > 6
                    || HelperMethods.getAttackDamage(player.getOffHandStack()) > 6
                    && SkillsAPI.getUnlockedSkills((ServerPlayerEntity) player,
                    "combat").get().contains(SkillReferencePosition.wayfarerSlender)){
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 25));
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 25, 1));
            }
        }
    }






    //DEBUG
    public static void debugPrintAttributes(PlayerEntity player) {
        //
        // For checking Spell Power attribute values
        //
        if (player.age % 20 == 0 && player.isSneaking()) {
            String attributeArcane       = SpellPower.getSpellPower(MagicSchool.ARCANE, player).toString();
            String attributeFire         = SpellPower.getSpellPower(MagicSchool.FIRE, player).toString();
            String attributeFrost         = SpellPower.getSpellPower(MagicSchool.FROST, player).toString();
            String attributeHealing      = SpellPower.getSpellPower(MagicSchool.HEALING, player).toString();
            String attributeLightning    = SpellPower.getSpellPower(MagicSchool.LIGHTNING, player).toString();
            String attributeSoul         = SpellPower.getSpellPower(MagicSchool.SOUL, player).toString();

            System.out.println("Arcane: "    + attributeArcane);
            System.out.println("Fire: "      + attributeFire);
            System.out.println("Frost: "     + attributeFrost);
            System.out.println("Healing: "   + attributeHealing);
            System.out.println("Lightning: " + attributeLightning);
            System.out.println("Soul: "      + attributeSoul);
        }
    }


    public static void testSpellEngine(PlayerEntity player, Entity target) {
        //
        // For testing Spell Engine spell casting
        //
        ItemStack itemStack     = player.getMainHandStack();
        Hand hand               = player.getActiveHand();
        SpellCast.Action action = SpellCast.Action.RELEASE;
        Identifier spellID      = new Identifier("simplyskills:fire_meteor");
        List<Entity> list       = new ArrayList<Entity>();
        list.add(target);

        SpellHelper.performSpell(
                player.world,
                player,
                spellID,
                list,
                itemStack,
                action,
                hand,
                20);

    }

    public static void testSpellEngineAOE(PlayerEntity player, String spellIdentifier) {
        //
        // For testing Spell Engine spell casting
        //
        ItemStack itemStack     = player.getMainHandStack();
        Hand hand               = player.getActiveHand();
        SpellCast.Action action = SpellCast.Action.RELEASE;
        Identifier spellID      = new Identifier(spellIdentifier);
        List<Entity> list       = new ArrayList<Entity>();


        Box box = HelperMethods.createBox(player, 6);
        for (Entity entities : player.world.getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {
            if (entities != null) {
                if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {

                    list.add(le);

                }
            }
        }

        if (!list.isEmpty()) {
            SpellHelper.performSpell(
                    player.world,
                    player,
                    spellID,
                    list,
                    itemStack,
                    action,
                    hand,
                    20);
        }

    }

    @Environment(EnvType.CLIENT)
    public static void testKeybindPacket() {

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(KeybindPacket.ABILITY1_PACKET, buf);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);

    }

    public static void getSpellCooldown(LivingEntity livingEntity, String spellID) {
        //Identifier spell = new Identifier(spellID);
        //SpellHelper.getCooldownDuration(livingEntity);

    }



}