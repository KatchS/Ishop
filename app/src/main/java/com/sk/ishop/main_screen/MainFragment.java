package com.sk.ishop.main_screen;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sk.ishop.MainActivity;
import com.example.sk.ishop.R;
import com.sk.ishop.db.AppDataBase;
import com.sk.ishop.db.Products;
import com.sk.ishop.infrastructure.UIProduct;

import java.util.ArrayList;
import java.util.List;

import static com.sk.ishop.infrastructure.Variables.TAB_NUMBER;

/**
 * Created by sk on 19/12/2017.
 */

public class MainFragment extends Fragment {

    private final String TAG = "MainFragment";

    private ArrayList<UIProduct> UIProducts = null;
    private ProductAdapter adapter;
    private AppDataBase db;
    private String[] tab = {"cart", "fav", "history"};
    private int tabDisplayed;

    /**
     * create a fragment object (from MainFragment)
     *
     * @param tabNumber the tab that the fragment is going to represent
     * @param db the db object to be passed to the fragment
     * @return a MainFragment object base on the given tab
     */
    public static MainFragment newInstace(int tabNumber,AppDataBase db){
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TAB_NUMBER,tabNumber);
        fragment.setArguments(bundle);
        fragment.setDb(db);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: has been used");
        tabDisplayed = getArguments().getInt(TAB_NUMBER);
        View view;
        final ScannerFragment scannerFragment = new ScannerFragment();


        // check if the CART tab is being open, then another button needs to be in the layout
        if(tabDisplayed == 0) {
            view = inflater.inflate(R.layout.fragment_main_cart, container, false);
            view.findViewById(R.id.main_fragment_cart_add_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFragmentManager()
                            .beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.main_activity_frame_layout, scannerFragment)
                            .commit();
                    ((MainActivity)getActivity()).scannerOpened();
                }
            });
        }else {
            view = inflater.inflate(R.layout.fragment_main, container, false);
        }

        if(UIProducts == null)
            UIProducts = getProducts(tabDisplayed);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.main_fragment_recycler_view);
        adapter = new ProductAdapter(getActivity(), UIProducts,tabDisplayed, db);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    /**
     * reads from the matching sql table (according to the opened tab), and get all the UIProducts
     *
     * @param tabDisplayed represent the opened tab
     * @return a List containing all the relevant UIProducts to the tab
     */
    private ArrayList<UIProduct> getProducts(int tabDisplayed) {
        List<Products> fromDb = db.productsDAO().getFromTab(tab[tabDisplayed]);
        ArrayList<UIProduct> result = new ArrayList<>();
        for(int i = 0; i<fromDb.size();i++){
            Products products = fromDb.get(i);
            for(int j=0; j<products.getNumInColumn(tabDisplayed); j++) {
                UIProduct UIProduct = new UIProduct(products.getName(), products.getPic(), products.getProd_id(), products.getPrice(), products.isFavorite());
                result.add(UIProduct);
            }
        }
        return result;
    }

    /**
     * add a UIProduct object to the ArrayList shown by the RecyclerView (see oneOfKind
     *
     * @param UIProduct the UIProduct to be added to the RecyclerView
     * @param oneOfKind true it there is no place to be more then one of a UIProduct
     */
    public void addToProducts(UIProduct UIProduct, boolean oneOfKind){
        boolean adapterExist = checkPrerequisite(getArguments().getInt(TAB_NUMBER));
        boolean run = true;
        if(oneOfKind)
            if(searchProduct(UIProduct) != -1)
                run = false;
        if(run) {
            UIProducts.add(UIProduct);
            if (adapterExist)
                adapter.notifyItemInserted(UIProducts.size());
        }
    }

    /**
     * remove the given UIProduct from the adapter (using the UIProduct id)
     *
     * already notify the adapter, if it exist
     *
     * @param UIProduct the UIProduct to be removed
     */
    public void removeFromProducts(UIProduct UIProduct){
        boolean adapterExist = checkPrerequisite(getArguments().getInt(TAB_NUMBER));
        int index = searchProduct(UIProduct);
        if(index != -1) {
            UIProducts.remove(index);
            if (adapterExist)
                adapter.notifyItemRemoved(index);
        }
    }

    public void removeAllProducts(){
        boolean adapterExist = checkPrerequisite(getArguments().getInt(TAB_NUMBER));
        UIProducts.clear();
        if(adapterExist)
            adapter.notifyDataSetChanged();
    }

    /**
     * check if UIProducts and the adapter had been initialized
     *
     * <p>if UIProducts have not been, it initialized it (doesn't do it for adapter)</p>
     *
     * @param tabDisplayed the tab that this object of mainFragment represent
     * @return true if the adapter has been initialized, false otherwise
     */
    private boolean checkPrerequisite(int tabDisplayed){
        if(UIProducts == null)
            UIProducts = getProducts(tabDisplayed);
        if(adapter == null)
            return false;
        return true;
    }

    /**
     * norify a fragment that the given UIProduct has changed its favorite status
     *
     * @param UIProduct the UIProduct that have changed
     * @param favorite true if it is now favorite, false otherwise
     */
    public void notifyFavoriteChanged(UIProduct UIProduct, boolean favorite){
        boolean adapterExist = checkPrerequisite(getArguments().getInt(TAB_NUMBER));
        if(tabDisplayed == 2) {
            int index = searchProduct(UIProduct);
            if (index != -1) {
                UIProducts.get(index).setFavorite(favorite);
                if (adapterExist)
                    adapter.notifyItemChanged(index);
            }
        }if(tabDisplayed == 0){
            for(UIProduct p : UIProducts){
                if(p.getProd_id().equals(UIProduct.getProd_id()))
                    p.setFavorite(favorite);
            }
            if (adapterExist)
                adapter.notifyDataSetChanged();
        }
    }

    /**
     * search for the given UIProduct in the UIProducts array
     *
     * @param UIProduct the UIProduct to search
     * @return the index of the UIProduct in UIProducts, -1 if it doesn't exist
     */
    private int searchProduct(UIProduct UIProduct){
        for(int i = 0; i< UIProducts.size(); i++){
            if(UIProducts.get(i).getProd_id().equals(UIProduct.getProd_id())){
                return i;
            }
        }
        return -1;
    }

    /**
     * sum the overall price of all the UIProducts that inside the array
     * @return int - represent the total amount
     */
    public float sumPrice(){
        float sum = 0;
        for(int i = 0; i< UIProducts.size(); i++){
            sum += UIProducts.get(i).getPrice();
        }
        return sum;
    }

    private void setDb(AppDataBase db) {
        this.db = db;
    }
}
