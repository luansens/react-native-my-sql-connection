package com.reactlibrary;

public class ConnectionConfiguration{
    private String host;
    private Integer port;
    private String database;
    private String user;
    private String password;
    private String params;

    public String getParams() {
        return this.params;
    }

    public void setParams(String params) {
        this.params = params;
    }
    
    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String BuildConnection(){
        String connString = "jdbc:mysql://" + getHost();
        if(getPort() != null){
            connString += ":" + getPort().toString();
        }
        connString += "/" + getDatabase();
        if(getParams() != null){
            connString += getParams();
        }
        return connString;
    }
}