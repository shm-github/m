package com.example.hossein.movieleitner;

/**
 * Created by Hossein on 10/30/2016.
 */
public class Application  extends android.app.Application{
    private static Application ourInstance = new Application();

    public static Application getInstance() {
        return ourInstance;
    }

    public Application() {
    }
}
