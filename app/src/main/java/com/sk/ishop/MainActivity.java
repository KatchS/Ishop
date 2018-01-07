package com.sk.ishop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sk.ishop.R;
import com.sk.ishop.checkout.CheckoutActivity;
import com.sk.ishop.db.Products;
import com.sk.ishop.infrastructure.BaseActivity;
import com.sk.ishop.infrastructure.UIProduct;
import com.sk.ishop.login.LoginActivity;
import com.sk.ishop.main_screen.FavoritesManagement;
import com.sk.ishop.main_screen.MainFragment;
import com.sk.ishop.main_screen.ScanListener;
import com.sk.ishop.server_request.OnConnectionListener;
import com.sk.ishop.infrastructure.Variables;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends BaseActivity implements TabLayout.OnTabSelectedListener,ScanListener,OnConnectionListener,FavoritesManagement {

    private final String TAG = "MainActivity";
    private final int CHECKOUT_REQUEST_CODE = 8;

    private TabLayout.Tab firstTab;
    private MainFragment[] fragments;

    // handle the stack of the fragments
    private int currentTab;
    private boolean inScannerFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isLoggedIn();

        initFragments();
        initTabs();
    }

    /**
     * initialize the tab
     */
    private void initTabs() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_activity_tab_layout);


        firstTab = tabLayout.newTab().setText(R.string.Cart_tab);
        tabLayout.addTab(firstTab);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Favorites_tab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.History_tab));

        // change their color
        tabLayout.setTabTextColors(Color.parseColor("#c6c0c0"),Color.parseColor("#FFFFFFFF"));

        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_checkout:
                toCheckout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * go to checkout activity
     */
    private void toCheckout() {
        Intent intent = new Intent(this,CheckoutActivity.class);
        intent.putExtra("toPay",fragments[0].sumPrice());
        startActivityForResult(intent,CHECKOUT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECKOUT_REQUEST_CODE)
            if(resultCode == RESULT_OK){
                // the user has returned from Checkout Activity after pressing the "pay" button
                List<Products> inCart = db.productsDAO().getCartNotHistory();
                db.productsDAO().addCartToHisotory();
                for(Products p : inCart){
                    fragments[2].addToProducts(new UIProduct(p),true);
                }
                db.productsDAO().removeAllCart();
                fragments[0].removeAllProducts();
            }
    }

    private void initFragments() {
        fragments = new MainFragment[3];
        for(int i=0 ; i<fragments.length; i++)
            fragments[i] = MainFragment.newInstace(i,db);

        switchFragment(0);
    }

    private void logout() {
        preferences.edit().remove(Variables.LOGGED_USER).apply();
        isLoggedIn();
    }

    /**
     * checks if there is a user logged in in the moment this method runs
     */
    private void isLoggedIn() {
        if(!preferences.contains(Variables.LOGGED_USER)){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switchFragment(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void switchFragment(int tabNumber){
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(tabNumber == 0 ? "CART" : null)
                .replace(R.id.main_activity_frame_layout, fragments[tabNumber])
                .commit();
        currentTab = tabNumber;
    }

    @Override
    public void onBackPressed() {
        // always return to the cart tab
        if( currentTab != 0){
            getFragmentManager().popBackStack("CART",0);
            firstTab.select();
            super.onBackPressed();
        }else{
            // if the user is scanning, close the scanner
            if(inScannerFragment) {
                getFragmentManager().popBackStack("CART", 0);
                scannerClosed();
            }else
                finish();
        }
    }

    @Override
    public void scannerOpened() {
        inScannerFragment = true;
    }

    @Override
    public void scannerClosed() {
        inScannerFragment = false;
    }

    @Override
    public void onSuccess(JSONObject data) {
        try {
            if(data.getBoolean("status")){
                // the product was found on the server (by barcode)
                String name = data.getString("name");
                String pic = data.getString("picture");
                String prod_id = data.getString("prod_id");
                double price = data.getDouble("price");
                UIProduct UIProduct = new UIProduct(name,pic,prod_id,price,db.productsDAO().getFavorite(prod_id));
                fragments[0].addToProducts(UIProduct,false);
                Products products = new Products(name,price,pic,prod_id);
                sendToSql(products);
            } else {
                // the product was not found on the server (by barcode)
                Toast.makeText(this, R.string.barcode_not_found, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure() {
        failed();
    }

    private void sendToSql(final Products product) {
        new Thread(new Runnable() {

            private String prodId;

            @Override
            public void run() {
                prodId = product.getProd_id();
                Products existing = db.productsDAO().getProduct(prodId);
                if(existing != null){
                    // there was already a product in the db

                    if(!existing.getName().equals(product.getName())){
                        db.productsDAO().updateName(product.getName(),prodId);
                    }
                    if(!existing.getPic().equals(product.getPic())){
                        db.productsDAO().updatePic(product.getPic(),prodId);
                    }
                    if(existing.getPrice() != product.getPrice()){
                        db.productsDAO().updatePrice(product.getPrice(),prodId);
                    }

                    Log.d(TAG, "run: the number in cart is: " + existing.getCart());
                    int inCart = existing.getCart() + 1;
                    db.productsDAO().updateCart((short)inCart,prodId);

                }else{
                    // this is a new product
                    Log.d(TAG, "run: the product exist");
                    product.setCart((short)1);
                    db.productsDAO().insertP(product);
                }
            }
        }).start();
    }

    @Override
    public void addFavorite(final UIProduct UIProduct, int currentTab) {
        fragments[1].addToProducts(UIProduct,true);
        fragments[currentTab == 0 ? 2 : 0].notifyFavoriteChanged(UIProduct,true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.productsDAO().updateFavorite(true, UIProduct.getProd_id());
                db.productsDAO().updateFav((short)1, UIProduct.getProd_id());
            }
        }).start();

    }

    @Override
    public void removeFavorite(final UIProduct UIProduct, int currentTab) {
        fragments[1].removeFromProducts(UIProduct);
        fragments[currentTab == 0 ? 2 : 0].notifyFavoriteChanged(UIProduct,false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.productsDAO().updateFavorite(false, UIProduct.getProd_id());
                db.productsDAO().updateFav((short)0, UIProduct.getProd_id());
            }
        }).start();
    }

    public MainFragment[] getFragments() {
        return fragments;
    }
}
