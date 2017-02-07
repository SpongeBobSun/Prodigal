package bob.sun.mpod.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bob.sun.mpod.MainActivity;
import bob.sun.mpod.R;
import bob.sun.mpod.adapters.MenuAdapter;
import bob.sun.mpod.controller.OnTickListener;
import bob.sun.mpod.model.MediaLibrary;
import bob.sun.mpod.model.SelectionDetail;
import bob.sun.mpod.model.SongBean;
import bob.sun.mpod.service.PlayerService;
import bob.sun.mpod.view.AlbumStack;

/**
 * Created by sunkuan on 2015/4/23.
 */
public class MainMenuFragment extends TwoPanelFragment implements OnTickListener {
//    ListView listView;
    RecyclerView theList;
    int currentItemIndex;
    ImageView imageView;
    FrameLayout rightPanelContent;
    LinearLayout nowPlayingPage;
    LinearLayout rightPanel;
    FrameLayout leftPanel;
    AlbumStack albumStack;

    private static int menuIcons[] = {
            R.drawable.artist,
            R.drawable.album,
            R.drawable.cover_flow,
            R.drawable.songs,
            R.drawable.playlist,
            R.drawable.genre,
            R.drawable.shuffle,
            R.drawable.settings,
            R.drawable.songs
    };
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup parent,Bundle savedInstanceState){
        View ret = inflater.inflate(R.layout.layout_main_menu,parent,false);
        theList = (RecyclerView) ret.findViewById(R.id.id_list_view_main_menu);
        theList.setAdapter(MenuAdapter.getStaticInstance(getActivity()).getMainMenuAdapter());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            theList.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            theList.setLayoutManager(new LinearLayoutManager(parent.getContext()));
        }
//        theList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        currentItemIndex = 0;
        MenuAdapter.getStaticInstance(null).HighlightItem(0);
        imageView = (ImageView) ret.findViewById(R.id.id_main_menu_image);
        nowPlayingPage = (LinearLayout) ret.findViewById(R.id.id_mainmenu_nowplaying);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),menuIcons[0]));
        rightPanelContent = (FrameLayout) ret.findViewById(R.id.right_panel_content);
        albumStack = (AlbumStack) ret.findViewById(R.id.id_album_stack);
        albumStack.init();

        leftPanel = (FrameLayout) ret.findViewById(R.id.main_menu_left);
        rightPanel = (LinearLayout) ret.findViewById(R.id.main_menu_right);
        return ret;
    }

    @Override
    public void onNextTick() {
        if(currentItemIndex >= theList.getAdapter().getItemCount()-1){
            currentItemIndex = theList.getAdapter().getItemCount()-1;
            return;
        }
        currentItemIndex+=1;
        theList.requestFocus();
        if(currentItemIndex >= ((LinearLayoutManager)theList.getLayoutManager()).findLastCompletelyVisibleItemPosition())
            theList.smoothScrollToPosition(currentItemIndex);
        MenuAdapter.getStaticInstance(null).HighlightItem(currentItemIndex);
        if (currentItemIndex == theList.getAdapter().getItemCount() -1){
            rightPanelContent.setVisibility(View.GONE);
            nowPlayingPage.setVisibility(View.VISIBLE);
            PlayerService playerService = ((MainActivity) getActivity()).playerService;
            if (playerService == null || playerService.getCurrentSong() == null){
                ((TextView) nowPlayingPage.findViewById(R.id.id_mainmenu_nowplaying_artist)).setText("Nobody");
                ((TextView) nowPlayingPage.findViewById(R.id.id_mainmenu_nowplaying_title)).setText("Nothing");
                return;
            }
            SongBean song = playerService.getCurrentSong();
            ((TextView) nowPlayingPage.findViewById(R.id.id_mainmenu_nowplaying_artist)).setText(song.getArtist());
            ((TextView) nowPlayingPage.findViewById(R.id.id_mainmenu_nowplaying_title)).setText(song.getTitle());
            ImageView currentCover = (ImageView) nowPlayingPage.findViewById(R.id.id_now_playing_cover);
            String img = MediaLibrary.getStaticInstance(nowPlayingPage.getContext()).getCoverUriByAlbumId(song.getAlbumId());

            Picasso.with(getActivity())
                    .load(Uri.parse(img)).fit().centerInside()
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.album)
                    .error(R.drawable.album)
                    .into(currentCover);
            return;
        } else if (currentItemIndex == 1) {
            albumStack.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            return;
        }
        albumStack.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        imageView.destroyDrawingCache();
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),menuIcons[currentItemIndex]));
    }

    @Override
    public void onPreviousTick() {
        if(currentItemIndex < 1){
            return;
        }
        if (currentItemIndex == theList.getAdapter().getItemCount()-1) {
            rightPanelContent.setVisibility(View.VISIBLE);
            nowPlayingPage.setVisibility(View.GONE);
        }
        currentItemIndex -= 1;
        if (currentItemIndex == 1) {
            albumStack.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
        } else {
            albumStack.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }
        theList.requestFocus();
        if(currentItemIndex <= ((LinearLayoutManager)theList.getLayoutManager()).findFirstCompletelyVisibleItemPosition())
            theList.smoothScrollToPosition(currentItemIndex);
        MenuAdapter.getStaticInstance(null).HighlightItem(currentItemIndex);
        imageView.destroyDrawingCache();
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), menuIcons[currentItemIndex]));
    }

    @Override
    public SelectionDetail getCurrentSelection(){
        SelectionDetail ret = new SelectionDetail();
        ret.setMenuType(ret.MENU_TPYE_MAIN);
        ret.setDataType(ret.DATA_TYPE_STRING);
        ret.setData((((MenuAdapter.MainMenuAdapter)theList.getAdapter()).getItem(currentItemIndex)).menuType);
        return ret;
    }

    @NonNull
    @Override
    public View getLeftPanel() {
        return leftPanel;
    }

    @NonNull
    @Override
    public View getRightPanel() {
        return rightPanel;
    }
}
