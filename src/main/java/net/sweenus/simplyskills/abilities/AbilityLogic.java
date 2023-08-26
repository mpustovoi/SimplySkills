package net.sweenus.simplyskills.abilities;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.puffish.skillsmod.SkillsAPI;
import net.puffish.skillsmod.api.Category;
import net.sweenus.simplyskills.SimplySkills;
import net.sweenus.simplyskills.registry.ItemRegistry;
import net.sweenus.simplyskills.util.HelperMethods;

import java.util.Collection;
import java.util.List;

public class AbilityLogic {

    // -- Unlock Manager --

    public static boolean skillTreeUnlockManager(PlayerEntity player, String categoryID) {

        if (HelperMethods.stringContainsAny(categoryID, SimplySkills.getSpecialisations())) {

            if (SimplySkills.generalConfig.removeUnlockRestrictions)
                return false;

            //Prevent unlocking multiple specialisations (kinda cursed ngl)
            List<String> specialisationList = SimplySkills.getSpecialisationsAsArray();
            for (String s : specialisationList) {
                //System.out.println("Comparing " + categoryID + " against " + s);
                if (categoryID.contains(s)) {
                    //System.out.println( "looking for a match in unlocked categories: " + SkillsAPI.getUnlockedCategories((ServerPlayerEntity)player).toString());

                    Collection<Category> categories = SkillsAPI.getUnlockedCategories((ServerPlayerEntity) player);
                    for (Category value : categories) {
                        if (HelperMethods.stringContainsAny(value.getId().toString(), SimplySkills.getSpecialisations())) {
                            //System.out.println(player + " attempted to unlock a second specialisation. Denied.");
                            return true;
                        }
                    }
                }
            }


            //Process unlock
            if (categoryID.contains("simplyskills:wizard")
                    && !HelperMethods.isUnlocked("simplyskills:wizard", null, player)) {
                if (SimplySkills.wizardConfig.enableWizardSpecialisation) {
                    playUnlockSound(player);
                    return false;
                }
            } else if (categoryID.contains("simplyskills:berserker")
                    && !HelperMethods.isUnlocked("simplyskills:berserker", null, player)) {
                if (SimplySkills.berserkerConfig.enableBerserkerSpecialisation) {
                    playUnlockSound(player);
                    return false;
                }
            } else if (categoryID.contains("simplyskills:rogue")
                    && !HelperMethods.isUnlocked("simplyskills:rogue", null, player)) {
                if (SimplySkills.rogueConfig.enableRogueSpecialisation) {
                    playUnlockSound(player);
                    return false;
                }
            } else if (categoryID.contains("simplyskills:ranger")
                    && !HelperMethods.isUnlocked("simplyskills:ranger", null, player)) {
                if (SimplySkills.rangerConfig.enableRangerSpecialisation) {
                    playUnlockSound(player);
                    return false;
                }
            } else if (categoryID.contains("simplyskills:spellblade")
                    && !HelperMethods.isUnlocked("simplyskills:spellblade", null, player)) {
                if (SimplySkills.spellbladeConfig.enableSpellbladeSpecialisation) {
                    playUnlockSound(player);
                    return false;
                }
            } else if (categoryID.contains("simplyskills:crusader")
                    && !HelperMethods.isUnlocked("simplyskills:crusader", null, player)) {

                if (!FabricLoader.getInstance().isModLoaded("paladins"))
                    return true;

                if (SimplySkills.crusaderConfig.enableCrusaderSpecialisation) {
                    playUnlockSound(player);
                    return false;
                }
            }
        }
        return false;
    }

    static void playUnlockSound(PlayerEntity player) {
        if (player.getMainHandStack().getItem() != ItemRegistry.GRACIOUSMANUSCRIPT)
            player.getWorld().playSoundFromEntity(null, player, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    SoundCategory.PLAYERS, 1, 1);
    }

    public static void performTagEffects(PlayerEntity player, String tags) {

        if (tags.contains("magic")) {

        }
        if (tags.contains("physical")) {

        }
        if (tags.contains("arrow")) {

        }
        if (tags.contains("arcane")) {

        }

    }


    //Misc Abilities
    public static void passiveAreaCleanse(PlayerEntity player) {
        if (player.age % 80 == 0) {
            int radius = 12;

            Box box = HelperMethods.createBox(player, radius);
            for (Entity entities : player.getWorld().getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {

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


}
