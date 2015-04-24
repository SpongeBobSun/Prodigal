package bob.sun.mpod.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bob.sun.mpod.R;

/**
 * Created by bob.sun on 2015/4/23.
 */
public class MainMenu extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View ret = null;
        ret = inflater.inflate(R.layout.layout_main_menu,parent,false);
        return ret;
    }
}
