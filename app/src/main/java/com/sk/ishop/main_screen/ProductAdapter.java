package com.sk.ishop.main_screen;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.sk.ishop.MainActivity;
import com.example.sk.ishop.R;
import com.sk.ishop.db.AppDataBase;
import com.sk.ishop.infrastructure.UIProduct;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by sk on 19/12/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ItemViewHolder> {

    private final String TAG = "ProductAdapter";

    // saved as an activity for curtain features
    private Activity activity;
    private List<UIProduct> UIProducts;
    private int tabDisplayed;
    private AppDataBase db;

    public ProductAdapter(Activity activity, List<UIProduct> UIProducts, int tabDisplayed, AppDataBase db) {
        this.activity = activity;
        this.UIProducts = UIProducts;
        this.tabDisplayed = tabDisplayed;
        this.db = db;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(R.layout.fragment_main_item, parent, false);
        View deleteButton = view.findViewById(R.id.main_fragment_item_delete_button);
        TextView price = (TextView) view.findViewById(R.id.main_fragment_item_price_text_view);
        TextView name = (TextView) view.findViewById(R.id.main_fragment_item_name_text_view);
        ImageView productImage = (ImageView) view.findViewById(R.id.main_fragment_item_picture_image_view);
        RadioButton favorite = (RadioButton) view.findViewById(R.id.main_fragment_favorite_radio_button);

        return new ItemViewHolder(view,deleteButton,price,name,productImage,favorite);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.price.setText(new DecimalFormat("##.##").format(UIProducts.get(position).getPrice()));
        holder.name.setText(UIProducts.get(position).getName());
        Picasso.with(activity).load(UIProducts.get(position).getPic()).into(holder.productImage);

        if(UIProducts.get(position).isFavorite())
            holder.favorite.setChecked(true);
        else
            holder.favorite.setChecked(false);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tabDisplayed == 1) {
                    // notify the other tabs (Cart and History) about the change
                    ((MainActivity)activity).getFragments()[0].notifyFavoriteChanged(UIProducts.get(position),false);
                    ((MainActivity)activity).getFragments()[2].notifyFavoriteChanged(UIProducts.get(position),false);
                }

                final String prodID = UIProducts.get(position).getProd_id();
                UIProducts.remove(position);
                notifyItemRemoved(position);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        short inTab;
                        switch (tabDisplayed){
                            case 0:
                                inTab = (short)(db.productsDAO().getInCart(prodID) - 1);
                                db.productsDAO().updateCart(inTab,prodID);
                                break;
                            case 1:
                                db.productsDAO().updateFav((short)0,prodID);
                                break;
                            case 2:
                                db.productsDAO().updateHistory((short)0,prodID);
                                break;
                        }
                        if(tabDisplayed == 1)
                            db.productsDAO().updateFavorite(false,prodID);
                    }
                }).start();

            }
        });

        // manage the Favorite Radio button that in the item. changes if it in the favorites tab
        if(tabDisplayed != 1) {
            holder.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!UIProducts.get(position).isFavorite()){
                        ((MainActivity) activity).addFavorite(UIProducts.get(position),tabDisplayed);
                        //UIProducts.get(position).setFavorite(true);
                        String id = UIProducts.get(position).getProd_id();
                        for(int i = 0; i< UIProducts.size(); i++){
                            if(UIProducts.get(i).getProd_id().equals(id)) {
                                UIProducts.get(i).setFavorite(true);
                                notifyItemChanged(i);
                            }
                        }
                    }else{
                        ((MainActivity)activity).removeFavorite(UIProducts.get(position),tabDisplayed);
                        String id = UIProducts.get(position).getProd_id();
                        for(int i = 0; i< UIProducts.size(); i++){
                            if(UIProducts.get(i).getProd_id().equals(id)) {
                                UIProducts.get(i).setFavorite(false);
                                notifyItemChanged(i);
                            }
                        }
                    }
                }
            });
        }else{
            holder.favorite.setChecked(true);
            holder.favorite.setClickable(false);
        }

    }

    @Override
    public int getItemCount() {
        return UIProducts.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{

        View deleteButton;
        TextView price,name;
        ImageView productImage;
        RadioButton favorite;

        public ItemViewHolder(View itemView, View deleteButton, TextView price, TextView name, ImageView productImage, RadioButton favorite) {
            super(itemView);
            this.deleteButton = deleteButton;
            this.price = price;
            this.name = name;
            this.productImage = productImage;
            this.favorite = favorite;
        }
    }
}
