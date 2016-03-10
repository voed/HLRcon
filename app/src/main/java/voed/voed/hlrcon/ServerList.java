package voed.voed.hlrcon;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import voed.voed.hlrcon.enums.IMGEnum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;

import com.afollestad.materialdialogs.MaterialDialog;
import voed.voed.hlrcon.steamcondenser.exceptions.SteamCondenserException;
import voed.voed.hlrcon.steamcondenser.steam.servers.GoldSrcServer;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class ServerList extends AppCompatActivity
{
    public static TypedArray images;
    public static int menupos;
    public static Drawer drawerResult;
    public DragSortListView listview;
    public ServerListAdapter adapter;
    public MaterialProgressBar progressBar;
    public Connect connect;
    public static Typeface Roboto;
    public static int listposition;
    public static boolean backpressed = false;
    public FloatingActionButton bAdd;
    public static boolean isRefreshing = false;
    public SharedPreferences SP;
    //public SwipeRefreshLayout swipeContainer;

    /*@Override
    public void onRefresh() {
        // говорим о том, что собираемся начать
        Toast.makeText(this, "started", Toast.LENGTH_SHORT).show();
        // начинаем показывать прогресс
        swipeContainer.setRefreshing(true);
        // ждем 3 секунды и прячем прогресс
        swipeContainer.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(false);
                // говорим о том, что собираемся закончить
                Toast.makeText(ServerList.this, "finished", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }*/

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            if (!isRefreshing && isRefreshEnabled()) {
                refresh();
            }

            timerHandler.postDelayed(this, getRefreshInt() * 60000);

        }
    };

    public int getRefreshInt()
    {
        String value = SP.getString("prefAutoRefreshInt", null);
        if(value == null)
        {
            SharedPreferences.Editor editor = SP.edit();
            editor.putString("prefAutoRefreshInt", "1");
            editor.apply();
            return 1;
        }
        return Integer.valueOf(value);
    }

    public boolean isRefreshEnabled()
    {
        return SP.getBoolean("prefAutoRefresh", false);
    }

    public void setRefreshEnabled(boolean state)
    {
        SharedPreferences.Editor editor = SP.edit();
        editor.putBoolean("prefAutoRefresh", state);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servers_list);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.sltoolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new DrawerBuilder().withActivity(this).build();

        drawerResult = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.drawer_header)
                .withActionBarDrawerToggleAnimated(true)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.app_name).withIdentifier(1).withIcon(FontAwesome.Icon.faw_home),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.action_settings).withIcon(FontAwesome.Icon.faw_cog)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        drawerResult.closeDrawer();
                        switch (position) {
                            case 3: { // settings
                                Intent myIntent = new Intent(ServerList.this, SettingsActivity.class);
                                startActivity(myIntent);
                                break;
                            }
                        }
                        return true;
                    }
                }).build();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);


        // set a custom tint color for all system bars
        tintManager.setTintResource(R.color.Primary);
        //tintManager.setNavigationBarTintColor();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.StatusBar));
        }

        Globals.readXml();

        Resources res = getResources();
        images = res.obtainTypedArray(R.array.images);

        progressBar = (MaterialProgressBar)findViewById(R.id.slprogress);
        //bAdd = (ButtonFloat) findViewById(R.id.buttonFloat);
        bAdd = (FloatingActionButton)findViewById(R.id.buttonFloat);
        listview = (DragSortListView) findViewById(R.id.list);

        //swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        //swipeContainer.setOnRefreshListener(this);

        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //int topRowVerticalPosition = (listview == null || listview.getChildCount() == 0) ? 0 : listview.getChildAt(0).getTop();
                if (!isRefreshing && (visibleItemCount != totalItemCount)) {
                    if ((totalItemCount - view.getLastVisiblePosition()) < 2) {
                        hidebutton();
                    } else {
                        showbutton();
                    }
                    //swipeContainer.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
                }

            }
        });
        listview.setDropListener(onDrop);
        connect = new Connect();
        refresh();

        adapter = new ServerListAdapter(getApplicationContext(), Globals.sList, ServerList.this);
        listview.setAdapter(adapter);
        //listview.setSwipeRefreshLayout(swipeContainer);
        /*DragSortController controller = new DragSortController(
                listview, R.styleable.DragSortListView_drag_handle_id, DragSortController.ON_LONG_PRESS, 0,
                0, 0, swipeContainer);
        listview.setFloatViewManager(controller);
        listview.setOnTouchListener(controller);*/



        bAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent myIntent = new Intent(ServerList.this, AddServer.class);
                startActivity(myIntent);
            }
        });

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        timerHandler.postDelayed(timerRunnable, getRefreshInt() * 60000);
    }

    @Override
    public void onBackPressed() {
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
            if(backpressed)
            {
                super.onBackPressed();
            }
            else
            {
                Snackbar.make(listview, "Нажмите еще раз для выхода" , Snackbar.LENGTH_SHORT).show();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        backpressed = false;
                    }
                }, 2000);
                backpressed = true;
            }


        }
    }

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    if(connect.getStatus() == AsyncTask.Status.RUNNING)
                        return;
                    Server server = Globals.sList.get(from);
                    Globals.sList.remove(from);
                    Globals.sList.add(to, server);
                    adapter.notifyDataSetChanged();
                    Globals.writeXml();
                    //adapter.remove(server);
                    //adapter.insert(server, to);
                }
            };

    public void refresh()
    {
        if(!isNetworkAvailable())
        {
            new MaterialDialog.Builder(this).title("HLRcon").content("Отсутствует интернет-соединение").positiveText("OK").show();
            return;
        }

        if(connect.getStatus() == AsyncTask.Status.RUNNING) {
            Snackbar.make(listview, "Список уже обновляется." , Snackbar.LENGTH_SHORT).show();
            return;
        }
        isRefreshing = true;
        connect = new Connect();
        connect.execute();
        progressBar.setVisibility(View.VISIBLE);
        hidebutton();
    }

    private void hidebutton()
    {
        if(bAdd.getVisibility() == View.VISIBLE)
            bAdd.hide();
    }

    private void showbutton()
    {
        if((bAdd.getVisibility() == View.GONE) || ( bAdd.getVisibility() == View.INVISIBLE))
            bAdd.show();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class Connect extends AsyncTask<Void, Void, String>
    {
        protected String doInBackground(Void... args) {
            try
            {
                //String arr = "";
                int i = 0;
                for(Server srv : Globals.sList)
                {
                    GoldSrcServer server = new GoldSrcServer(srv.getIP(), srv.getPort());

                    //server = new Server(eip.getText().toString(), Integer.parseInt(eport.getText().toString()), epass.getText().toString());

                    try
                    {
                        HashMap<String, Object> hashMap = server.getServerInfo();
                        //arr = hashMap.toString();
                        for (Map.Entry<String, Object> info : hashMap.entrySet())
                        {
                            String key = info.getKey();
                            String value = info.getValue().toString();
                            if (key.equals("serverName"))
                            {
                                srv.setHostname(value);
                            } else if (key.equals("mapName"))
                            {
                                srv.setMapname(value);//mapname = info.getValue().toString();
                            } else if (key.equals("maxPlayers"))
                            {
                                srv.setMaxplayers(value);//maxplayers = info.getValue().toString();
                            } else if (key.equals("numberOfPlayers"))
                            {
                                srv.setPlayers(value);//players = info.getValue().toString();
                            } else if(key.equals("gameDir"))
                            {
                                if(value.equals("cstrike"))
                                {
                                    srv.setImg(IMGEnum.CS.index());
                                }
                                else
                                {
                                    srv.setImg(IMGEnum.HL.index());
                                }
                            }
                        }
                        srv.setOnline(true);
                    }
                    catch (TimeoutException e)
                    {
                        srv.setOnline(false);
                    }
                    Globals.sList.set(i, srv);
                    i++;
                }
                //return Integer.toString(i);

                //Files.write(arr, new File(Environment.getExternalStorageDirectory() + "/hlrcon/test.txt"), Charset.defaultCharset());
                //return arr;
            }
            catch(SteamCondenserException e)
            {
                return e.toString();
            }
            return null;
        }

        protected void onPostExecute(String result)
        {
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
            isRefreshing = false;
            showbutton();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sl_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.refresh:
            {
                //RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate360);
                //item.startAnimation(rotateAnimation);
                refresh();
            }

            default:
                break;

        }
        return true;
    }

}

