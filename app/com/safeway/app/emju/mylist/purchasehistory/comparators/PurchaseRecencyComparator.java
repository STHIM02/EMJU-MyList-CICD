package com.safeway.app.emju.mylist.purchasehistory.comparators;


import java.io.Serializable;
import java.util.Comparator;

import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffer;

/**
 * 
 * @author sshar64
 *
 */
public class PurchaseRecencyComparator implements Comparator<PurchasedItemOffer>, Serializable {

    private static final long serialVersionUID = 7662389695509569901L;

    @Override
    public int compare(final PurchasedItemOffer o1, final PurchasedItemOffer o2) {

        StringBuilder o1String = new StringBuilder();
        o1String.append(o1.getTitleDsc1());

        StringBuilder o2String = new StringBuilder();
        o2String.append(o2.getTitleDsc1());

        if (o1.getTitleDsc1().equalsIgnoreCase(o2.getTitleDsc1())) {
            o1String.append(o1.getProdDsc1());
            o2String.append(o2.getProdDsc1());
        }

        if (o1.getLastPurchasedTs() != null && o2.getLastPurchasedTs() == null) {
            return -1;
        }
        else if (o2.getLastPurchasedTs() != null && o1.getLastPurchasedTs() == null) {
            return 1;
        }
        else if (o1.getLastPurchasedTs() == null && o2.getLastPurchasedTs() == null) {
            return o1String.toString().compareToIgnoreCase(o2String.toString());
        }
        else if (o1.getLastPurchasedTs().after(o2.getLastPurchasedTs())) {
            return -1;
        }
        else if (o2.getLastPurchasedTs().after(o1.getLastPurchasedTs())) {
            return 1;
        }
        else {
            return o1String.toString().compareToIgnoreCase(o2String.toString());
        }
    }
}
