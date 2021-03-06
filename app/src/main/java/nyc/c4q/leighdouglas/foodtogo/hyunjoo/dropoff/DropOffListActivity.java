package nyc.c4q.leighdouglas.foodtogo.hyunjoo.dropoff;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nyc.c4q.leighdouglas.foodtogo.R;
import nyc.c4q.leighdouglas.foodtogo.hyunjoo.yelp.YelpAdapter;
import nyc.c4q.leighdouglas.foodtogo.hyunjoo.yelp.yelpinfo.YelpHomelessShelters;
import nyc.c4q.leighdouglas.foodtogo.hyunjoo.yelp.yelpinfo.YelpSource;
import nyc.c4q.leighdouglas.foodtogo.jon.animations.Animations;
import nyc.c4q.leighdouglas.foodtogo.leigh.ExitAppDialog;
import nyc.c4q.leighdouglas.foodtogo.leigh.RestaurantExtras;
import nyc.c4q.leighdouglas.foodtogo.leigh.RestaurantListActivity;

/**
 * Created by Hyun on 2/18/17.
 */
public class DropOffListActivity extends AppCompatActivity {
    private List<YelpHomelessShelters> mList = new ArrayList<>();
    private RecyclerView mDropOffRecycler;
    private Button optOutBttn;
    private boolean userClaimed = false;
    private CardView claimedRestaurant;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropoff);

        initViews();
        showRecyclerView();
        claimCheckerDisplay();

        optOutBttn = (Button) findViewById(R.id.opt_out);
        optOutBttn.setOnClickListener(view -> {
            int NOTIFICATION_ID = 123;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.leaflogo_small)
                    .setContentTitle("On no!")
                    .setContentText("The user is no longer able to pick-up");

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(NOTIFICATION_ID, builder.build());

            view.getContext().startActivity(new Intent(getApplicationContext(), RestaurantListActivity.class));
            finish();
        });

    }

    synchronized private void showRecyclerView() {
        mDropOffRecycler = (RecyclerView) findViewById(R.id.drop_off_recyclerview);
        mDropOffRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mDropOffRecycler.setAdapter(new YelpAdapter(mList));
        getData();
    }

    synchronized private void getData() {
        YelpSource.getHomelessList(mDropOffRecycler);
    }

    private void initViews() {
        optOutBttn = (Button) findViewById(R.id.opt_out);
        claimedRestaurant = (CardView) findViewById(R.id.claimed_restaurant);
        claimedRestaurant.setVisibility(View.GONE);
    }

    synchronized private void claimCheckerDisplay() {
        if (getIntent().getIntExtra(RestaurantExtras.CLAIMED, 0) == 1) {
            userClaimed = true;
        }

        if (userClaimed) {

            showClaimedRestaurant();
            TextView name = (TextView) findViewById(R.id.nameOfBusiness);
            TextView address1 = (TextView) findViewById(R.id.address1);
            TextView address2 = (TextView) findViewById(R.id.address2);

            TextView phone = (TextView) findViewById(R.id.claimedPhone);

            TextView time = (TextView) findViewById(R.id.claimedtime);

            ImageButton directions = (ImageButton) findViewById(R.id.claimed_directions);

            String line1 = getIntent().getStringExtra(RestaurantExtras.ADDRESS1);
            name.setText(getIntent().getStringExtra(RestaurantExtras.BUSINESS_NAME));
            address1.setText(getIntent().getStringExtra(RestaurantExtras.ADDRESS1));
            address2.setText(getIntent().getStringExtra(RestaurantExtras.ADDRESS2));
            phone.setText(getIntent().getStringExtra(RestaurantExtras.PHONE));
            time.setText(getIntent().getStringExtra(RestaurantExtras.TIME));

            directions.setOnClickListener(view -> {
                Log.d("view holder", "onClick: " + "should start intent");

                String uri = "http://maps.google.com/maps?daddr=" + line1;
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(uri));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                view.getContext().startActivity(intent);
            });
        }
    }

    private void showClaimedRestaurant() {
        Animations anim = new Animations(getApplicationContext());
        claimedRestaurant.setVisibility(View.VISIBLE);
        anim.slideInFromBottom(claimedRestaurant);
    }

    @Override
    public void onBackPressed() {
        if (userClaimed) {
            ExitAppDialog d = new ExitAppDialog();
            d.show(getSupportFragmentManager(), "dialog");
        } else {
            super.onBackPressed();
        }
    }
}
