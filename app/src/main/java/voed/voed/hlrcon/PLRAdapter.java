package voed.voed.hlrcon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import voed.voed.hlrcon.steamcondenser.exceptions.SteamCondenserException;
import voed.voed.hlrcon.steamcondenser.steam.SteamPlayer;
import voed.voed.hlrcon.steamcondenser.steam.servers.GoldSrcServer;
import com.pkmmte.view.CircularImageView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class PLRAdapter extends RecyclerView.Adapter<PLRAdapter.PlayerViewHolder>{

    private Resources res;
    private List<SteamPlayer> players;
    private Context context;
    private String pack;
    private Server server;
    private Activity activity;

    public class PlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        View contentView;
        TextView playerIndex;
        TextView playerName;
        TextView playerTime;
        TextView playerScore;
        TextView playerPing;
        CircularImageView avatarImg;

        PlayerViewHolder(View itemView)
        {
            super(itemView);

            contentView = itemView.findViewById(R.id.plcontentview);
            playerIndex = (TextView)itemView.findViewById(R.id.player_index);
            playerName  = (TextView)itemView.findViewById(R.id.player_name);
            playerTime  = (TextView)itemView.findViewById(R.id.player_time);
            playerScore = (TextView)itemView.findViewById(R.id.player_score);
            playerPing  = (TextView)itemView.findViewById(R.id.player_ping);
            avatarImg   = (CircularImageView)itemView.findViewById(R.id.avatarImageView);
            avatarImg.setClickable(true);
            avatarImg.setOnClickListener(this);
            contentView.setClickable(true);
            contentView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            final SteamPlayer player = players.get(position);
            if (v.getId() == avatarImg.getId()){
                if(player.isRealSteam()) {
                    Uri uri = Uri.parse(player.getProfileLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
                else{
                    Snackbar.make(v, "Это обычный пират", Snackbar.LENGTH_SHORT).show();
                }

            } else {
                new MaterialDialog.Builder(context)
                        .title(player.getName())
                        .items(context.getResources().getStringArray(R.array.playerpopup))
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0: {
                                        kick(player);
                                        break;
                                    }
                                    case 1: {
                                        ban(player);
                                        break;
                                    }
                                    case 2: {
                                        changeName(player);
                                        break;
                                    }
                                }
                            }
                        })
                        .show();
            }
        }

        public void kick(final SteamPlayer player)
        {
            new MaterialDialog.Builder(context)
                    .title("Кикнуть игрока " + player.getName())
                    .content("")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .positiveText("OK")
                    .input("Введите причину кика", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            String cmd = String.format("amx_kick #%d %s", player.getRealId(), input);
                            new Command().execute(cmd);
                        }

                    }).show();
        }

        public void ban(SteamPlayer player)
        {

        }

        public void changeName(final SteamPlayer player)
        {
            new MaterialDialog.Builder(context)
                    .title("Сменить ник игроку " + player.getName())
                    .content("")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .positiveText("OK")
                    .input("Введите ник", "", new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            String cmd = String.format("amx_nick #%d %s", player.getRealId(), input);
                            new Command().execute(cmd);
                        }

                    }).show();
        }

    }

    public void update(List<SteamPlayer> data)
    {
        players = data;
        notifyDataSetChanged();
    }

    PLRAdapter(List<SteamPlayer> players, Context context, Server server){
        this.players = players;
        this.context = context;
        this.res = context.getResources();
        pack = context.getPackageName();
        this.server = server;
        this.activity = (Activity) context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.players_contentview, viewGroup, false);
        //PlayerViewHolder pvh = new PlayerViewHolder(v);
        return new PlayerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder playerViewHolder, int i) {
        final SteamPlayer pl = players.get(i);
        String ping;

        playerViewHolder.avatarImg.setImageBitmap(formatAvatar(pl.getAvatar()));
        playerViewHolder.playerIndex.setText(formatId(pl.getRealId()));
        playerViewHolder.playerName.setText(formatName(pl.getName()));
        playerViewHolder.playerTime.setText(formatTime(pl.getConnectTime()));
        playerViewHolder.playerScore.setText(formatScore(pl.getScore()));

        if(pl.isBot())
        {
            playerViewHolder.avatarImg.setBorderWidth(4);
            playerViewHolder.avatarImg.setBorderColor(res.getColor(R.color.cheater));
            //playerViewHolder.contentView.setBackgroundColor(context.getResources().getColor(R.color.md_red_100));
        }

        playerViewHolder.playerPing.setText(formatPing(pl));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public Bitmap formatAvatar(Bitmap avatar)
    {
        if(avatar == null)
        {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.crackedsteam);
        }
        return avatar;
    }
    public String formatId(int id)
    {
        return "#" + Integer.toString(id);
    }

    public String formatName(String name)
    {
        return name;
    }

    public String formatTime(float time)
    {

        return String.format("%s: %s", getstr("player_ingame_time"), parsetime(time));
    }

    public String formatScore(int score)
    {
        return String.format("%s: %d", getstr("player_score"), score);
    }

    public String formatPing(SteamPlayer player)
    {
        String ping;
        if(player.isBot())
        {
            ping = "BOT";
        }
        else if(player.isHLTV())
        {
            ping = "HLTV";
        }
        else ping = String.format("%s: %d", getstr("player_ping"), player.getPing());
        return ping;
    }

    public String getstr(String resourcename)
    {
        return context.getString(res.getIdentifier(resourcename, "string", pack));
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

    public class Command extends AsyncTask<String, Void, String>
    {
        protected String doInBackground(String... args) {
            try
            {
                GoldSrcServer srv = new GoldSrcServer(server.getIP(), server.getPort());
                String cmd = args[0];
                try
                {

                    srv.rconAuth(server.getPass());
                    return srv.rconExec(cmd);
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

            new MaterialDialog.Builder(activity).title("HLRcon").content(result).positiveText("OK").show();
            // adapter.notifyDataSetChanged();
            //progressBar.setVisibility(View.GONE);

        }

    }
}
