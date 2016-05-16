package com.safeway.app.emju.mylist.comparator;

import java.util.Comparator;

import com.safeway.app.emju.mylist.model.AllocatedOffer;

public class OfferComparator implements Comparator<AllocatedOffer> {

	private static final long serialVersionUID = 4160586769059270963L;

    @Override
    public int compare(AllocatedOffer o1, AllocatedOffer o2) {
        
        String titleDsc1 = 
        		o1.getOfferDetail().getTitleDsc1() == null ? "|" : (String)o1.getOfferDetail().getTitleDsc1();
        String titleDsc2 = 
        		o2.getOfferDetail().getTitleDsc1() == null ? "|" : (String)o2.getOfferDetail().getTitleDsc1();
        
        return titleDsc1.compareTo(titleDsc2);
    }
}
