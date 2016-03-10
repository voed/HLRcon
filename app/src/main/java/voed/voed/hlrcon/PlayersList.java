package voed.voed.hlrcon;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import voed.voed.hlrcon.steamcondenser.exceptions.RCONNoAuthException;
import voed.voed.hlrcon.steamcondenser.exceptions.SteamCondenserException;
import voed.voed.hlrcon.steamcondenser.steam.SteamPlayer;
import voed.voed.hlrcon.steamcondenser.steam.servers.GoldSrcServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class PlayersList extends AppCompatActivity
{
    public static Server server;
    private PlayerTask playerTask;
    public List<SteamPlayer> steamPlayerList;
    private ListView playerslistview;
    private PlayersListAdapter playersadapter;
    static PlayersList pl;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        pl = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.players_list);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.pltoolbar);
        //toolbar.setLogo(R.drawable.hlrcon);
        TextView title = (TextView)findViewById(R.id.pl_toolbar_title);

        title.setTypeface(Globals.Roboto);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        steamPlayerList = new ArrayList<>();
        server = (Server) getIntent().getSerializableExtra("server");
        playerslistview = (ListView)findViewById(R.id.playetslist_listview);



        playerTask = new PlayerTask();
        playerTask.execute();
    }



    public class PlayerTask extends AsyncTask<Void, Void, String>
    {
        protected String doInBackground(Void... arg)
        {
            String output;
            try
            {
                GoldSrcServer srv = new GoldSrcServer(server.getIP(), server.getPort());
                HashMap<String, SteamPlayer> hashMap = srv.getPlayers(server.getPass());
                steamPlayerList = new ArrayList<>(hashMap.values());
                output = steamPlayerList.toString();
            }
            catch(RCONNoAuthException e)
            {
                output = "Неверный RCON пароль.";
            }
            catch(TimeoutException e)
            {
                output = "Сервер не отвечает.";
            }

            catch(SteamCondenserException e)
            {
                output = e.toString();
            }
            return output;
        }

        protected void onPostExecute(String result)
        {
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            //Log.d("Players", result);
            playersadapter = new PlayersListAdapter(getApplicationContext(), steamPlayerList, PlayersList.this);
            playerslistview.setAdapter(playersadapter);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    //getMenuInflater().inflate(R.menu.menu_players, menu);
    return true;
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id)
        {
            case android.R.id.home:
                finish();
                break;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

