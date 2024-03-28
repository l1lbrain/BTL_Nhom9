package com.example.ecommerceapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.models.NewProductsModel;
import com.example.ecommerceapp.models.PopularProductsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.context.AttributeContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {

    Button addToCart;
    TextInputEditText name,price;
    TextView quantity;
    ImageView addItems,removeItems;
    int totalQuantity =1;
    int totalPrice =0;
    Toolbar toolbar;
    private FirebaseFirestore fireStore;
    private NewProductsModel newProductsModel;
    private PopularProductsModel polularProductsModel;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detailed);

        fireStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        toolbar= findViewById(R.id.detailed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //new
        totalPrice = newProductsModel.getPrice()*totalQuantity;

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity<10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));

                    if(newProductsModel!=null){
                        totalPrice=newProductsModel.getPrice()*totalQuantity;
                    }
                    if(polularProductsModel!=null){
                        totalPrice=polularProductsModel.getPrice()*totalQuantity;
                    }
                }

            }
        });
        removeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity>=1){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });
    }

    private void addToCart() {
        String saveCurrentDate,saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap<String,Object> cartMap =new HashMap<>();

        cartMap.put("productName",name.getText().toString());
        cartMap.put("productPrice",price.getText().toString());
        cartMap.put("currentTime",saveCurrentTime);
        cartMap.put("currentDate",saveCurrentDate);
        cartMap.put("totalQuantity",quantity.getText().toString());
        cartMap.put("totalPrice",totalPrice);

        fireStore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(DetailedActivity.this,"Added To A Cart",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}