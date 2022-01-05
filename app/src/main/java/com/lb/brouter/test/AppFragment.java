package com.lb.brouter.test;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lb.brouter.R;
import com.lb.brouter.anno.BRouter;
import com.lb.brouter.anno.BRouterMethod;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

@BRouter(path = "/fragment/app", desc = "app fragment")
public class AppFragment extends Fragment {
    private Params params;

    public static class Params implements Serializable {
        public String bgColor;
        public String aStr;
        public int aInt;
        public boolean aBool;
        public String[] strArray;
        public ArrayList<String> strList;

        public InnerParam innerParam;

        public static class InnerParam {
            public Boolean aBoolean;
            public char[] charArray;

            public InnerParam(Boolean aBoolean, char[] charArray) {
                this.aBoolean = aBoolean;
                this.charArray = charArray;
            }

            @Override
            public String toString() {
                return "InnerParam{" +
                        "aBoolean=" + aBoolean +
                        ", charArray=" + Arrays.toString(charArray) +
                        '}';
            }
        }

        public Params(String bgColor, String aStr, int aInt, boolean aBool, String[] strArray,
                      ArrayList<String> strList, InnerParam innerParam) {
            this.bgColor = bgColor;
            this.aStr = aStr;
            this.aInt = aInt;
            this.aBool = aBool;
            this.strArray = strArray;
            this.strList = strList;
            this.innerParam = innerParam;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "bgColor='" + bgColor + '\'' +
                    ", aStr='" + aStr + '\'' +
                    ", aInt=" + aInt +
                    ", aBool=" + aBool +
                    ", strArray=" + Arrays.toString(strArray) +
                    ", strList=" + strList +
                    ", innerParam=" + innerParam +
                    '}';
        }
    }

    @BRouterMethod
    public static boolean launch(FragmentManager fragmentManager, @IdRes int containerViewId, Params params) {
        if (fragmentManager == null || params == null) {
            return false;
        }
        AppFragment fragment = new AppFragment();
        fragment.params = params;
        fragmentManager.beginTransaction().add(containerViewId, fragment).commitAllowingStateLoss();
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_plain, container, false);
        if (params != null) {
            view.setBackgroundColor(Color.parseColor(params.bgColor));
            ((TextView) view.findViewById(R.id.tv_note)).setText("AppFragment  params: \n" + params.toString());
        } else {
            view.setBackgroundColor(Color.parseColor("#0033FF"));
            ((TextView) view.findViewById(R.id.tv_note)).setText("AppFragment");
        }
        return view;
    }
}
