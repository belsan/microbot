/*
 * Copyright (c) 2020, Zoinkwiz
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
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.clientofkourend;

import net.runelite.client.plugins.microbot.questhelper.collections.ItemCollections;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.BasicQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.conditional.Conditions;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.var.VarbitRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ItemReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.QuestPointReward;
import net.runelite.client.plugins.microbot.questhelper.steps.ConditionalStep;
import net.runelite.client.plugins.microbot.questhelper.steps.DetailedQuestStep;
import net.runelite.client.plugins.microbot.questhelper.steps.NpcStep;
import net.runelite.client.plugins.microbot.questhelper.steps.QuestStep;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.NpcID;

import java.util.*;

public class ClientOfKourend extends BasicQuestHelper
{
	//Items Required
	ItemRequirement feather;
	
	//Items Recommended
	ItemRequirement gamesNecklace;

	//Other items used
	ItemRequirement enchantedScroll, enchantedQuill, mysteriousOrb;

	Requirement talkedToLeenz, talkedToHorace, talkedToJennifer, talkedToMunty, talkedToRegath;

	QuestStep talkToVeos, useFeatherOnScroll, talkToLeenz, talkToHorace, talkToJennifer, talkToMunty, talkToRegath, returnToVeos, goToAltar, finishQuest;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		initializeRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToVeos);

		ConditionalStep makeEnchantedQuill = new ConditionalStep(this, talkToVeos);
		makeEnchantedQuill.addStep(new Conditions(enchantedQuill, talkedToLeenz, talkedToRegath, talkedToMunty, talkedToJennifer), talkToHorace);
		makeEnchantedQuill.addStep(new Conditions(enchantedQuill, talkedToLeenz, talkedToRegath, talkedToMunty), talkToJennifer);
		makeEnchantedQuill.addStep(new Conditions(enchantedQuill, talkedToLeenz, talkedToRegath), talkToMunty);
		makeEnchantedQuill.addStep(new Conditions(enchantedQuill, talkedToLeenz), talkToRegath);
		makeEnchantedQuill.addStep(new Conditions(enchantedQuill), talkToLeenz);
		makeEnchantedQuill.addStep(enchantedScroll, useFeatherOnScroll);
		steps.put(1, makeEnchantedQuill);

		steps.put(2, returnToVeos);

		ConditionalStep takeOrbToAltar = new ConditionalStep(this, returnToVeos);
		takeOrbToAltar.addStep(mysteriousOrb, goToAltar);

		steps.put(3, returnToVeos);

		steps.put(4, takeOrbToAltar);

		steps.put(5, finishQuest);
		steps.put(6, finishQuest);

		return steps;
	}

	@Override
	protected void setupRequirements()
	{
		feather = new ItemRequirement("Feather", ItemID.FEATHER);
		feather.setTooltip("Can be purchased from Gerrant's Fishy Business in Port Sarim.");
		feather.addAlternates(ItemID.HUNTING_POLAR_FEATHER, ItemID.HUNTING_WOODLAND_FEATHER, ItemID.HUNTING_JUNGLE_FEATHER, ItemID.HUNTING_DESERT_FEATHER,
			ItemID.HUNTING_EAGLE_FEATHER, ItemID.HUNTING_STRIPY_BIRD_FEATHER);
		feather.setHighlightInInventory(true);
		gamesNecklace = new ItemRequirement("Games necklace", ItemCollections.GAMES_NECKLACES);
		enchantedScroll = new ItemRequirement("Enchanted scroll", ItemID.VEOS_SCROLL);
		enchantedScroll.setHighlightInInventory(true);
		mysteriousOrb = new ItemRequirement("Mysterious orb", ItemID.VEOS_ORB);
		mysteriousOrb.setHighlightInInventory(true);

		enchantedQuill = new ItemRequirement("Enchanted quill", ItemID.VEOS_QUILL);
	}

	public void setupConditions()
	{
		talkedToLeenz = new VarbitRequirement(5620, 1);
		talkedToRegath = new VarbitRequirement(5621, 1);
		talkedToMunty = new VarbitRequirement(5622, 1);
		talkedToJennifer = new VarbitRequirement(5623, 1);
		talkedToHorace = new VarbitRequirement(5624, 1);
	}

	public void setupSteps()
	{
		talkToVeos = new NpcStep(this, NpcID.VEOS_VIS_AMULET, new WorldPoint(1824, 3690, 0),
			"Talk to Veos on the Port Piscarilius docks. You can travel to him by talking to Veos in Port Sarim.");
		talkToVeos.addDialogStep("Sounds interesting! How can I help?");
		talkToVeos.addDialogStep("Can you take me to Great Kourend?");
		talkToVeos.addDialogStep("Have you got any quests for me?");
		talkToVeos.addDialogStep("Let's talk about your client...");
		talkToVeos.addDialogStep("I've lost something you've given me.");

		useFeatherOnScroll = new DetailedQuestStep(this, "Use a feather on the Enchanted Scroll.", feather, enchantedScroll);

		talkToLeenz = new NpcStep(this, NpcID.PISCARILIUS_GENERALSTORE_KEEPER, new WorldPoint(1807, 3726, 0), "Talk to Leenz in Port Piscarilius general store.", enchantedQuill);
		talkToLeenz.addDialogStep("Can I ask you about Port Piscarilius?");
		talkToLeenz.addDialogStep("What is there to do in Port Piscarilius?");
		talkToHorace = new NpcStep(this, NpcID.HOSIDIUS_GENERALSTORE, new WorldPoint(1774, 3589, 0), "Talk to Horace in the Hosidius general store.", enchantedQuill);
		talkToHorace.addDialogStep("Can I ask you about Hosidius?");
		talkToHorace.addDialogStep("What is there to do in Hosidius?");
		talkToJennifer = new NpcStep(this, NpcID.SHAYZIEN_GENERALSTORE, new WorldPoint(1518, 3586, 0), "Talk to Jennifer in Shayzien general store.", enchantedQuill);
		talkToJennifer.addDialogStep("Can I ask you about Shayzien?");
		talkToJennifer.addDialogStep("What is there to do in Shayzien?");
		talkToMunty = new NpcStep(this, NpcID.LOVAKENGJ_GENERALSTORE, new WorldPoint(1551, 3752, 0), "Talk to Munty in Lovakengj general store.", enchantedQuill);
		talkToMunty.addDialogStep("Can I ask you about Lovakengj?");
		talkToMunty.addDialogStep("What is there to do in Lovakengj?");
		talkToRegath = new NpcStep(this, NpcID.ARCEUUS_GENERALSTORE, new WorldPoint(1720, 3724, 0), "Talk to Regath in Arceuus general store.", enchantedQuill);
		talkToRegath.addDialogStep("Can I ask you about Arceuus?");
		talkToRegath.addDialogStep("What is there to do in Arceuus?");

		returnToVeos = new NpcStep(this, NpcID.VEOS_VIS_AMULET, new WorldPoint(1824, 3690, 0), "Return to Veos on Piscarilius docks.");
		returnToVeos.addDialogStep("Let's talk about your client...");
		returnToVeos.addDialogStep("I've lost something you've given me.");
		goToAltar = new DetailedQuestStep(this, new WorldPoint(1712, 3883, 0), "Activate the mysterious orb at the Dark Altar. You can either run there through Arceuus, teleport to Wintertodt with the Games Necklace and run south, or teleport straight there on the Arceuus spellbook.", mysteriousOrb);

		finishQuest = new NpcStep(this, NpcID.VEOS_VIS_AMULET, new WorldPoint(1824, 3690, 0), "Return to Veos on Piscarilius docks.");
		finishQuest.addDialogStep("Let's talk about your client...");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		ArrayList<ItemRequirement> reqs = new ArrayList<>();
		reqs.add(feather);
		return reqs;
	}
	
	@Override
	public List<ItemRequirement> getItemRecommended()
	{
		return Arrays.asList(gamesNecklace);
	}
	
	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();
		reqs.add(new QuestRequirement(QuestHelperQuest.X_MARKS_THE_SPOT, QuestState.FINISHED));
		return reqs;
	}

	@Override
	public QuestPointReward getQuestPointReward()
	{
		return new QuestPointReward(1);
	}

	@Override
	public List<ItemReward> getItemRewards()
	{
		return Arrays.asList(
				new ItemReward("500 Experience Lamps (Any Skill)", ItemID.THOSF_REWARD_LAMP, 2), //4447 Placeholder until confirmed.
				new ItemReward("Kharedst's Memoirs", ItemID.VEOS_KHAREDSTS_MEMOIRS, 1));
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();

		allSteps.add(new PanelDetails("Starting off", Arrays.asList(talkToVeos, useFeatherOnScroll), feather));
		allSteps.add(new PanelDetails("Learn about Kourend", Arrays.asList(talkToLeenz, talkToRegath, talkToMunty, talkToJennifer, talkToHorace, returnToVeos)));
		allSteps.add(new PanelDetails("The Dark Altar", Arrays.asList(goToAltar, finishQuest)));
		return allSteps;
	}
}
