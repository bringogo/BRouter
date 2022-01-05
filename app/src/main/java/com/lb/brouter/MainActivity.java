package com.lb.brouter;

import android.os.Bundle;
import android.util.Log;

import com.lb.brouter.api.BRouterApi;
import com.lb.brouter.api.BRouterException;
import com.lb.brouter.test.AppFragment;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // register
        BRouterApi.i().registerRouterIndex("com.demo.lb.brouter.AppBRouterIndex");


        // do route test
        String[] strArray = {"this ", "is "};
        ArrayList<String> strList = new ArrayList<>();
        strList.add("a ");
        strList.add("demo");

        char[] charArray = {'a', 'b', 'c'};
        AppFragment.Params.InnerParam innerParam = new AppFragment.Params.InnerParam(true, charArray);
        AppFragment.Params params = new AppFragment.Params("#CC3333", "a Hello from MainActivity", 101, true, strArray, strList, innerParam);

        try {
            BRouterApi.i().route("/fragment/app", getSupportFragmentManager(), R.id.fragment_dock, params);
        } catch (BRouterException e) {
            e.printStackTrace();
            Log.i("lbtest", "Err when route, " + e);

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_dock, new AppFragment()).commitAllowingStateLoss();
        }
    }
}
