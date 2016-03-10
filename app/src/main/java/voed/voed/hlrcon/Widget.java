package voed.voed.hlrcon;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;

import voed.voed.hlrcon.steamcondenser.exceptions.SteamCondenserException;
import voed.voed.hlrcon.steamcondenser.steam.servers.GoldSrcServer;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import voed.voed.hlrcon.enums.IMGEnum;

public class Widget extends AppWidgetProvider
{

    public static String ACTION_WIDGET_RECEIVER = "ActionReceiverWidget";
    private static String serverlist = "serverlist.json";
    private static File folder = new File(Environment.getExternalStorageDirectory() + "/hlrcon/");
    private static File file = new File(Environment.getExternalStorageDirectory() + "/hlrcon/" + serverlist);
    private static Server w_server;

    private Context context;
    private AppWidgetManager appWidgetManager;
    private int[] appWidgetIds;
    private RemoteViews remoteViews;
    private Connect connect;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.appWidgetIds = appWidgetIds;

        Intent active = new Intent(context, Widget.class);
        active.setAction(ACTION_WIDGET_RECEIVER);
        remoteViews.setOnClickPendingIntent(R.id.w_refresh, getPendingSelfIntent(context, ACTION_WIDGET_RECEIVER));
        //пишем в лог имя данного метода для отслеживания
        Log.d("kkk", "onUpdate");

        //Создаем новый RemoteViews
        //Создаем строку с текущей датой и временем
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        //Date date = new Date();
        //String now = dateFormat.format(date);
        //обновляем данные в TextView виджета
        //remoteViews.setTextViewText(R.id.textView2, now);
        connect = new Connect();
        connect.execute(server());


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        if (ACTION_WIDGET_RECEIVER.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), Widget.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
                onUpdate(context, appWidgetManager, appWidgetIds);
            }

    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public static Server server()
    {
        try
        {
            String ls;
            if(!file.exists())
            {
                return null;
            }
            else
            {
                ls = Files.readFirstLine(file, Charset.defaultCharset());
            }

            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonArray jArray = parser.parse(ls).getAsJsonArray();
            for(JsonElement obj : jArray )
            {
                Log.d("hlrcon_widget", obj.toString());
                return gson.fromJson(obj, Server.class);
            }
        }
        catch (IOException e)
        {
            return null;
        }
        return null;
    }

    public class Connect extends AsyncTask<Server, Void, Server>
    {

        protected Server doInBackground(Server... srvs) {
            Server srv = srvs[0];
            if(srv==null)
            {
                Log.d("hlrcon_widget", "no servers");
                return null;
            }
            try
            {
                GoldSrcServer server = new GoldSrcServer(srv.getIP(), srv.getPort());


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
                }

                catch (TimeoutException e)
                {
                    srv.setOnline(false);
                }
            }
            catch(SteamCondenserException e)
            {
                srv.setHostname("Error");
            }
            return srv;
        }

        protected void onPostExecute(Server result)
        {
            if(result == null)
            {
                Log.d("hlrcon_widget", "no results");
                return;
            }

            //if(!bAdd.isShow())
                //bAdd.show();

            Log.d("hlrcon_widget", result.getMapname());

            //AppWidgetManager wm = AppWidgetManager.getInstance(context);
//get widget ids for widgets created
            //ComponentName widget = new ComponentName(context, Widget.class);
            //int[] widgetIds = wm.getAppWidgetIds(widget);
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setTextViewText(R.id.hostname, result.getHostName());
            remoteViews.setTextViewText(R.id.hostip, result.getHostIP());

            if(result.getOnline())
                remoteViews.setTextViewText(R.id.hostinfo, result.getPlayers() + "/" + result.getMaxPlayers() + " на " + result.getMapname());
            else
                remoteViews.setTextViewText(R.id.hostinfo, "оффлайн");

            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        }

    }
}
