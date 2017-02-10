package bob.sun.bender.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import bob.sun.bender.R;
import bob.sun.bender.controller.OnTickListener;
import bob.sun.bender.model.SelectionDetail;

/**
 * Created by bobsun on 15-5-22.
 */
public class AboutFragment extends Fragment implements OnTickListener {
    private ScrollView scrollView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View ret =  inflater.inflate(R.layout.layout_about,parent,false);
        scrollView = (ScrollView) ret.findViewById(R.id.id_scroll_view_about);
        return ret;
    }

    @Override
    public void onNextTick() {
        if (scrollView == null)
            return;
    }

    @Override
    public void onPreviousTick() {
        if (scrollView == null)
            return;
    }

    @Override
    public SelectionDetail getCurrentSelection() {
        SelectionDetail selectionDetail = new SelectionDetail();
        selectionDetail.setMenuType(SelectionDetail.MENU_TYPE_UNUSED);
        return selectionDetail;
    }

}
