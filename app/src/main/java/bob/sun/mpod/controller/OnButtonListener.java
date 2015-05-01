package bob.sun.mpod.controller;

import bob.sun.mpod.model.SelectionDetail;

/**
 * Created by sunkuan on 15/4/29.
 */
public interface OnButtonListener {
    public void onMenu();
    public void onPlay();
    public void onNext();
    public void onPrevious();
    public void onSelect();
}
