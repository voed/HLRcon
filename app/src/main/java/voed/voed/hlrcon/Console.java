package voed.voed.hlrcon;
//import com.gc.materialdesign.views.ProgressBarIndeterminate;
import voed.voed.hlrcon.steamcondenser.exceptions.*;
import voed.voed.hlrcon.steamcondenser.steam.servers.GoldSrcServer;
import voed.voed.hlrcon.steamcondenser.steam.sockets.GoldSrcSocket;
import com.rengwuxian.materialedittext.MaterialEditText;


import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;


public class Console extends AppCompatActivity
{

    public static MaterialEditText ecmd;
    public static EditText econsole;
    private static Server server;
    public Connect connect;
    private AsyncTask<Void, Void, Void> async;
    //public ProgressBarIndeterminate console_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        //StrictMode.setThreadPolicy(policy);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.consoletoolbar);
        //toolbar.setLogo(R.drawable.hlrcon);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //console_progress = (ProgressBarIndeterminate)findViewById(R.id.console_progress);

        connect = new Connect();

        server = (Server) getIntent().getSerializableExtra("server");
        ecmd = (MaterialEditText)this.findViewById(R.id.editText5);
        econsole = (EditText) this.findViewById(R.id.editText4);
        //ecmd.requestFocus();
        ImageButton btn2 = (ImageButton) findViewById(R.id.button_send);
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), server.getIP() + server.getPort(), Toast.LENGTH_LONG).show();
                if(StringUtils.isNotBlank(ecmd.getText().toString()))
                {
                    sendCommand();
                }
            }
        });

    }

    public void sendCommand()
    {

        if(connect.getStatus() == AsyncTask.Status.RUNNING)
            return;
        //Toast.makeText(getApplicationContext(), server.getIP() + ":" + server.getPort() + " | " + server.getPass(), Toast.LENGTH_SHORT).show();
        connect = new Connect();
        connect.execute(ecmd.getText().toString());
        //console_progress.setVisibility(View.VISIBLE);
    }

    public void start()
    {

    }

    /*public void getlogs()
    {
        async = new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                byte[] lMsg = new byte[4096];
                DatagramPacket dp = new DatagramPacket(lMsg, lMsg.length);
                DatagramSocket ds = null;

                try
                {
                    ds = new DatagramSocket(0);
                    Log.d("CONLOGS", "Port " + ds.getLocalPort());
                    GoldSrcServer srv = new GoldSrcServer(server.getIP(), server.getPort());
                    srv.rconAuth(server.getPass());
                    srv.rconExec("logaddress_add 192.168.1.37 " + Integer.toString(ds.getLocalPort()));

                    while(true)
                    {
                        ds.receive(dp);
                        Log.d("CONLOGS", new String(lMsg, 0, dp.getLength()));
                    }
                }
                catch (Exception e)
                {
                    Log.d("CONLOGS", e.toString());
                }
                finally
                {
                    if (ds != null)
                    {
                        ds.close();
                    }
                }

                return null;
            }
        };
        async.execute();

    }*/

    public class Connect extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... args) {
            String output;
            try
            {
                GoldSrcServer srv = new GoldSrcServer(server.getIP(), server.getPort());
                srv.rconAuth(server.getPass());
                output = srv.rconExec(args[0]);
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
            econsole.append(">" + ecmd.getText().toString() + "\n");
            econsole.append(result + "\n");
            ecmd.setText("");

            //console_progress.setVisibility(View.GONE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_console, menu);
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
