package com.sk.ishop.checkout;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sk.ishop.R;
import com.sk.ishop.db.ProductInfo;
import com.sk.ishop.infrastructure.BaseActivity;
import com.sk.ishop.infrastructure.Variables;

import java.util.List;

public class CheckoutActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private final String TAG = "CheckoutActivity";

    private int year; // not in used in this demo
    private int month; // not in used in this demo
    private String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initPrice();
        initSpinners();

        findViewById(R.id.checkout_activity_pay_button).setOnClickListener(this);
        findViewById(R.id.checkout_activity_show_products_button).setOnClickListener(this);
    }

    private void initPrice() {
        TextView price = ((TextView) findViewById(R.id.checkout_activity_amount_to_pay_text_view));
        price.setText(String.valueOf(getIntent().getFloatExtra("toPay",-1)));
    }

    private void initSpinners() {
        Spinner monthSpinner = (Spinner) findViewById(R.id.checkout_activity_month_spinner);
        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this,R.array.months,android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setOnItemSelectedListener(this);

        Spinner yearSpinner = (Spinner) findViewById(R.id.checkout_activity_year_spinner);
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this,R.array.years,android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.checkout_activity_month_spinner:
                month = Integer.parseInt((String)parent.getItemAtPosition(position));
                break;
            case R.id.checkout_activity_year_spinner:
                year = Integer.parseInt((String)parent.getItemAtPosition(position));
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.checkout_activity_show_products_button:
                showProducts();
                break;
            case R.id.checkout_activity_pay_button:
                pay();
                break;
        }
    }

    private void pay() {
        setResult(RESULT_OK);
        finish();
    }

    /**
     * display a Dialog containing the names of the products in the Cart tab
     */
    private void showProducts() {
        // take data from db if not already had
        if(names == null)
            createNamesArray();

        // pass it into a bundle
        Bundle bundle = new Bundle();
        bundle.putStringArray(Variables.NAMES,names);

        // display the dialog
        ProductsDialog dialog = new ProductsDialog();
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(),null);
    }

    /**
     * create an array with String representing the products and quantity of the buyer
     */
    private void createNamesArray() {
        List<ProductInfo> fromDb = db.productsDAO().getNameAndCart();
        names = new String[fromDb.size()];
        for(int i=0; i<fromDb.size(); i++){
            ProductInfo p = fromDb.get(i);
            names[i] = p.getName() + " x" + p.getCart();
        }
    }
}
