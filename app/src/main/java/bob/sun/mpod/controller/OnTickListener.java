package bob.sun.mpod.controller;

/**
 * Created by sunkuan on 2015/4/23.
 */
public interface OnTickListener {
    public void onNextTick();
    public void onPreviousTick();
    public void onMenu();
    public void onPlay();
    public void onNext();
    public void onPrevious();
}
