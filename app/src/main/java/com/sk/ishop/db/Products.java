package com.sk.ishop.db;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by sk on 13/12/2017.
 */

@Entity(indices = {@Index(value = "prod_id",unique = true)})
public class Products {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private double price;
    private String prod_id;
    private String pic;

    private boolean favorite;

    private short cart;
    private short fav;
    private short history;

    public Products(String name, double price, String pic, String prod_id) {
        this.name = name;
        this.price = price;
        this.prod_id = prod_id;
        this.pic = pic;
    }

    public short getNumInColumn(int column){
        switch (column){
            case 0:
                return getCart();
            case 1:
                return getFav();
            default:
                return getHistory();
        }
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public short getCart() {
        return cart;
    }

    public short getFav() {
        return fav;
    }

    public short getHistory() {
        return history;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public void setCart(short cart) {
        this.cart = cart;
    }

    public void setFav(short fav) {
        this.fav = fav;
    }

    public void setHistory(short history) {
        this.history = history;
    }
}
