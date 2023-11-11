package org.example;

import java.security.InvalidParameterException;

public class EnvironmentCredentials {
    private  String url;
    private  String name;
    private  String pass;

    public EnvironmentCredentials() {
        setEnv("deb");
    }

    public void setEnv(String env) {
        switch (env) {
            case "deb":
                initDebianData();
                break;
            case  "nord":
                initNordData();
                break;
            case "70":
                init70Data();
                break;
            default:
                throw new InvalidParameterException("Введено несуществующее окружение");
        }
    }

    private void init70Data() {
        this.url = "jdbc:postgresql://{DB_URL:PORT}/{DB_NAME}";
        this.name = "{USERNAME}";
        this.pass = "{PASS}";
    }

    private void initNordData() {
        this.url = "jdbc:postgresql://{DB_URL:PORT}/{DB_NAME}";
        this.name = "{USERNAME}";
        this.pass = "{PASS}";

    }

    private void initDebianData() {
        this.url = "jdbc:postgresql://{DB_URL:PORT}/{DB_NAME}";
        this.name = "{USERNAME}";
        this.pass = "{PASS}";
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }
}
