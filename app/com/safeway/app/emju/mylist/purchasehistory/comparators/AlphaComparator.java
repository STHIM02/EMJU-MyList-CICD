
package com.safeway.app.emju.mylist.purchasehistory.comparators;


import java.io.Serializable;
import java.util.Comparator;

import com.safeway.app.emju.mylist.purchasehistory.model.json.PurchasedItemOffer;

/**
 * 
 * @author sshar64
 *
 */
public class AlphaComparator implements Comparator<PurchasedItemOffer>, Serializable {

    private static final long serialVersionUID = 6835223711899479540L;

    @Override
    public int compare(final PurchasedItemOffer o1, final PurchasedItemOffer o2) {

    	StringBuilder o1String = new StringBuilder();
        o1String.append(o1.getTitleDsc1());
        o1String.append(o1.getProdDsc1());
        
        StringBuilder o2String = new StringBuilder();
        o2String.append(o2.getTitleDsc1());
        o2String.append(o2.getProdDsc1());

        return o1String.toString().compareToIgnoreCase(o2String.toString());
    }
}
