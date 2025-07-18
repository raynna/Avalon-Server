package com.rs.java.utils;

import com.rs.core.cache.defintions.ItemDefinitions;
import com.rs.java.game.item.Item;
import com.rs.java.game.player.content.ItemConstants;

public final class EconomyPrices {

	public static int getPrice(int itemId) {
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
		ItemDefinitions defs2 = ItemDefinitions.getItemDefinitions(defs.getCertId());
		if (defs.isNoted())
			return defs2.getValue();
		else if (defs.isLended())
			itemId = defs.getLendId();
		else if (!ItemConstants.isTradeable(new Item(itemId, 1))) {
			if (defs.getValue() > 0)
				return defs.getValue();
			return defs.getPrice();
		}
		else if (itemId == 995)
			return 1;
		return defs.getValue();
	}

	private EconomyPrices() {

	}
}
