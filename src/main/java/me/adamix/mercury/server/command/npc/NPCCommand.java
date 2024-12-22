package me.adamix.mercury.server.command.npc;

import me.adamix.mercury.server.npc.NPC;
import me.adamix.mercury.server.player.MercuryPlayer;
import net.minestom.server.command.builder.Command;

public class NPCCommand extends Command {
	public NPCCommand() {
		super("npc");

		setDefaultExecutor((sender, ctx) -> {
			if (!(sender instanceof MercuryPlayer player)) {
				return;
			}

			String skinValue = "ewogICJ0aW1lc3RhbXAiIDogMTczNDg5OTExOTMzNiwKICAicHJvZmlsZUlkIiA6ICJiYTQ4MzgzNTVkYTU0YjAxOTJjNmQyODZmM2M0YzhmZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBREFNXzQ2NDQiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2YyMjQyY2U4YzM5MTI4NGYxOTU5NmNiM2E2NDVmNzU4MjBkZmVmNGZlZWY1ODUyZmI5OTFkOTIyZTlkOTZhMiIKICAgIH0sCiAgICAiQ0FQRSIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I0MGE5MmUzMmI1N2ZkNzMyYTAwZmMzMjVlN2FmYjAwYTdjYTc0OTM2YWQ1MGQ4ZTg2MDE1MmU0ODJjZmJkZSIKICAgIH0KICB9Cn0=";
			String skinSignature = "RTetYw9AbIW26BoxYQm7IrQRRyJXUlEQDLx+gsIoOC9T3gKFsjPaJFDxgjX+AG8UzHrgEfE5w5+RV72sVmXf5he/r3pV+x44jnnqkwKrVJNXlVX6hvQ3SBqxofzQKuCxYp66xbZc/iLnQEvazMWx5bawLePuCSR6+F/7SGFX6LdTwD+FeJ5s09wrt8BlEDMbw5dEVz9jGWpWzcDp91RcMxovLUMZTqvBo08bedcjKowtKr57olvV6Vivtql6Yb0zD/g7sfI6acGZdUWLuCVmlamMtCZVuf7QnbuqdYCbUL47133xddC8PSaQ2udxxPaXTgoIIxhXynTjxXOgBufTeez7CERW/QldZGSQuCQW4Q7oGwJ6VVOIDkCj6057ls+pA0H+/0knLw63dE3YPOzl/CH3U1edfGJuo4JafWx2oMN5a7bp9OiYTvBABCQvUJLzmhaeMQyG6uSOkN8AbLs1mN62QNWPjyp1lqAobV1NpJNEeDdpt7w6Djt0vLma6X1yVkjxmx6bi5+jT+Vg2/ICH3LqIlZqWITIjkengY8BEJHOjAxxGd4o6H6YNH/Qqfa9oa/Uq2/P6sayPhwMHgpY8TvCarWBSLDCGeKva8PkpuIMpg1b2rEOXDBGmGuUvi6PrDDRlIcc+WNLVlwiN+BkRWtjiggJPXfY/SC81QAtzJ8=";

			NPC npc = new NPC("Test NPC", skinValue, skinSignature);
			npc.setInstance(player.getInstance(), player.getPosition());

		});
	}
}
