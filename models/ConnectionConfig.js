export default class ConnectionConfiguration{
    constructor(config){
        this.host = config.host;
        this.database = config.database;
        this.port = config.port;
        this.user = config.user;
        this.password = config.password;
        this.params = config.params;
    }
}