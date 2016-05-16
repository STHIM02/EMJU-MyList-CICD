
package com.safeway.app.emju.mylist.purchasehistory.model.json;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.safeway.app.emju.mylist.purchasehistory.model.OfferHierarchy;

/**
 * 
 * @author sshar64
 *
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class PurchasedItemOffers {

    private PurchasedItemOffer[] items;
    private OfferHierarchy[] categories;

    /**
     * Returns the items.
     * 
     * @return PurchasedItemOffer[]
     */
    public PurchasedItemOffer[] getItems() {
        return items;
    }

    /**
     * @param items
     *            The items to set.
     */
    public void setItems(final PurchasedItemOffer[] items) {
        this.items = items;
    }

    /**
     * Returns the categories.
     * 
     * @return OfferHierarchy[]
     */
    public OfferHierarchy[] getCategories() {
        return categories;
    }

    /**
     * @param categories
     *            The categories to set.
     */
    public void setCategories(final OfferHierarchy[] categories) {
        this.categories = categories;
    }


}
