package voed.voed.hlrcon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import voed.voed.hlrcon.steamcondenser.exceptions.SteamCondenserException;
import voed.voed.hlrcon.steamcondenser.steam.SteamPlayer;
import voed.voed.hlrcon.steamcondenser.steam.servers.GoldSrcServer;


import java.util.Formatter;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class PlayersListAdapter extends ArrayAdapter<Integer>
{
    private List<SteamPlayer> data;
    private Context context;
    public Command command;
    public View view;
    public Activity act;
    public Resources res;
    public String pack;
    public int banpos;

    public PlayersListAdapter(Context context, List<SteamPlayer> data, Activity act) {
        super(context, R.layout.serverlist_listview);
        this.data = data;
        this.context = context;
        this.act = act;
        res = context.getResources();
        pack = context.getPackageName();
    }

    @Override
    public int getCount() {
        // возвращаем количество элементов списка
        return data.size();
    }

    @Override
    public Integer getItem(int position) {
        // получение одного элемента по индексу
        return data.get(position).getRealId();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // задаем вид элемента списка, который мы создали высше
        view = inflater.inflate(R.layout.players_listview, parent, false);

        TextView playername = (TextView)view.findViewById(R.id.player_name);
        TextView playerscore = (TextView)view.findViewById(R.id.player_score);
        TextView playertime = (TextView)view.findViewById(R.id.player_time);
        TextView playerindex = (TextView)view.findViewById(R.id.player_index);

        playername.setTypeface(ServerList.Roboto);
        playerscore.setTypeface(ServerList.Roboto);
        playertime.setTypeface(ServerList.Roboto);
        playerindex.setTypeface(ServerList.Roboto);

        final SteamPlayer player = data.get(position);


        playername.setText(player.getName());
        playerindex.setText("#"+Integer.toString(player.getRealId()));
        playerscore.setText(getstr("player_ingame_time") + ": " + Integer.toString(player.getScore()));
        playertime.setText(R.string.player_ingame_time + ": " + parsetime(player.getConnectTime()));

        ImageButton imb = (ImageButton)view.findViewById(R.id.player_menu);

        final LayoutInflater infltr = inflater;
        final ViewGroup p = parent;

        imb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                View popupView = infltr.inflate(R.layout.pl_popupmenu, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());

                TextView tvkick = (TextView)popupView.findViewById(R.id.plmenuitem_kick);
                tvkick.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        popupWindow.dismiss();
                        kick(player.getRealId(), player.getName());
                    }
                });

                TextView tvban = (TextView) popupView.findViewById(R.id.plmenuitem_ban);
                tvban.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        popupWindow.dismiss();
                        ban(player.getRealId(), player.getName());
                    }
                });

                TextView tvname = (TextView) popupView.findViewById(R.id.plmenuitem_name);
                tvname.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        popupWindow.dismiss();
                        changeName(player.getRealId(), player.getName());
                    }
                });
                TextView tvextra = (TextView) popupView.findViewById(R.id.plmenuitem_extra);
                tvextra.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        popupWindow.dismiss();
                        StringBuilder sb = new StringBuilder();
                        sb.append(getstr("player_id") + " :" + player.getRealId()).append("\n");
                        sb.append(getstr("player_score") + " :" + player.getScore()).append("\n");
                        sb.append(getstr("player_ingame_time") + " :" + parsetime(player.getConnectTime())).append("\n");
                        sb.append(getstr("player_ip") + " :" + player.getIpAddress()+ ":" + player.getClientPort()).append("\n");
                        sb.append(getstr("player_steamid") + " :" + player.getSteamId()).append("\n");
                        sb.append(getstr("player_ping") + " :" + player.getPing()).append("\n");


                        //String info = String.format("%d \n %s", player.getRealId(), player.getName());
                        new MaterialDialog.Builder(act).title(player.getName()).content(sb.toString()).positiveText("OK").show();
                    }
                });
                int[] coords = {0, 0};
                view.getLocationOnScreen(coords);

                if ((p.getHeight() - coords[1]) < (56 * 3))
                {
                    popupWindow.showAsDropDown(v, 56 * 3, 10);
                }
                //popupWindow.setAnimationStyle(R.style.PopupAnimation);
                popupWindow.showAsDropDown(v);//(v, 100, -5);
            }



        });
        return view;
    }

    public String getstr(String resourcename)
    {
        int resId = res.getIdentifier(resourcename, "string", pack);
        return context.getString(resId);
    }
    public String parsetime(float time)
    {
        String output;
        if(time < 60)
        {
            output = String.format("%.0f с", time);
        }
        else if(time < 3600)
        {
            output = String.format("%.0f м", time/60);
        }
        else
        {
            output = String.format("%.0f ч %.0f м", time/3600, time/60);
        }
        return output;
    }

    public void kick(final int userid, String name)
    {
        new MaterialDialog.Builder(act)
                .title("Кикнуть игрока " + name)
                .content("")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .positiveText("OK")
                .input("Введите причину кика", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        new Command().execute("amx_kick #" + Integer.toString(userid) + " " + input);
                    }

                }).show();
    }

    public void ban(final int userid, String name)
    {

        /*View view;
        LayoutInflater inflater = (LayoutInflater)   getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_banmenu, null);

        Spinner spinner = (Spinner) view.findViewById(R.id.bm_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.banmenuspinner, R.layout.layout_banmenu);

        adapter.setDropDownViewResource(R.layout.layout_banmenu);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                banpos = position;
                Snackbar.make(view, "asd" + position, Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        MaterialDialog dialog = new MaterialDialog.Builder(act)
                .title("Забанить игрока " + name)
                .customView(R.layout.layout_banmenu, true)
                .positiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        //Spinner spinner = (Spinner) dialog.getCustomView().findViewById(R.id.bm_spinner);
                        new MaterialDialog.Builder(act).title("HLRcon").content(banpos).positiveText("OK").show();

                        //new Command().execute("amx_ban #" + Integer.toString(userid) + " 5 Banned by HLRcon");
                    }
                })
                .show();
        //TODO: create ban menu*/
        //new Command().execute("amx_ban #" + Integer.toString(userid) + " 5 Banned by HLRcon");
    }

    public void changeName(final int userid, String name)
    {
        new MaterialDialog.Builder(act)
                .title("Сменить ник игроку " + name)
                .content("")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .positiveText("OK")
                .input("Введите ник", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        new Command().execute("amx_nick #" + Integer.toString(userid) + " " + input);
                    }

                }).show();
        //TODO:enter name
        //new Command().execute("amx_nick #" + Integer.toString(userid) + " HLRcon");
    }

    public class Command extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... args) {
            try
            {
                Server srv = PlayersList.server;
                GoldSrcServer server = new GoldSrcServer(srv.getIP(), srv.getPort());
                String cmd = args[0];
                try
                {

                    server.rconAuth(srv.getPass());
                    return server.rconExec(cmd);
                }

                catch (TimeoutException e)
                    {
                        return e.toString();
                    }
            }
            catch(SteamCondenserException e)
            {
                return e.toString();
            }
        }

        protected void onPostExecute(String result)
        {

            new MaterialDialog.Builder(act).title("HLRcon").content(result).positiveText("OK").show();
           // adapter.notifyDataSetChanged();
            //progressBar.setVisibility(View.GONE);

        }

    }
}
