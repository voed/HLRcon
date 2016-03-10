package voed.voed.hlrcon;


import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Environment;

import com.google.gson.*;
import com.google.common.io.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class Globals
{
    public static String tagPlayersList = "PLAYERS_LIST";
    public static String test = "[{\"IP\":\"192.168.1.10\",\"hostname\":\"VOED I CO\",\"players\":\"1\",\"pass\":\"322\",\"mapname\":\"de_cbble\",\"maxplayers\":\"30\",\"isOnline\":true,\"img\":0,\"port\":27016},{\"IP\":\"192.168.1.10\",\"hostname\":\"VOED I CO\",\"players\":\"1\",\"pass\":\"322\",\"mapname\":\"de_cbble\",\"maxplayers\":\"30\",\"isOnline\":true,\"img\":0,\"port\":27016},{\"IP\":\"192.168.1.10\",\"hostname\":\"VOED I CO\",\"players\":\"1\",\"pass\":\"322\",\"mapname\":\"de_cbble\",\"maxplayers\":\"30\",\"isOnline\":true,\"img\":0,\"port\":27016},{\"IP\":\"192.168.1.10\",\"hostname\":\"VOED I CO\",\"players\":\"1\",\"pass\":\"322\",\"mapname\":\"de_cbble\",\"maxplayers\":\"30\",\"isOnline\":true,\"img\":0,\"port\":27016},{\"IP\":\"192.168.1.10\",\"hostname\":\"VOED I CO\",\"players\":\"1\",\"pass\":\"322\",\"mapname\":\"de_cbble\",\"maxplayers\":\"30\",\"isOnline\":true,\"img\":0,\"port\":27016},{\"IP\":\"192.168.1.10\",\"hostname\":\"VOED I CO\",\"players\":\"1\",\"pass\":\"322\",\"mapname\":\"de_cbble\",\"maxplayers\":\"30\",\"isOnline\":true,\"img\":0,\"port\":27016},{\"IP\":\"192.168.1.10\",\"hostname\":\"VOED I CO\",\"players\":\"1\",\"pass\":\"322\",\"mapname\":\"de_cbble\",\"maxplayers\":\"30\",\"isOnline\":true,\"img\":0,\"port\":27016},{\"IP\":\"192.168.1.10\",\"hostname\":\"VOED I CO\",\"players\":\"1\",\"pass\":\"322\",\"mapname\":\"de_cbble\",\"maxplayers\":\"30\",\"isOnline\":true,\"img\":0,\"port\":27016},{\"IP\":\"192.168.1.10\",\"hostname\":\" \",\"pass\":\"322\",\"isOnline\":false,\"img\":0,\"port\":27016}]";
    public static ArrayList<Server> sList = new ArrayList<Server>();
    private static String serverlist = "serverlist.json";
    private static File folder = new File(Environment.getExternalStorageDirectory() + "/hlrcon/");
    private static File file = new File(Environment.getExternalStorageDirectory() + "/hlrcon/" + serverlist);
    public static Typeface Roboto = Typeface.createFromAsset(ApplicationContextProvider.getContext().getAssets(), "fonts/Roboto-Regular.ttf");
    public static void writeXml()
    {
        try
        {
            boolean state = false;
            if(!folder.exists())
            {
                state = folder.mkdirs();
            }
            if(file.exists())
            {
                state = file.delete();
            }
            if(!state)
            {
                //Toast.makeText(ApplicationContextProvider.getContext(), "cant create file " + Environment.getExternalStorageDirectory() + "/hlrcon/" + serverlist, Toast.LENGTH_LONG).show();
            }
            String json = new Gson().toJson(sList);
            Files.write(json, file, Charset.defaultCharset());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void readXml()
    {
        try
        {
            String ls;
            if(!file.exists())
            {
                ls = test;
            }
            else
            {
                ls = Files.readFirstLine(file, Charset.defaultCharset());
            }
            Globals.sList.clear();


            Gson gson = new Gson();
            JsonParser parser = new JsonParser();
            JsonArray jArray = parser.parse(ls).getAsJsonArray();

            for(JsonElement obj : jArray )
            {
                Server server = gson.fromJson( obj , Server.class);
                sList.add(server);
                //Toast.makeText(ApplicationContextProvider.getContext(), server.getHostName(), Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(ApplicationContextProvider.getContext(), ls, Toast.LENGTH_LONG).show();
        }
        catch (IOException e)
        {
            new AlertDialog.Builder(ApplicationContextProvider.getContext()).setMessage(e.toString()).show();
        }
    }

}
