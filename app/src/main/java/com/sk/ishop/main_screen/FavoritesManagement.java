package com.sk.ishop.main_screen;

import com.sk.ishop.infrastructure.UIProduct;

/**
 * Created by sk on 19/12/2017.
 */

public interface FavoritesManagement {

    void addFavorite(UIProduct UIProduct, int currentTab);

    void removeFavorite(UIProduct UIProduct, int currentTab);

}
