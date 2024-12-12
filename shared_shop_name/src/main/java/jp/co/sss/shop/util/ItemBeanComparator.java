package jp.co.sss.shop.util;

import java.util.Comparator;

import jp.co.sss.shop.bean.ItemBean;
import jp.co.sss.shop.entity.Item;

public class ItemBeanComparator implements Comparator<ItemBean> {
	public int compare(ItemBean o1, ItemBean o2) {
		
		if (o1.getTotalQuantity() < o2.getTotalQuantity()) {
			return 1;
		} else if (o1.getTotalQuantity() > o2.getTotalQuantity()){
			return -1;
		} else {
			if (o1.getId() < o2.getId()){
				return -1;
			} else {
				return 1;
			}
		}
	};
}
