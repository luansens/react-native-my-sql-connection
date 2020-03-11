import { NativeModules } from 'react-native';

const { MySqlConnection } = NativeModules;

const connection = {
    executeQuery : MySqlConnection.executeQuery,
    executeUpdate: MySqlConnection.executeUpdate,
    close : MySqlConnection.close
}

export default {
    createConnection : async (config) =>{
        await MySqlConnection.connect(config);
        return connection;
    }
}