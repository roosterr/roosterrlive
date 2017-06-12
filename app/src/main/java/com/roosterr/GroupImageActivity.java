package com.roosterr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class GroupImageActivity extends AppCompatActivity {
    public Integer freeImage;
    int[] freeImagesList;
    String globalGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String groupid = getIntent().getStringExtra("groupid");
        this.globalGroup = groupid;

        final int[] freeImagesList = new int[]{
                R.drawable.ic_bell,//R.drawable.agentsmith,
                R.drawable.comedy2,//R.drawable.ameliepoulain,
                R.drawable.cryingbaby,//R.drawable.astronaut,
                R.drawable.babysroom
        };

        final int[] premiumImagesList = new int[]{
                R.drawable.banknotes,
                R.drawable.bavariangirl,
                //R.drawable.ic_bell,
                R.drawable.bird,
                R.drawable.bra,
                R.drawable.brain,
                R.drawable.bull,
                R.drawable.cashinhand,
                R.drawable.cheap2,
                R.drawable.chicken,
                //R.drawable.comedy2,
                R.drawable.couplemanwoman,
                R.drawable.cross,
                //R.drawable.cryingbaby,
                R.drawable.dancewithdevil,
                R.drawable.datemanwoman,
                R.drawable.dharmachakra,
                R.drawable.dog,
                R.drawable.donaldtrump,
                R.drawable.dumbbell,
                R.drawable.eastercake,
                R.drawable.einstein,
                R.drawable.elephant,
                R.drawable.englishmustache,
                R.drawable.floatingguru,
                R.drawable.geisha,
                R.drawable.girl,
                R.drawable.guestfemale,
                R.drawable.guestmale,
                R.drawable.gymnastics,
                R.drawable.headinsand,
                R.drawable.heartballoon,
                R.drawable.heartwitharrow,
                R.drawable.innovation,
                R.drawable.likefilled,
                R.drawable.lion,
                R.drawable.lips,
                R.drawable.maneki,
                R.drawable.mensunderwear,
                R.drawable.messi,
                R.drawable.moneybag,
                R.drawable.morpheus,
                R.drawable.neo,
                R.drawable.novel,
                R.drawable.oldmanskintype12,
                R.drawable. oldwoman,
                R.drawable.poison,
                R.drawable.poo,
                R.drawable.pranava,
                R.drawable.running,
                R.drawable.santa,
                R.drawable.selfdestructbutton,
                R.drawable.starcrescent,
                R.drawable.starfilled,
                R.drawable.stevejobs,
                R.drawable.sumo,
                R.drawable.tango,
                R.drawable.trinity,
                R.drawable.turtle,
                R.drawable.twohearts,
                R.drawable.unicorn,
                R.drawable.vampire,
                R.drawable.visa,
                R.drawable.witch,
                R.drawable.womensunderwear,
                R.drawable.wrestling,
                R.drawable.yearofmonkey,
                R.drawable.yearofsnake
        };

        final String go_pro =  getSharedPreferences("Purchase_Type", 0).getString("go_pro", null);
        final String both_products =  getSharedPreferences("Purchase_Type", 0).getString("both", null);

        Button freeButton = (Button) findViewById(R.id.button_free);
        freeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Handle click
                Bitmap bitmap = GroupImageActivity.drawableToBitmap(GroupImageActivity.this.getResources().getDrawable(freeImagesList[freeImage]));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                new JavaScriptInterface(GroupImageActivity.this.getApplicationContext()).saveGroupImage(groupid, Base64.encode(stream.toByteArray()));
                GroupImageActivity.this.startActivity(new Intent(GroupImageActivity.this, EditActivity.class).putExtra("groupid", groupid));

            }
        });

        Button premiumButton = (Button) findViewById(R.id.button_premium);

        if(go_pro.equals("1") || both_products.equals("1")){
            premiumButton.setText(GroupImageActivity.this.getResources().getString(R.string.premium_image_label));
        }

        premiumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Handle click
                if(go_pro.equals("1") || both_products.equals("1")) {
                    try {
                        Bitmap bitmap = GroupImageActivity.drawableToBitmap(GroupImageActivity.this.getResources().getDrawable(premiumImagesList[freeImage]));
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        new JavaScriptInterface(GroupImageActivity.this.getApplicationContext()).saveGroupImage(groupid, Base64.encode(stream.toByteArray()));
                        GroupImageActivity.this.startActivity(new Intent(GroupImageActivity.this, EditActivity.class).putExtra("groupid", groupid));
                    }
                    catch (Exception ex){

                    }
                }
                else{
                    String userId =  getSharedPreferences("AzureUser", 0).getString("azureuser", null);
                    String phoneNumber = getSharedPreferences("AzureUser", 0).getString("phone_number", null);

                    startActivity(new Intent(GroupImageActivity.this, UpgradeActivity.class)
                            .putExtra("UserID",userId)
                            .putExtra("Phone",phoneNumber)
                    );
                }
            }
        });

        Button browseButton = (Button) findViewById(R.id.button_browse);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Handle click
                GroupImageActivity.this.startActivity(new Intent(GroupImageActivity.this, FileActivity.class).putExtra("groupid", groupid));
            }

        });

        RecyclerView freeRecyclerView = (RecyclerView) findViewById(R.id.list_group_image_free);
        freeRecyclerView.setHasFixedSize(true);
        freeRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        freeRecyclerView.setAdapter(new GroupImageRecyclerViewAdapter(freeImagesList));

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.activity_margin_small);
        freeRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, spacingInPixels, true));

        RecyclerView premiumRecyclerView = (RecyclerView) findViewById(R.id.list_group_image_premium);
        premiumRecyclerView.setHasFixedSize(true);
        premiumRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        premiumRecyclerView.setAdapter(new GroupImageRecyclerViewAdapter(premiumImagesList));
        premiumRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, spacingInPixels, true));

    }

    private class GroupImageRecyclerViewAdapter extends RecyclerView.Adapter<GroupImageRecyclerViewAdapter.ViewHolder> {
        private final int[] mList;
        private int selectedPos = -1;

        public GroupImageRecyclerViewAdapter(int[] list) {
            this.mList = list;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mImage.setImageResource(mList[position]);
            GroupImageActivity.this.freeImage = Integer.valueOf(this.selectedPos);
            holder.mCheckmark.setSelected(selectedPos == position);
        }

        @Override
        public int getItemCount() {
            return mList.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageView mImage;
            public View mView;
            public ImageView mCheckmark;

            public ViewHolder(View view) {
                super(view);
                mImage = (ImageView) view.findViewById(R.id.image);
                mCheckmark = (ImageView) view.findViewById(R.id.checkmark);
                mView = view;

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        notifyItemChanged(selectedPos);
                        selectedPos = getLayoutPosition();
                        notifyItemChanged(selectedPos);
                    }
                });
            }
        }
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int i = 1;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int i2;
        int width = !drawable.getBounds().isEmpty() ? drawable.getBounds().width() : drawable.getIntrinsicWidth();
        int height = !drawable.getBounds().isEmpty() ? drawable.getBounds().height() : drawable.getIntrinsicHeight();
        if (width <= 0) {
            i2 = 1;
        } else {
            i2 = width;
        }
        if (height > 0) {
            i = height;
        }
        Bitmap bitmap = Bitmap.createBitmap(i2, i, Bitmap.Config.ARGB_8888);
        Log.v("Bitmap width - Height :", width + " : " + height);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, EditActivity.class).putExtra("groupid", this.globalGroup));
        finish();
    }
}
