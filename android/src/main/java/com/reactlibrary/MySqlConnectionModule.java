package com.reactlibrary;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableArray;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlConnectionModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private Connection connection;

    public MySqlConnectionModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "MySqlConnection";
    }

    private ConnectionConfiguration ReadConfig(ReadableMap config) {
        ConnectionConfiguration configuration = new ConnectionConfiguration();
        configuration.setHost(config.getString("host"));
        configuration.setDatabase(config.getString("database"));
        configuration.setUser(config.getString("user"));
        configuration.setPassword(config.getString("password"));
        if(config.hasKey("params")){
            if(!config.isNull("params"))
                configuration.setParams(config.getString("params"));
        }
        if(config.hasKey("port")){
            if(!config.isNull("port"))
                configuration.setPort(config.getInt("port"));
        }
        return configuration;
    }

    @ReactMethod
    public void connect(ReadableMap config, Promise promise) {
        ConnectionConfiguration configuration = null;
        try{
            configuration = ReadConfig(config);
        }catch(Exception e){
            promise.reject("500","Error while read configuration object " + e.getMessage(),e);
        }
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                configuration.BuildConnection(),
                configuration.getUser(),
                configuration.getPassword());
            this.connection = conn;
            promise.resolve(null);
        }catch(Exception e){
            promise.reject("500","Error when open connection "+ e.getMessage(), e);
        }
    }

    @ReactMethod
    public void executeQuery(String query, Promise promise) {
        ResultSet resultSet = null;
        try{
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
        }catch(Exception e){
            promise.reject("500","Error when execute query "+ e.getMessage(), e);
        }
        try{
            WritableArray results = PrepareResultSet(resultSet);
            promise.resolve(results);
        }catch(Exception e){
            promise.reject("500","Error when execute parse of results "+ e.getMessage(), e);
        }
    }

    @ReactMethod
    public void executeUpdate(String query, Promise promise) {
        try{
            Statement statement = connection.createStatement();
            int affectedRows = statement.executeUpdate(query);
            promise.resolve(affectedRows);
        }catch(Exception e){
            promise.reject("500","Error when execute UPDATE | INSERT | DELETE "+ e.getMessage(), e);
        }
    }

    private WritableArray PrepareResultSet(ResultSet resultSet) throws Exception{
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numOfColumns = metaData.getColumnCount();
        WritableArray results = Arguments.createArray();
        while(resultSet.next()){
            WritableMap writableMap = Arguments.createMap();
            for(int i = 1; i <= numOfColumns; i++){
                Object currentObj = resultSet.getObject(i);
                String currentColumnName = metaData.getColumnName(i);
                if(currentObj == null){
                    writableMap.putNull(currentColumnName);
                    continue;
                }
                if(currentObj instanceof Integer){
                    writableMap.putInt(currentColumnName, (Integer) currentObj );
                }else if(currentObj instanceof Boolean){
                    writableMap.putBoolean(currentColumnName, (Boolean) currentObj );
                }else if(currentObj instanceof Double){
                    writableMap.putDouble(currentColumnName, (Double) currentObj );
                }else if(currentObj instanceof String){
                    writableMap.putString(currentColumnName, (String) currentObj );
                }else{
                    writableMap.putString(currentColumnName, String.valueOf(currentObj) );
                }
            }
            results.pushMap(writableMap);
        }
        return results;
    }

    @ReactMethod
    public void close(Promise promise) {
        ResultSet resultSet = null;
        try{
            connection.close();
            promise.resolve(null);
        }catch(Exception e){
            promise.reject("500", "Error when close connection "+ e.getMessage(),e);
        }
    }
}
