package com.example.recettesgteaux.Adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.example.recettesgteaux.remote_data_source.models.Recipe;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VideoPlayerRecyclerView extends RecyclerView implements RecipeViewHolder.UpdatePlayerAndHolder{

    private static final String TAG = VideoPlayerRecyclerView.class.getSimpleName();

    // ui
    private SimpleExoPlayer mSimpleExoPlayer;
    private GridLayoutManager mGridLayoutManager;
    private int columnWidth = -1;
    private RecipeViewHolder mRecipeViewHolder;


    // vars
    private List<Recipe> mRecipeList;
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private Context mContext;
    private int playPosition = -1;

    private boolean playWhenReady;
    private int mCurrentWindow;
    private long mPlaybackPosition;
    private int mNumberHolder;


    public VideoPlayerRecyclerView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public VideoPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attributeSet) {

        mGridLayoutManager = new GridLayoutManager(context, 1);
        setLayoutManager(mGridLayoutManager);

        if (attributeSet != null) {
            // list the attributes we want to fetch
            int[] attrsArray = {
                    android.R.attr.columnWidth
            };
            TypedArray array = context.obtainStyledAttributes(attributeSet, attrsArray);
            //retrieve the value of the 0 index, which is columnWidth
            columnWidth = array.getDimensionPixelSize(0, -1);
            array.recycle();
        }


        this.mContext = context.getApplicationContext();
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d(TAG, "onScrollStateChanged: called.");



                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true);
                    } else {
                        playVideo(false);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                Log.i(TAG,"onScrolled!!!!!!!!!!!!!!!!");
                resetPlayer();
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {




            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });

    }



    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);

        if (columnWidth > 0) {
            //The spanCount will always be at least 1
            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
            mGridLayoutManager.setSpanCount(spanCount);
        }


    }

    public void playVideo(boolean isEndOfList) {

        int targetPosition;

        if (!isEndOfList) {
            int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }

            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return;
            }

            // if there is more than 1 list-item on the screen
            if (startPosition != endPosition) {
                int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

                targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
            } else {
                targetPosition = startPosition;
            }
        } else {
            targetPosition = mRecipeList.size() - 1;
        }

        Log.d(TAG, "playVideo: target position: " + targetPosition);

        // video is already playing so return
        if (targetPosition < 0 ||targetPosition == playPosition) {
            return;
        }

        // set the position of the list-item that is to be played
        playPosition = targetPosition;

        int currentPosition = targetPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();

        View child = getChildAt(currentPosition);
        if (child == null) {
            return;
        }

        //aller chercher le recipe correspondant
        mRecipeViewHolder = (RecipeViewHolder) child.getTag();
        //ne pas rejouer ce qui est en cours
        if(mRecipeViewHolder.mSimpleExoPlayer != null && mRecipeViewHolder.mSimpleExoPlayer.getPlayWhenReady()){
            return;
        }

        if (mRecipeViewHolder == null) {
            playPosition = -1;
            return;
        }

        mSimpleExoPlayer = mRecipeViewHolder.mSimpleExoPlayer;
        mRecipeViewHolder.onClickPlayerView();

    }




    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     *
     * @param playPosition
     * @return
     */
    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at);

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }



    public void releasePlayer() {

        if(mSimpleExoPlayer != null){

            mPlaybackPosition = mSimpleExoPlayer.getCurrentPosition();
            mCurrentWindow = mSimpleExoPlayer.getCurrentWindowIndex();
            playWhenReady = mSimpleExoPlayer.getPlayWhenReady();

            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
        }


    }

    public boolean isPlayWhenReady() {
        return playWhenReady;
    }

    public int getCurrentWindow() {
        return mCurrentWindow;
    }

    public long getPlaybackPosition() {
        return mPlaybackPosition;
    }

    public int getNumberHolder() {
        return mNumberHolder;
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.playWhenReady = playWhenReady;

    }

    public void resetPlayer() {
        Log.i(TAG,"mmmmmm  "+mNumberHolder);
        RecipeViewHolder holder =(RecipeViewHolder) getChildAt(mNumberHolder).getTag();

        holder.resetPlayer(mCurrentWindow,mPlaybackPosition,playWhenReady);

    }

    public void setCurrentWindow(int currentWindow) {
        mCurrentWindow = currentWindow;
    }

    public void setPlaybackPosition(long playbackPosition) {
        mPlaybackPosition = playbackPosition;

    }

    public void setNumberHolder(int numberHolder) {
        mNumberHolder = numberHolder;
    }

    public void setListRecipe(List<Recipe> recipeList) {
        this.mRecipeList = recipeList;
    }

    private void pauseAllOtherPlayer() {
        for (int childCount = getChildCount(), i = 0; i < childCount; i++) {

            RecipeViewHolder holder =(RecipeViewHolder) getChildViewHolder(getChildAt(i));

            if(mSimpleExoPlayer != null && holder.mSimpleExoPlayer != mSimpleExoPlayer && holder.mSimpleExoPlayer!= null && holder.mSimpleExoPlayer.getPlayWhenReady()){

                holder.mSimpleExoPlayer.setPlayWhenReady(false);
                holder.mSimpleExoPlayer.release();
                holder.mSimpleExoPlayer = null;
            }
        }
    }

    @Override
    public void onUpdatePlayerAndHolder(SimpleExoPlayer simpleExoPlayer,int numberHolder) {

        mNumberHolder = numberHolder;
        mSimpleExoPlayer = simpleExoPlayer;

        pauseAllOtherPlayer();

    }
}