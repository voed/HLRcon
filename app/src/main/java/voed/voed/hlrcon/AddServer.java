package voed.voed.hlrcon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rengwuxian.materialedittext.MaterialEditText;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddServer extends AppCompatActivity
{
    public static Pattern p = Pattern.compile("^(?=\\d+\\.\\d+\\.\\d+\\.\\d+$)(?:(?:25[0-9]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]|[0-9])\\.?){4}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.addtoolbar);
        //toolbar.setLogo(R.drawable.hlrcon);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*final RelativeLayout focuslayout = (RelativeLayout) findViewById(R.id.RequestFocusLayout);
        focuslayout.requestFocus();
        focuslayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focuslayout.getWindowToken(), 0);
            }
        });*/

        FloatingActionButton btn = (FloatingActionButton)findViewById(R.id.addserver_done);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
               bClick();
            }
        });
    }


    public void bClick()
    {
        MaterialEditText eip = (MaterialEditText)this.findViewById(R.id.addserver_textip);
        String ip = eip.getText().toString();
        if(!StringUtils.isNotBlank(ip))
        {
            eip.setError("Введите IP");
            return;
        }

        Matcher m = p.matcher(ip);
        if(!m.matches())
        {
            eip.setError("Неправильный формат IP");
            return;
        }

        MaterialEditText eport = (MaterialEditText)this.findViewById(R.id.addserver_textport);
        int port = Integer.parseInt(eport.getText().toString());
        if(port < 1)
        {
            eip.setError("Введите порт");
            return;
        }

        MaterialEditText epass = (MaterialEditText)this.findViewById(R.id.addserver_textpass);

        Server srv = new Server(ip, port,epass.getText().toString(), " ");
        Globals.sList.add(srv);
        Globals.writeXml();
        finish();
        /*Intent myIntent = new Intent(AddServer.this, ServerList.class);
        startActivity(myIntent);*/
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

