package com.Ben12345rocks.VotingPlugin.Commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import com.Ben12345rocks.VotingPlugin.Main;
import com.Ben12345rocks.VotingPlugin.Utils;
import com.Ben12345rocks.VotingPlugin.Config.Config;
import com.Ben12345rocks.VotingPlugin.Config.ConfigBonusReward;
import com.Ben12345rocks.VotingPlugin.Config.ConfigFormat;
import com.Ben12345rocks.VotingPlugin.Config.ConfigGUI;
import com.Ben12345rocks.VotingPlugin.Config.ConfigVoteSites;
import com.Ben12345rocks.VotingPlugin.Data.Data;
import com.Ben12345rocks.VotingPlugin.Inventory.BInventory;
import com.Ben12345rocks.VotingPlugin.Inventory.BInventoryButton;
import com.Ben12345rocks.VotingPlugin.Objects.CommandHandler;
import com.Ben12345rocks.VotingPlugin.Objects.User;
import com.Ben12345rocks.VotingPlugin.Objects.VoteSite;
import com.Ben12345rocks.VotingPlugin.TopVoter.TopVoter;

public class Commands {

	static ConfigBonusReward bonusReward = ConfigBonusReward.getInstance();

	static Config config = Config.getInstance();

	static ConfigVoteSites configVoteSites = ConfigVoteSites.getInstance();

	static ConfigFormat format = ConfigFormat.getInstance();

	static Commands instance = new Commands();

	static Main plugin = Main.plugin;

	public static Commands getInstance() {
		return instance;
	}

	private Commands() {
	}

	public Commands(Main plugin) {
		Commands.plugin = plugin;
	}

	public ArrayList<String> adminHelpText() {
		ArrayList<String> msg = new ArrayList<String>();
		msg.add("VotingPlugin Admin Help");
		msg.add("[] = Optional");
		msg.add("() = Needed");
		msg.add("Aliases: adminvote, av");
		msg.add("/adminvote help - See this page");
		msg.add("/adminvote perms - See list of perms");
		msg.add("/adminvote vote (player) (sitename) - Trigger vote");
		msg.add("/adminvote bungeevote (player) (sitename) - Trigger bungee only vote");
		msg.add("/adminvote servervote (player) (sitename) - Trigger server only vote");
		msg.add("/adminvote settotal (player) (sitename) (amount) - Set total votes of a player on votesite");
		msg.add("/adminvote reload - Reload the plugin");
		msg.add("/adminvote uuid (playername) - See uuid of player");
		msg.add("/adminvote version - Version info");
		msg.add("/adminvote sites [site] - List of sites and site info");
		msg.add("Editing Commands");
		msg.add("/adminvote VoteSite (SiteName) Create - Gernerate a votesite");
		msg.add("/adminvote VoteSite (SiteName) AddItem (Item) - Add item in hand to votesite");
		msg.add("/adminvote VoteSite (SiteName) SetMoney (Money) - Set money for votesite");
		msg.add("/adminvote VoteSite (SiteName) SetServiceSite (ServiceSite) - Set servicesite on votesite");
		msg.add("/adminvote VoteSite (SiteName) SetDisabled (Disabled) - Set votesite disabled");
		msg.add("/adminvote VoteSite (SiteName) SetVoteDelay (Delay) - Set votesite votedelay");
		msg.add("/adminvote VoteSite (SiteName) AddCommandPlayer (Command) - Add player command to votesite");
		msg.add("/adminvote VoteSite (SiteName) AddCommandConsole (Command) - Add console command to votesite");
		msg.add("/adminvote VoteSite (SiteName) AddExtraRewardItem (Reward) (Item) - Add ExtraReward item in hand to votesite");
		msg.add("/adminvote VoteSite (SiteName) SetExtraRewardMoney (Reward) (Money) - Set ExtraReward money for votesite");
		msg.add("/adminvote VoteSite (SiteName) SetExtraRewardChance (Reward) (Chance) - Set ExtraReward chance");
		msg.add("/adminvote VoteSite (SiteName) AddExtraRewardCommandPlayer (Reward) (Command) - Add ExtraReward player command to votesite");
		msg.add("/adminvote VoteSite (SiteName) AddExtraRewardCommandConsole (Reward) (Command) - Add ExtraReward console command to votesite");
		msg.add("/adminvote BonusReward AddItem (Item) - Add item in hand");
		msg.add("/adminvote BonusReward SetMoney (Money) - Set money");
		msg.add("/adminvote BonusReward SetGiveBonusReward (Disabled) - Set bonus reward enabled");
		msg.add("/adminvote BonusReward AddCommandPlayer (Command) - Add player command");
		msg.add("/adminvote BonusReward AddCommandConsole (Command) - Add console command");
		msg.add("/adminvote BonusReward AddExtraRewardItem (Reward) (Item) - Add ExtraReward item in hand");
		msg.add("/adminvote BonusReward SetExtraRewardMoney (Reward) (Money) - Set ExtraReward money");
		msg.add("/adminvote BonusReward SetExtraRewardChance (Reward) (Chance) - Set ExtraReward chance");
		msg.add("/adminvote BonusReward AddExtraRewardCommandPlayer (Reward) (Command) - Add ExtraReward player command");
		msg.add("/adminvote BonusReward AddExtraRewardCommandConsole (Reward) (Command) - Add ExtraReward console command");
		msg.add("/adminvote Config SetDebug (true/false) - Set debug");
		msg.add("/adminvote Config SetBroadcastVote (true/false) - Set broadcastvote");
		msg.add("/adminvote Config SetUpdateReminder (true/false) - Set updatereminder");
		msg.add("/adminvote Config SetAllowUnjoined (true/false) - Set allowunjoined");
		msg.add("/adminvote Config SetDisableTopVoterAwards (true/false) - Set disabletopvoterawards");
		msg.add("/adminvote ServerData SetPrevMonth - Set prevmonth, DO NOT USE");
		return msg;
	}

	public String[] adminHelpTextColored() {
		ArrayList<String> texts = new ArrayList<String>();
		for (String msg : adminHelpText()) {
			if (msg.split("-").length > 1) {
				texts.add("&3&l" + msg.split("-")[0] + "-&3"
						+ msg.split("-")[1]);
			} else {
				texts.add("&3&l" + msg.split("-")[0]);
			}
		}
		texts = Utils.getInstance().colorize(texts);
		return Utils.getInstance().convertArray(texts);

	}

	public String[] commandVoteToday(int page) {
		int pagesize = ConfigFormat.getInstance().getPageSize();
		if (page < 1) {
			page = 1;
		}
		ArrayList<String> msg = new ArrayList<String>();

		int maxPage = plugin.voteToday.length / pagesize;
		if (plugin.voteToday.length % pagesize != 0) {
			maxPage++;
		}

		msg.add("&cToday's Votes " + page + "/" + maxPage);
		msg.add("&cPlayerName : VoteSite : Time");
		page--;

		for (int i = pagesize * page; (i < plugin.voteToday.length)
				&& (i < ((page + 1) * pagesize)); i++) {
			msg.add(plugin.voteToday[i]);
		}

		msg = Utils.getInstance().colorize(msg);
		return Utils.getInstance().convertArray(msg);

	}

	public String[] listPerms() {
		ArrayList<String> msg = new ArrayList<String>();

		for (Permission perm : plugin.getDescription().getPermissions()) {
			msg.add(perm.getName());
		}

		for (CommandHandler handle : plugin.voteCommand) {
			if (!msg.contains(handle.getPerm())) {
				msg.add(handle.getPerm());
			}
		}

		for (CommandHandler handle : plugin.adminVoteCommand) {
			if (!msg.contains(handle.getPerm())) {
				msg.add(handle.getPerm());
			}
		}

		msg = Utils.getInstance().colorize(msg);
		Collections.sort(msg, String.CASE_INSENSITIVE_ORDER);

		return Utils.getInstance().convertArray(msg);
	}

	@SuppressWarnings("deprecation")
	public void openVoteGUI(Player player) {
		BInventory inv = new BInventory("VoteGUI", 9);

		int count = 0;
		for (String slot : ConfigGUI.getInstance().getVoteGUISlots()) {
			ItemStack item = new ItemStack(ConfigGUI.getInstance()
					.getVoteGUISlotID(slot), ConfigGUI.getInstance()
					.getVoteGUISlotAmount(slot), (short) ConfigGUI
					.getInstance().getVoteGUISlotData(slot));

			String[] lore = new String[1];

			if (slot.equalsIgnoreCase("url")) {
				lore = Commands.getInstance().voteURLs();
			} else if (slot.equalsIgnoreCase("next")) {
				lore = Commands.getInstance().voteCommandNext(new User(player));
			} else if (slot.equalsIgnoreCase("last")) {
				lore = Commands.getInstance().voteCommandLast(new User(player));
			} else if (slot.equalsIgnoreCase("total")) {
				lore = Commands.getInstance()
						.voteCommandTotal(new User(player));
			} else if (slot.equalsIgnoreCase("top")) {
				lore = TopVoter.getInstance().topVoter(1);
			} else if (slot.equalsIgnoreCase("today")) {
				lore = plugin.voteToday;
			} else if (slot.equalsIgnoreCase("help")) {
				lore = Commands.getInstance().voteHelpTextColored();
			}

			inv.addButton(count, new BInventoryButton(ConfigGUI.getInstance()
					.getVoteGUISlotName(slot), lore, item) {

				@Override
				public void onClick(InventoryClickEvent event) {
					Player player = (Player) event.getWhoClicked();
					if (player != null) {
						player.closeInventory();
						player.performCommand(ConfigGUI.getInstance()
								.getVoteGUISlotCommand(slot));

					}

				}
			});
			count++;
		}

		BInventory.openInventory(player, inv);
	}

	public String[] playerInfo(User user) {
		ArrayList<String> msg = new ArrayList<String>();

		// title
		msg.add("&cPlayer '" + user.getPlayerName() + "' Info");

		// last vote
		msg.addAll(Utils.getInstance().convertArray(voteCommandLast(user)));

		// next vote
		msg.addAll(Utils.getInstance().convertArray(voteCommandNext(user)));

		// total
		msg.addAll(Utils.getInstance().convertArray(voteCommandTotal(user)));

		msg = Utils.getInstance().colorize(msg);
		return Utils.getInstance().convertArray(msg);
	}

	public String[] voteCommandLast(User user) {

		ArrayList<String> msg = new ArrayList<String>();

		ArrayList<VoteSite> voteSites = configVoteSites.getVoteSites();

		String playerName = user.getPlayerName();

		msg.add(Utils.getInstance().replaceIgnoreCase(
				format.getCommandsVoteLastTitle(), "%player%", playerName));

		for (VoteSite voteSite : voteSites) {
			String timeString = voteCommandLastDate(user, voteSite);

			msg.add(format
					.getCommandsVoteLastLine()
					.replace("%Month% %Day%, %Year% %Hour%:%Minute% %ampm%",
							"%time%").replace("%time%", timeString)
					.replace("%SiteName%", voteSite.getSiteName()));
		}

		msg = Utils.getInstance().colorize(msg);
		return Utils.getInstance().convertArray(msg);
	}

	public String voteCommandLastDate(User user, VoteSite voteSite) {
		Date date = new Date(user.getTime(voteSite));
		String timeString = new SimpleDateFormat(format.getTimeFormat())
				.format(date);
		return timeString;
	}

	public String[] voteCommandNext(User user) {
		ArrayList<String> msg = new ArrayList<String>();

		ArrayList<VoteSite> voteSites = configVoteSites.getVoteSites();

		String playerName = user.getPlayerName();

		msg.add(Utils.getInstance().colorize(
				Utils.getInstance().replaceIgnoreCase(
						format.getCommandsVoteNextTitle(), "%player%",
						playerName)));

		for (VoteSite voteSite : voteSites) {

			String msgLine = format.getCommandsVoteNextLayout();

			msgLine = Utils.getInstance().replaceIgnoreCase(msgLine, "%info%",
					voteCommandNextInfo(user, voteSite));

			msgLine = Utils.getInstance().replaceIgnoreCase(msgLine,
					"%SiteName%", voteSite.getSiteName());
			msg.add(Utils.getInstance().colorize(msgLine));

		}
		return Utils.getInstance().convertArray(msg);
	}

	@SuppressWarnings("deprecation")
	public String voteCommandNextInfo(User user, VoteSite voteSite) {
		String info = new String();

		Date date = new Date(user.getTime(voteSite));

		int month = date.getMonth();
		int day = date.getDate();
		int hour = date.getHours();
		int min = date.getMinutes();
		int year = date.getYear();

		int votedelay = configVoteSites.getVoteDelay(voteSite.getSiteName());
		if (votedelay == 0) {
			String errorMsg = format.getCommandsVoteNextInfoError();
			info = errorMsg;
		} else {

			Date voteTime = new Date(year, month, day, hour, min);
			Date nextvote = DateUtils.addHours(voteTime, votedelay);

			int cday = new Date().getDate();
			int cmonth = new Date().getMonth();
			int chour = new Date().getHours();
			int cmin = new Date().getMinutes();
			int cyear = new Date().getYear();
			Date currentDate = new Date(cyear, cmonth, cday, chour, cmin);

			if ((nextvote == null) || (day == 0) || (hour == 0)) {
				String canVoteMsg = format.getCommandsVoteNextInfoCanVote();
				info = canVoteMsg;
			} else {
				if (!currentDate.after(nextvote)) {
					long diff = nextvote.getTime() - currentDate.getTime();

					// long diffSeconds = (diff / 1000) % 60;
					long diffMinutes = (diff / (60 * 1000)) % 60;
					long diffHours = diff / (60 * 60 * 1000);
					// long diffDays = diff / (24 * 60 * 60 * 1000);

					String timeMsg = format.getCommandsVoteNextInfoTime();
					timeMsg = Utils.getInstance().replaceIgnoreCase(timeMsg,
							"%hours%", Long.toString(diffHours));
					timeMsg = Utils.getInstance().replaceIgnoreCase(timeMsg,
							"%minutes%", Long.toString(diffMinutes));
					info = timeMsg;
				} else {
					String canVoteMsg = format.getCommandsVoteNextInfoCanVote();
					info = canVoteMsg;
				}
			}
		}
		return info;
	}

	public String[] voteCommandSiteInfo(String voteSiteName) {
		ArrayList<String> msg = new ArrayList<String>();

		if (!ConfigVoteSites.getInstance().getVoteSiteFile(voteSiteName)
				.exists()) {
			msg.add("&cInvalid Vote Site, see /av sites!");
		} else {

			VoteSite voteSite = plugin.getVoteSite(voteSiteName);

			msg.add("&c&lVote Site Info for " + voteSiteName + ":");

			msg.add("&cSite: &6" + voteSite.getServiceSite());
			msg.add("&cVoteURL: &6" + voteSite.getVoteURL());
			msg.add("&cVote Delay: &6" + voteSite.getVoteDelay());
			msg.add("&cMoney: &6" + voteSite.getMoney());
			msg.add("&cPriority: &6"
					+ ConfigVoteSites.getInstance().getPriority(voteSiteName));

			msg.add("&cItems:");
			for (String item : ConfigVoteSites.getInstance().getItems(
					voteSite.getSiteName())) {
				msg.add("&c- &6" + item);
			}

			msg.add("&cPlayer Commands:");

			try {
				for (String playerCommands : voteSite.getPlayerCommands()) {
					msg.add("&c- " + playerCommands);
				}
			} catch (Exception ex) {
			}

			msg.add("&cConsole Commands:");

			try {
				for (String consoleCommands : voteSite.getConsoleCommands()) {
					msg.add("&c- " + consoleCommands);
				}
			} catch (Exception ex) {
			}

			msg.add("&4&l&nExtra Rewards:");
			for (String reward : configVoteSites
					.getExtraRewardRewards(voteSiteName)) {
				msg.add("&4&lReward: &c" + reward);
				msg.add("&cChance: &6"
						+ voteSite.getExtraRewardsChance().get(reward));
				msg.add("&cMoney: &6"
						+ voteSite.getExtraRewardsMoney().get(reward));

				ArrayList<String> worlds = voteSite.getExtraRewardsWorld().get(
						reward);
				if (worlds != null) {
					msg.add("&cWorlds: "
							+ Utils.getInstance().makeStringList(worlds));
				}
				msg.add("&cGiveInEachWorld: &6"
						+ ConfigVoteSites.getInstance()
								.getExtraRewardGiveInEachWorld(
										voteSite.getSiteName(), reward));

				msg.add("&cPermission: &6"
						+ voteSite.getExtraRewardsPermission().get(reward));

				msg.add("&cItems:");
				for (String item : ConfigVoteSites.getInstance()
						.getExtraRewardItems(voteSite.getSiteName(), reward)) {
					msg.add("&c- &6" + item);
				}

				msg.add("&cPlayer Commands:");

				try {
					for (String playerCommands : voteSite
							.getExtraRewardsPlayerCommands().get(reward)) {
						msg.add("&c- " + playerCommands);
					}
				} catch (Exception ex) {
				}

				msg.add("&cConsole Commands:");

				try {
					for (String consoleCommands : voteSite
							.getExtraRewardsConsoleCommands().get(reward)) {
						msg.add("&c- " + consoleCommands);
					}
				} catch (Exception ex) {
				}
			}

			msg.add("&c&lCumulative Rewards:");

			msg.add("&cVotes: &6" + voteSite.getCumulativeVotes());
			msg.add("&cMoney: &6" + voteSite.getCumulativeMoney());

			msg.add("&cItems:");
			for (String item : ConfigVoteSites.getInstance()
					.getCumulativeRewardItems(voteSite.getSiteName())) {
				msg.add("&c- &6" + item);
			}

			msg.add("&cPlayer Commands:");

			try {
				for (String playerCommands : voteSite
						.getCumulativePlayerCommands()) {
					msg.add("&c- " + playerCommands);
				}
			} catch (Exception ex) {
			}

			msg.add("&cConsole Commands:");

			try {
				for (String consoleCommands : voteSite
						.getCumulativeConsoleCommands()) {
					msg.add("&c- " + consoleCommands);
				}
			} catch (Exception ex) {
			}
		}
		msg = Utils.getInstance().colorize(msg);
		return Utils.getInstance().convertArray(msg);
	}

	public String[] voteCommandSites() {
		ArrayList<String> msg = new ArrayList<String>();

		msg.add("&c&lVote Sites:");

		int count = 1;
		ArrayList<VoteSite> voteSites = ConfigVoteSites.getInstance()
				.getVoteSites();
		if (voteSites != null) {
			for (VoteSite voteSite : voteSites) {
				msg.add("&c" + count + ". &6" + voteSite.getSiteName());
				count++;
			}
		}

		msg = Utils.getInstance().colorize(msg);
		return Utils.getInstance().convertArray(msg);
	}

	public String[] voteCommandTotal(User user) {
		ArrayList<String> msg = new ArrayList<String>();
		ArrayList<VoteSite> voteSites = configVoteSites.getVoteSites();

		String playerName = user.getPlayerName();

		msg.add(Utils.getInstance().replaceIgnoreCase(
				format.getCommandsVoteTotalTitle(), "%player%", playerName));

		// total votes
		int total = 0;

		for (VoteSite voteSite : voteSites) {
			int votes = user.getTotal(voteSite);
			// int votes = Data.getInstance().getTotal(playerName, siteName);
			total += votes;
			String line = format.getCommandsVoteTotalLine();
			msg.add(Utils.getInstance().replaceIgnoreCase(
					Utils.getInstance().replaceIgnoreCase(line, "%Total%",
							"" + votes), "%SiteName%", voteSite.getSiteName()));

		}
		msg.add(Utils.getInstance().replaceIgnoreCase(
				format.getCommandsVoteTotalTotal(), "%Totals%", "" + total));

		msg = Utils.getInstance().colorize(msg);
		return Utils.getInstance().convertArray(msg);
	}

	public String[] voteCommandTotalAll() {

		ArrayList<String> msg = new ArrayList<String>();

		ArrayList<VoteSite> voteSites = configVoteSites.getVoteSites();

		ArrayList<String> voteNames = Data.getInstance().getPlayerNames();

		msg.add(format.getCommandsVoteTotalAllTitle());
		int total = 0;
		for (VoteSite voteSite : voteSites) {
			int votes = 0;
			for (String playerName : voteNames) {
				if (playerName != null) {
					User user = new User(playerName);
					votes += user.getTotal(voteSite);
				}
			}
			msg.add(Utils.getInstance().replaceIgnoreCase(
					Utils.getInstance().replaceIgnoreCase(
							format.getCommandsVoteTotalAllLine(), "%SiteName%",
							voteSite.getSiteName()), "%Total%", "" + votes));
			total += votes;
		}
		msg.add(Utils.getInstance().replaceIgnoreCase(
				format.getCommandsVoteTotalAllTotal(), "%Totals%", "" + total));

		msg = Utils.getInstance().colorize(msg);
		return Utils.getInstance().convertArray(msg);
	}

	public ArrayList<String> voteHelpText() {
		ArrayList<String> texts = new ArrayList<String>();
		texts.add(ConfigFormat.getInstance().getCommandsVoteHelpTitle());
		texts.addAll(ConfigFormat.getInstance().getCommandsVoteHelpLines());
		return texts;
	}

	public String[] voteHelpTextColored() {
		ArrayList<String> texts = new ArrayList<String>();
		for (String msg : voteHelpText()) {
			if (msg.split("-").length > 1) {
				texts.add("&3&l" + msg.split("-")[0] + "-&3"
						+ msg.split("-")[1]);
			} else {
				texts.add("&3&l" + msg.split("-")[0]);
			}
		}
		texts = Utils.getInstance().colorize(texts);
		return Utils.getInstance().convertArray(texts);

	}

	@SuppressWarnings("deprecation")
	public void voteReward(Player player, String siteName) {
		BInventory inv = new BInventory("VoteReward", 9);

		int count = 0;
		if (siteName == null || siteName == "") {
			for (VoteSite voteSite : plugin.voteSites) {
				ItemStack item = new ItemStack(ConfigGUI.getInstance()
						.getVoteSiteItemID(voteSite.getSiteName()), ConfigGUI
						.getInstance().getVoteSiteItemAmount(
								voteSite.getSiteName()), (short) ConfigGUI
						.getInstance().getVoteSiteItemData(
								voteSite.getSiteName()));

				inv.addButton(
						count,
						new BInventoryButton(
								ConfigGUI.getInstance().getVoteSiteItemName(
										voteSite.getSiteName()),
								Utils.getInstance()
										.convertArray(
												(ArrayList<String>) ConfigGUI
														.getInstance()
														.getVoteSiteItemLore(
																voteSite.getSiteName())),
								item) {

							@Override
							public void onClick(InventoryClickEvent event) {
								Player player = (Player) event.getWhoClicked();
								if (player != null) {
									player.closeInventory();
									player.performCommand("vote reward "
											+ voteSite.getSiteName());

								}

							}
						});
				count++;
			}
		} else {
			for (String itemName : ConfigGUI.getInstance().getVoteSiteItems(
					siteName)) {
				ItemStack item = new ItemStack(ConfigGUI.getInstance()
						.getVoteSiteItemsID(siteName, itemName), ConfigGUI
						.getInstance().getVoteSiteItemsAmount(siteName,
								itemName), (short) ConfigGUI.getInstance()
						.getVoteSiteItemsData(siteName, itemName));

				inv.addButton(
						count,
						new BInventoryButton(ConfigGUI.getInstance()
								.getVoteSiteItemsName(siteName, itemName),
								Utils.getInstance().convertArray(
										(ArrayList<String>) ConfigGUI
												.getInstance()
												.getVoteSiteItemsLore(siteName,
														itemName)), item) {

							@Override
							public void onClick(InventoryClickEvent event) {
								Player player = (Player) event.getWhoClicked();
								if (player != null) {
									player.closeInventory();
								}

							}
						});
				count++;
			}
		}

		BInventory.openInventory(player, inv);
	}

	@SuppressWarnings("deprecation")
	public String[] voteToday() {
		ArrayList<String> msg = new ArrayList<String>();

		ArrayList<User> users = Utils.getInstance().convertSet(
				Data.getInstance().getUsers());

		if (users != null) {

			for (User user : users) {
				for (VoteSite voteSite : configVoteSites.getVoteSites()) {
					long time = user.getTime(voteSite);
					if (new Date().getDate() == Utils.getInstance()
							.getDayFromMili(time)
							&& new Date().getMonth() == Utils.getInstance()
									.getMonthFromMili(time)
							&& new Date().getYear() == Utils.getInstance()
									.getYearFromMili(time)) {

						String timeString = new SimpleDateFormat(
								format.getTimeFormat()).format(new Date(time));
						msg.add("&6" + user.getPlayerName() + " : "
								+ voteSite.getSiteName() + " : " + timeString);
					}
				}
			}
		}
		msg = Utils.getInstance().colorize(msg);
		return Utils.getInstance().convertArray(msg);
	}

	@SuppressWarnings("deprecation")
	public void voteURL(Player player) {
		BInventory inv = new BInventory("VoteURL", 9);

		int count = 0;
		for (VoteSite voteSite : plugin.voteSites) {
			ItemStack item = new ItemStack(ConfigGUI.getInstance()
					.getVoteURLAlreadyVotedItemID(), ConfigGUI.getInstance()
					.getVoteURLAlreadyVotedItemAmount(), (short) ConfigGUI
					.getInstance().getVoteURLAlreadyVotedItemData());
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ConfigGUI.getInstance().getVoteURLSeeURL());

			User user = new User(player);
			if (user.canVoteSite(voteSite)) {
				item = new ItemStack(ConfigGUI.getInstance()
						.getVoteURLCanVoteItemID(), ConfigGUI.getInstance()
						.getVoteURLCanVoteItemAmount(), (short) ConfigGUI
						.getInstance().getVoteURLCanVoteItemData());
			} else {
				lore.add(ConfigGUI.getInstance().getVoteURLNextVote()
						.replace("%Info%", voteCommandNextInfo(user, voteSite)));
			}

			inv.addButton(
					count,
					new BInventoryButton(ConfigGUI.getInstance()
							.getVoteURLSiteName()
							.replace("%Name%", voteSite.getSiteName()), Utils
							.getInstance().convertArray(lore), item) {

						@Override
						public void onClick(InventoryClickEvent event) {
							Player player = (Player) event.getWhoClicked();
							if (player != null) {
								player.closeInventory();
								player.sendMessage(voteSite.getVoteURL());

							}

						}
					});
			count++;
		}

		BInventory.openInventory(player, inv);
	}

	public String[] voteURLs() {
		ArrayList<String> sites = new ArrayList<String>();
		ArrayList<VoteSite> voteSites = configVoteSites.getVoteSites();

		List<String> title = ConfigFormat.getInstance().getCommandsVoteTitle();
		if (title != null) {
			sites.addAll(title);
		}
		int counter = 0;
		for (VoteSite voteSite : voteSites) {
			counter++;
			String voteURL = configVoteSites.getVoteURL(voteSite.getSiteName());
			String msg = format.getCommandsVoteURLS();
			msg = Utils.getInstance().colorize(msg);
			msg = Utils.getInstance().replaceIgnoreCase(msg, "%num%",
					Integer.toString(counter));
			msg = Utils.getInstance().replaceIgnoreCase(msg, "%url%", voteURL);
			msg = Utils.getInstance().replaceIgnoreCase(msg, "%SiteName%",
					voteSite.getSiteName());
			sites.add(msg);
		}
		sites = Utils.getInstance().colorize(sites);
		return Utils.getInstance().convertArray(sites);
	}
}