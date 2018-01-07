package com.sk.ishop.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by sk on 13/12/2017.
 */

@Dao
public interface ProductsDAO {

    @Query("SELECT * FROM Products WHERE :tabColumn > 0")
    List<Products> getFromTab(String tabColumn);

    @Query("SELECT * FROM Products WHERE cart > 0 AND history = 0")
    List<Products> getCartNotHistory();

    @Query("SELECT cart FROM Products WHERE prod_id = :prod_id")
    short getInCart(String prod_id);

    @Query("SELECT favorite FROM Products WHERE prod_id = :prod_id")
    boolean getFavorite(String prod_id);

    @Query("SELECT * FROM Products WHERE prod_id = :prod_id")
    Products getProduct(String prod_id);

    @Query("SELECT name,cart FROM Products WHERE cart > 0")
    List<ProductInfo> getNameAndCart();

    @Query("UPDATE Products SET cart = 0 WHERE cart > 0")
    void removeAllCart();

    @Query("UPDATE Products SET name = :value WHERE prod_id = :prod_id")
    int updateName(String value, String prod_id);

    @Query("UPDATE Products SET pic = :value WHERE prod_id = :prod_id")
    int updatePic(String value, String prod_id);

    @Query("UPDATE Products SET price = :value WHERE prod_id = :prod_id")
    int updatePrice(double value, String prod_id);

    @Query("UPDATE Products SET cart = :value WHERE prod_id = :prod_id")
    int updateCart(short value, String prod_id);

    @Query("UPDATE Products SET fav = :value WHERE prod_id = :prod_id")
    int updateFav(short value, String prod_id);

    @Query("UPDATE Products SET history = :value WHERE prod_id = :prod_id")
    int updateHistory(short value, String prod_id);

    @Query("UPDATE Products SET history = 1 WHERE cart > 0 AND history = 0")
    void addCartToHisotory();

    @Query("UPDATE Products SET favorite = :value WHERE prod_id = :prod_id")
    int updateFavorite(boolean value, String prod_id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertP(Products product);
}
