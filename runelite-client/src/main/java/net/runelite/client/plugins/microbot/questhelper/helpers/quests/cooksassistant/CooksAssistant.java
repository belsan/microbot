/*
 * Copyright (c) 2019, Trevor <https://github.com/Trevor159>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.cooksassistant;

import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.Zone;
import net.runelite.client.plugins.microbot.questhelper.requirements.zone.ZoneRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.UnlockReward;
import net.runelite.client.plugins.microbot.questhelper.steps.*;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.ObjectID;

import java.util.*;

public class CooksAssistant extends BasicQuestHelper
{
	//Items Required
	ItemRequirement egg, milk, flour, bucket, pot, coins, grain;

	Requirement controlsUsed;

	QuestStep getEgg, getWheat, milkCow, climbLadderOne, climbLadderTwoUp, climbLadderTwoDown, climbLadderThree, fillHopper,
		operateControls, collectFlour, finishQuest;

	NpcStep getPot, getBucket;

	Zone millSecond, millThird;

	Requirement inMillSecond, inMillThird;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();

		Map<Integer, QuestStep> steps = new HashMap<>();
		ConditionalStep doQuest = new ConditionalStep(this, getBucket);
		doQuest.addStep(new Conditions(milk, flour, egg), finishQuest);
		doQuest.addStep(new Conditions(milk, pot, egg, controlsUsed, inMillThird), climbLadderThree);
		doQuest.addStep(new Conditions(milk, pot, egg, controlsUsed, inMillSecond), climbLadderTwoDown);
		doQuest.addStep(new Conditions(milk, pot, egg, controlsUsed), collectFlour);
		doQuest.addStep(new Conditions(milk, pot, egg, grain, inMillThird), fillHopper);
		doQuest.addStep(new Conditions(milk, pot, egg, inMillThird), operateControls);
		doQuest.addStep(new Conditions(milk, pot, egg, grain, inMillSecond), climbLadderTwoUp);
		doQuest.addStep(new Conditions(milk, pot, egg, grain), climbLadderOne);
		doQuest.addStep(new Conditions(milk, pot, egg), getWheat);
		doQuest.addStep(new Conditions(milk, pot), getEgg);
		doQuest.addStep(new Conditions(bucket, pot), milkCow);
		doQuest.addStep(bucket, getPot);

		steps.put(0, doQuest);
		steps.put(1, doQuest);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		egg = new ItemRequirement("Egg", ItemID.EGG);
		egg.canBeObtainedDuringQuest();
		milk = new ItemRequirement("Bucket of milk", ItemID.BUCKET_MILK);
		milk.canBeObtainedDuringQuest();
		flour = new ItemRequirement("Pot of flour", ItemID.POT_FLOUR);
		flour.canBeObtainedDuringQuest();
		bucket = new ItemRequirement("Bucket", ItemID.BUCKET_EMPTY);
		pot = new ItemRequirement("Pot", ItemID.POT_EMPTY);
		coins = new ItemRequirement("Coins", ItemCollections.COINS);
		coins.setTooltip("Necessary if you do not have a pot / bucket");
		grain = new ItemRequirement("Grain", ItemID.GRAIN);

		controlsUsed = new VarbitRequirement(4920, 1);
	}

	@Override
	protected void setupZones()
	{
		millSecond = new Zone(new WorldPoint(3162, 3311, 1), new WorldPoint(3171, 3302, 1));
		millThird = new Zone(new WorldPoint(3162, 3311, 2), new WorldPoint(3171, 3302, 2));
	}

	public void setupConditions()
	{
		inMillSecond = new ZoneRequirement(millSecond);
		inMillThird = new ZoneRequirement(millThird);
	}

	public void setupSteps()
	{
		getEgg = new ItemStep(this, new WorldPoint(3177, 3296, 0),
			"Grab an egg from the farm north of Lumbridge.", egg);
		getBucket = new NpcStep(this, NpcID.GENERALSHOPKEEPER1, new WorldPoint(3212, 3246, 0),
			"Purchase a bucket from the Lumbridge General Store.", coins.quantity(3));
		getBucket.addWidgetHighlightWithItemIdRequirement(300, 16, ItemID.BUCKET_EMPTY, true);
		getBucket.addAlternateNpcs(NpcID.GENERALASSISTANT1);
		getPot = new NpcStep(this, NpcID.GENERALSHOPKEEPER1, new WorldPoint(3212, 3246, 0),
			"Purchase a pot from the Lumbridge General Store.", coins.quantity(3));
		getPot.addAlternateNpcs(NpcID.GENERALASSISTANT1);
		milkCow = new ObjectStep(this, ObjectID.FAT_COW, new WorldPoint(3254, 3272, 0),
			"Milk the cow north-east of Lumbridge.", bucket);
		getWheat = new ObjectStep(this, ObjectID.FAI_VARROCK_WHEAT_CORNER, new WorldPoint(3161, 3292, 0),
			"Pick some wheat north of Lumbridge.");
		climbLadderOne = new ObjectStep(this, ObjectID.QIP_COOK_LADDER, new WorldPoint(3164, 3307, 0),
			"Climb up the ladder in the Mill north of Lumbridge to the top floor.", pot, grain);
		climbLadderTwoUp = new ObjectStep(this, ObjectID.QIP_COOK_LADDER_MIDDLE, new WorldPoint(3164, 3307, 1),
			"Climb up the ladder in the Mill north of Lumbridge to the top floor.", pot, grain);
		climbLadderTwoUp.addDialogStep("Climb Up.");
		climbLadderOne.addSubSteps(climbLadderTwoUp);
		fillHopper = new ObjectStep(this, ObjectID.HOPPER1, new WorldPoint(3166, 3307, 2),
			"Fill the hopper with your grain.", pot, grain.highlighted());
		fillHopper.addIcon(ItemID.GRAIN);
		operateControls = new ObjectStep(this, ObjectID.HOPPERLEVERS1, new WorldPoint(3166, 3305, 2),
			"Operate the hopper controls.", pot);
		climbLadderThree = new ObjectStep(this, ObjectID.QIP_COOK_LADDER_TOP, new WorldPoint(3164, 3307, 2),
			"Climb down the ladder in the Mill to the ground floor.", pot);
		climbLadderTwoDown = new ObjectStep(this, ObjectID.QIP_COOK_LADDER_MIDDLE, new WorldPoint(3164, 3307, 1),
			"Climb down the ladder in the Mill to the ground floor.", pot);
		climbLadderTwoDown.addDialogStep("Climb Down.");
		climbLadderThree.addSubSteps(climbLadderTwoDown);
		collectFlour = new ObjectStep(this, ObjectID.MILLBASE_FLOUR, new WorldPoint(3166, 3306, 0),
			"Collect the flour in the bin.", pot.highlighted());
		collectFlour.addIcon(ItemID.POT_EMPTY);
		finishQuest = new NpcStep(this, NpcID.POH_SERVANT_COOK_WOMAN, new WorldPoint(3206, 3214, 0),
			"Give the Cook in Lumbridge Castle's kitchen the required items to finish the quest.",
			egg, milk, flour);
		finishQuest.addDialogSteps("What's wrong?", "Can I help?", "Yes.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(egg);
		reqs.add(flour);
		reqs.add(milk);
		return reqs;
	}

	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Collections.singletonList(coins);
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ExperienceReward> getExperienceRewards()
	{
		return Collections.singletonList(new ExperienceReward(Skill.COOKING, 300));
	}

	@Override
	public List<UnlockReward> getUnlockRewards()
	{
		return Collections.singletonList(new UnlockReward("Permission to use The Cook's range."));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off", Arrays.asList(getBucket, getPot), coins.quantity(3)));
		allSteps.add(new PanelDetails("Getting the Milk", Collections.singletonList(milkCow), bucket));
		allSteps.add(new PanelDetails("Getting the Egg", Collections.singletonList(getEgg)));
		allSteps.add(new PanelDetails("Getting the Flour", Arrays.asList(getWheat, climbLadderOne, fillHopper,
			operateControls, climbLadderThree, collectFlour), pot));
		allSteps.add(new PanelDetails("Finishing up", Collections.singletonList(finishQuest), egg, flour, milk));

		return allSteps;
	}
}
