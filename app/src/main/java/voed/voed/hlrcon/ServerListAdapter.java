package voed.voed.hlrcon;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import voed.voed.hlrcon.steamcondenser.exceptions.SteamCondenserException;
import voed.voed.hlrcon.steamcondenser.steam.servers.GoldSrcServer;

import java.text.Collator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class ServerListAdapter extends ArrayAdapter<String> {
    private List<Server> data;
    private Context context;
    public Activity act;
    public static Server server;
    private View view;

    public ServerListAdapter(Context context, List<Server> data, Activity act) {
        super(context, R.layout.serverlist_listview);
        this.data = data;
        this.context = context;
        this.act = act;
    }

    @Override
    public int getCount() {
        // возвращаем количество элементов списка
        return data.size();
    }

    @Override
    public String getItem(int position) {
        // получение одного элемента по индексу
        return data.get(position).getHostIP();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // заполнение элементов списка
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // задаем вид элемента списка, который мы создали высше
        view = inflater.inflate(R.layout.serverlist_listview, parent, false);

        // проставляем данные для элементов
        TextView hostname = (TextView) view.findViewById(R.id.hostname);
        TextView hostip = (TextView) view.findViewById(R.id.hostip);
        TextView hostinfo = (TextView) view.findViewById(R.id.hostinfo);

        ImageView gameicon = (ImageView) view.findViewById(R.id.gameicon);

        hostname.setTypeface(ServerList.Roboto);
        hostip.setTypeface(ServerList.Roboto);
        hostinfo.setTypeface(ServerList.Roboto);
        //ImageView thumbImage = (ImageView)view.findViewById(R.id.imageView);
        final ImageButton imb = (ImageButton) view.findViewById(R.id.imgbMenu);
        // получаем элемент со списка
        imb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new MaterialDialog.Builder(act)
                        .title(data.get(position).getHostName())
                        .items(R.array.serverpopup)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0: {
                                        onconsole(view, position);
                                        break;
                                    }
                                    case 1: {
                                        onplayers(view, position);
                                        break;
                                    }
                                    case 2: {
                                        onmaps(position);
                                        break;
                                    }
                                    case 3: {
                                        onsettings(view, position);
                                        break;
                                    }
                                    case 4: {
                                        onremove(position);
                                        break;
                                    }
                                }
                            }
                        })
                        .show();
            }


        });
        // устанавливаем значения компонентам одного эелемента списка
        server = data.get(position);
        hostname.setText(server.getHostName());
        hostip.setText(server.getHostIP());
        if (server.getOnline()) {
            hostinfo.setText(server.getPlayers() + "/" + server.getMaxPlayers() + " на " + server.getMapname());
        } else {
            hostinfo.setText("оффлайн");
        }

        gameicon.setImageDrawable(ServerList.images.getDrawable(server.getImg()));

        return view;
    }

    public void onconsole(View view, int position) {

        Server srv = data.get(position);

        if(!srv.getOnline()) {
            new MaterialDialog.Builder(act).title(srv.getHostName()).content("Сервер выключен или временно недоступен").positiveText("OK").show();
            return;
        }

        Intent i = new Intent(view.getContext(), Console.class);
        i.putExtra("server", srv);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        view.getContext().startActivity(i);
    }

    public void onplayers(View view, int position) {
        Server srv = data.get(position);

        if(!srv.getOnline()) {
            new MaterialDialog.Builder(act).title(srv.getHostName()).content("Сервер выключен или временно недоступен").positiveText("OK").show();
            return;
        }

        if(srv.getPlayers().equals("0")) {
            new MaterialDialog.Builder(act).title(srv.getHostName()).content("Сервер пуст").positiveText("OK").show();
            return;
        }
        /*Intent i = new Intent(view.getContext(), PlayersList.class);
        i.putExtra("server", srv);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        view.getContext().startActivity(i);*/
        Intent i = new Intent(view.getContext(), PlayersActivity.class);
        i.putExtra("server", srv);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        view.getContext().startActivity(i);
    }

    public void onmaps(int position)
    {
        Server srv = data.get(position);

        if(!srv.getOnline()) {
            new MaterialDialog.Builder(act).title(srv.getHostName()).content("Сервер выключен или временно недоступен").positiveText("OK").show();
            return;
        }
        new Command().execute("maps *", "getlist", Integer.toString(position));
    }

    public void onsettings(View view, int position)
    {

    }

    public void onremove(int position)
    {
        data.remove(position);
        notifyDataSetChanged();
        Globals.writeXml();
    }
    public void showmaplist(String maps, final String serverpos)
    {
        maps = maps.replace(".bsp", ""); // removing .bsp endings
        List<String> listmaps = new LinkedList<>(Arrays.asList(maps.split("\\r?\\n")));
        listmaps.remove(0);// removing "-----------" shit
        Set<String> setmaps = new HashSet<>(listmaps); // splitting string to set by newline char for duplicate removing
        listmaps.clear();
        listmaps.addAll(setmaps); // converting set to list
        java.util.Collections.sort(listmaps, Collator.getInstance()); // sorting list alphabetical
        CharSequence[] maps_parsed = listmaps.toArray(new CharSequence[listmaps.size()]); // converting to array
        new MaterialDialog.Builder(act)
                .title("Выберите карту")
                .items(maps_parsed)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        new Command().execute(String.format("changelevel %s", text.toString()), "changemap" + text, serverpos);
                        //new MaterialDialog.Builder(act).title("HLRcon").content(text.toString()).positiveText("OK").show();
                    }
                })
                .show();
    }
    public class Command extends AsyncTask<String, Void, String[] > {
        protected String[] doInBackground(String... args) {
            String [] result = new String[]{args[1], "", args[2], ""};
            //result[0] = ;
            try {
                Server srvfromstring = data.get(Integer.parseInt(args[2]));
                GoldSrcServer srv = new GoldSrcServer(srvfromstring.getIP(), srvfromstring.getPort());
                String cmd = args[0];

                try {

                    srv.rconAuth(srvfromstring.getPass());
                    //return srv.rconExec(cmd);
                    result[1] = srv.rconExec(cmd);
                } catch (TimeoutException e) {
                    result[1] = e.toString();
                    result[3] = "error";
                }
            } catch (SteamCondenserException e) {
                result[1] = e.toString();
                result[3] = "error";
            }
            return result;
        }

        protected void onPostExecute(String[] result) {

            if(result[0].contains("changemap"))
            {

                //new MaterialDialog.Builder(act).title("HLRcon").content("Карта изменена на " + result[0].replace("changemap", "")).positiveText("OK").show();
                Snackbar.make(view, "Карта меняется на " + result[0].replace("changemap", "")+"...", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if(result[3].equals("error"))
            {
                new MaterialDialog.Builder(act).title("Error").content(result[1]).positiveText("OK").show();
            }
            else if(result[0].equals("getlist")) {
                showmaplist(result[1], result[2]);
            }

        }
    }
}

/**View popupView = infltr.inflate(R.layout.popupmenu, null);
 final PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
 popupWindow.setFocusable(true);
 popupWindow.setOutsideTouchable(true);
 popupWindow.setBackgroundDrawable(new BitmapDrawable());

 TextView tvconsole = (TextView) popupView.findViewById(R.id.menuitem_console);
 tvconsole.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
//Console.server = data.get(pos);
popupWindow.dismiss();
Intent i = new Intent(view.getContext(), Console.class);
i.putExtra("server", server);
i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
view.getContext().startActivity(i);
}
});

 TextView tvplayers = (TextView)popupView.findViewById(R.id.menuitem_players);
 tvplayers.setOnClickListener(new View.OnClickListener()
 {
 @Override
 public void onClick(View v)
 {
 popupWindow.dismiss();
 Intent i = new Intent(view.getContext(), PlayersList.class);
 i.putExtra("server", server);
 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 view.getContext().startActivity(i);
 }
 });

 TextView tvremove = (TextView) popupView.findViewById(R.id.menuitem_delete);
 tvremove.setOnClickListener(new View.OnClickListener()
 {
 @Override
 public void onClick(View v)
 {
 popupWindow.dismiss();
 data.remove(pos);
 notifyDataSetChanged();
 Globals.writeXml();
 }
 });

 TextView tvmaps = (TextView) popupView.findViewById(R.id.menuitem_maps);
 tvmaps.setOnClickListener(new View.OnClickListener()
 {
 @Override
 public void onClick(View v)
 {
 popupWindow.dismiss();

 new Command().execute("maps *", "getlist", Integer.toString(position));

 }
 });

 int[] coords = {0, 0};
 view.getLocationOnScreen(coords);

 if ((p.getHeight() - coords[1]) < (56 * 4))
 {
 popupWindow.showAsDropDown(v, 56 * 4, 10);
 }
 //popupWindow.setAnimationStyle(R.style.PopupAnimation);
 popupWindow.showAsDropDown(v);**/
