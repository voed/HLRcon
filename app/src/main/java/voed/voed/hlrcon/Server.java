package voed.voed.hlrcon;

import java.io.Serializable;

public class Server implements Serializable{
    private String IP;
    private String pass;
    private int port;
    private String hostname;
    private String mapname;
    private String players;
    private String maxplayers;
    private int img;
    private boolean isOnline;
    private String banCommand;

    public Server(String IP, int port, String pass, String hostname)
    {
        this.IP = IP;
        this.port = port;
        this.pass = pass;
        this.hostname = hostname;
    }

    public void setIP(String IP)
    {
        this.IP = IP;
    }

    public String getIP()
    {
        return this.IP;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getPort()
    {
        return this.port;
    }

    public void setPass(String pass)
    {
        this.pass = pass;
    }

    public String getPass()
    {
        return this.pass;
    }

    public void setHostname(String hostname)
    {
        this.hostname = hostname;
    }

    public String getHostName() {
        return this.hostname;
    }

    public String getMapname()
    {
        return this.mapname;
    }

    public void setMapname(String mapname)
    {
        this.mapname = mapname;
    }

    public String getPlayers()
    {
        return this.players;
    }

    public void setPlayers(String players)
    {
        this.players = players;
    }

    public String getMaxPlayers()
    {
        return this.maxplayers;
    }

    public void setMaxplayers(String maxplayers)
    {
        this.maxplayers = maxplayers;
    }

    public String getHostIP()
    {
        return this.IP + ":"+this.port;
    }

    public void setImg(int img)
    {
        this.img = img;
    }

    public int getImg()
    {
        return this.img;
    }

    public void setOnline(boolean isOnline)
    {
        this.isOnline = isOnline;
    }

    public boolean getOnline()
    {
        return this.isOnline;
    }

    public void setBanCommand(String banCommand)
    {
        this.banCommand = banCommand;
    }

    public String getBanCommand()
    {
        return this.banCommand;
    }

}
