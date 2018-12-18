package com.g05.itkmitl.multioder.food;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.g05.itkmitl.multioder.R;
import com.g05.itkmitl.multioder.comment.Comment;
import com.g05.itkmitl.multioder.comment.CommentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class FoodDetailActivity extends AppCompatActivity {
    private Food food;
    private List<Comment> comments = new ArrayList<>();

    private ImageView foodImage;
    private TextView foodName;
    private TextView foodDes;
    private TextView foodPrice;
    private EditText commentInput;
    private Button addToCart, postComment;
    private RecyclerView commentListView;
    private CommentAdapter adapter;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Food Detail");
        }

        food = (Food) getIntent().getSerializableExtra("food");
        foodImage = findViewById(R.id.food_detail_image);
        foodName = findViewById(R.id.food_detail_name);
        foodDes = findViewById(R.id.food_detail_descrip);
        foodPrice = findViewById(R.id.food_detail_price);
        commentInput = findViewById(R.id.comment_input);
        commentListView = findViewById(R.id.comment_list);

        adapter = new CommentAdapter(this, comments);
        commentListView.setAdapter(adapter);
        commentListView.setLayoutManager(new LinearLayoutManager(this));
        commentListView.setHasFixedSize(true);

        foodName.setText(food.getName());
        foodDes.setText(food.getDescription());
        foodPrice.setText(food.getPrice() + " Baht");
        Picasso.get().load(food.getUrl()).fit().centerCrop().into(foodImage);

        initButton();
        loadComment();
    }

    private void initButton() {
        addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Added", Toast.LENGTH_SHORT).show();
                addFoodToCart();
                finish();
            }
        });

        postComment = findViewById(R.id.comment_post_btn);
        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = commentInput.getText().toString();
                if (!message.isEmpty()) {
                    postComment(message);
                    commentInput.setText(null);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void addFoodToCart() {
//        food.setDeleteKey(System.currentTimeMillis()+"");
//        auth.getCurrentUser().getUid();
//        firestore.collection("Users")
//                .document(auth.getCurrentUser().getUid())
//                .collection("cart")
//                .document(food.getDeleteKey())
//                .set(food);
    }

    private void loadComment() {
        firebaseFirestore.collection("restaurant")
                .document(food.getRestaurantID())
                .collection("food")
                .document(food.getUid())
                .collection("comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        comments.clear();
                        for(QueryDocumentSnapshot item : queryDocumentSnapshots) {
                            comments.add(item.toObject(Comment.class));
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void postComment(String message) {
        String commentID = "comment_" + System.currentTimeMillis();

        Comment comment = new Comment();
        comment.setMessage(message);
        comment.setUserID(auth.getCurrentUser().getUid());
        comment.setDate(new Date());

        firebaseFirestore.collection("restaurant")
                .document(food.getRestaurantID())
                .collection("food")
                .document(food.getUid())
                .collection("comments")
                .document(commentID)
                .set(comment);
    }
}
