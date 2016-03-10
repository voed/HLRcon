package voed.voed.hlrcon;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.afollestad.materialdialogs.MaterialDialog;
import voed.voed.hlrcon.steamcondenser.exceptions.RCONNoAuthException;
import voed.voed.hlrcon.steamcondenser.exceptions.SteamCondenserException;
import voed.voed.hlrcon.steamcondenser.steam.SteamPlayer;
import voed.voed.hlrcon.steamcondenser.steam.servers.GoldSrcServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class PLRFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    protected RecyclerView plRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Server server;
    private PlayerTask playerTask = new PlayerTask();
    public List<SteamPlayer> steamPlayerList = new ArrayList<>();
    private PLRAdapter mAdapter;

    @Override
    public void onRefresh() {

        refresh();
        
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.players_fragment, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.plswiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        plRecyclerView = (RecyclerView) rootView.findViewById(R.id.plrecycler);
        LinearLayoutManager plLayoutManager = new LinearLayoutManager(getActivity());
        plRecyclerView.setLayoutManager(plLayoutManager);

        server = (Server) getActivity().getIntent().getSerializableExtra("server");

        mAdapter = new PLRAdapter(steamPlayerList, getContext(), server);
        plRecyclerView.setAdapter(mAdapter);



        mSwipeRefreshLayout.setColorSchemeResources(R.color.Accent);
        //mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.Primary);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        onRefresh();

        return rootView;
    }


    public void refresh()
    {
        if(playerTask.getStatus() == AsyncTask.Status.RUNNING)
        {
            return;
        }
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
                
                output = "ok";
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
            mSwipeRefreshLayout.setRefreshing(false);
            if(!result.equals("ok"))
            {
                new MaterialDialog.Builder(getActivity()).title("HLRcon").content(result).positiveText("OK").show();
                return;
            }
            mAdapter.update(steamPlayerList);
        }

    }


}
