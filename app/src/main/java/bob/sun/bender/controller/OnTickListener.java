package bob.sun.bender.controller;

import bob.sun.bender.model.SelectionDetail;

/**
 * Created by sunkuan on 2015/4/23.
 */
public interface OnTickListener {
    public void onNextTick();
    public void onPreviousTick();
    public SelectionDetail getCurrentSelection();
}
