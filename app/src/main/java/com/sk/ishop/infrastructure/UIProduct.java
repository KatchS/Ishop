package com.sk.ishop.infrastructure;

import com.sk.ishop.db.Products;

/**
 * Created by sk on 20/12/2017.
 *
 * used to contain data of a product for UI purposes
 */

public class UIProduct {

    private String name;
    private String pic;
    private String prod_id;
    private Double price;
    private boolean favorite;

    public UIProduct(String name, String pic, String prod_id, Double price, boolean favorite) {
        this.name = name;
        this.pic = pic;
        this.prod_id = prod_id;
        this.price = price;
        this.favorite = favorite;
    }

    /**
     * create a UIProduct from a Products object
     * @param product the Products object to create by
     */
    public UIProduct(Products product){
        name = product.getName();
        pic = product.getPic();
        prod_id = product.getProd_id();
        price = product.getPrice();
        favorite = product.isFavorite();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getProd_id() {
        return prod_id;
    }

    public void setProd_id(String prod_id) {
        this.prod_id = prod_id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
