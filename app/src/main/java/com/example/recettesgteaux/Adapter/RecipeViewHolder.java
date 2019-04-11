package com.example.recettesgteaux.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.recettesgteaux.R;
import com.example.recettesgteaux.remote_data_source.models.Recipe;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class RecipeViewHolder extends RecyclerView.ViewHolder implements Player.EventListener {

    private static String TAG = RecipeViewHolder.class.getSimpleName();

    @BindView(R.id.title_recipe)
    TextView mTextViewTitle;
    @BindView(R.id.start_btn)
    Button mButtonStart;
    @BindView(R.id.playerViewIntroRecipe)
    PlayerView mPlayerView;
    @BindView(R.id.exo_buffering)
    ProgressBar mProgressBar;
    @BindView(R.id.exo_artwork)
    ImageView mImageViewArtWork;
//    @BindView(R.id.exo_shutter)
//    View mView;
    @BindView(R.id.iv_play)
    ImageView mImageViewPlay;
    @BindView(R.id.exo_content_frame)
    AspectRatioFrameLayout mAspectRatioFrameLayout;

    private Context mContext;

    int idDrawable;

    View parent;
    SimpleExoPlayer mSimpleExoPlayer;
    VideoPlayerRecyclerView mVideoPlayerRecyclerView;
    Recipe mRecipe;

    UpdatePlayerAndHolder mUpdatePlayerAndHolder;

    public void resetPlayer(int currentWindow, long playbackPosition, boolean playWhenReady) {

        mSimpleExoPlayer = newExoPlayer(mContext);
        preparePlayer();

        mSimpleExoPlayer.seekTo(currentWindow,playbackPosition);

        mSimpleExoPlayer.setPlayWhenReady(playWhenReady);



    }

    public interface UpdatePlayerAndHolder{

        void onUpdatePlayerAndHolder(SimpleExoPlayer simpleExoPlayer, int numberHolder);

    }


    public RecipeViewHolder(@NonNull View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
        parent = itemView;

    }


    @OnClick(R.id.exo_content_frame)
    public void onClickPlayerView(){

        if(mSimpleExoPlayer == null){

            mSimpleExoPlayer = newExoPlayer(mContext);
            preparePlayer();

        }


        if(!mSimpleExoPlayer.getPlayWhenReady()){

            mSimpleExoPlayer.setPlayWhenReady(true);

        }else {
            mSimpleExoPlayer.setPlayWhenReady(false);
            mPlayerView.setUseController(true);
        }
    }

    @OnClick(R.id.exo_pause)
    public void onPauseButtonClicked(){

        mSimpleExoPlayer.setPlayWhenReady(false);


    }

    @OnClick(R.id.exo_play)
    public void onPlayButtonClicked(){

        if(mSimpleExoPlayer == null){

            mSimpleExoPlayer = newExoPlayer(mContext);
            preparePlayer();

        }

        mSimpleExoPlayer.setPlayWhenReady(true);

    }



    public void bind(UpdatePlayerAndHolder updatePlayerAndHolder, Recipe recipe) {

        mUpdatePlayerAndHolder = updatePlayerAndHolder;
        mRecipe = recipe;

        mTextViewTitle.setText(recipe.getName());
        parent.setTag(this);


        int idDrawable;

        switch (recipe.getName()) {
            case "Nutella Pie":
                idDrawable = R.drawable.nutella_pie;
                break;
            case "Brownies":
                idDrawable = R.drawable.brownie;
                break;
            case "Yellow Cake":
                idDrawable = R.drawable.yellow_cake;
                break;
            case "Cheesecake":
                idDrawable = R.drawable.cheesecake;
                break;
            default:
                return;

        }

        mImageViewArtWork.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),idDrawable));
        mImageViewArtWork.setVisibility(VISIBLE);

        this.idDrawable = idDrawable;
        Log.i(TAG,"bind!!!!!!!!!!"+getAdapterPosition());


    }

    public void preparePlayer() {
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, mContext.getString(R.string.app_name)));
        String mediaUrl = mRecipe.getStepsList().get(0).getVideoUrl();
        if (mediaUrl != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mediaUrl));




            mPlayerView.setPlayer(mSimpleExoPlayer);

            mSimpleExoPlayer.prepare(videoSource);

            mSimpleExoPlayer.addListener(this);

            mPlayerView.setUseController(true);
            mPlayerView.hideController();


            //pour sauvegader
            mUpdatePlayerAndHolder.onUpdatePlayerAndHolder(mSimpleExoPlayer,getAdapterPosition());
            Log.i(TAG,"prepare!!!!!!!!!!"+getAdapterPosition());
        }
    }

    public SimpleExoPlayer newExoPlayer(Context context) {

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);


        return ExoPlayerFactory.newSimpleInstance(context, trackSelector);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {


        String str = "onPlayerStateChanged: holder"+getAdapterPosition()+" playWhenReady=" + playWhenReady + ", playbackState=";
        switch (playbackState) {

            case Player.STATE_BUFFERING:
                str+="Buffering video.";
                if (mProgressBar != null && playWhenReady) {
                    mProgressBar.setVisibility(VISIBLE);
                }


                break;
            case Player.STATE_ENDED:
                str+="Video ended";
                mSimpleExoPlayer.setPlayWhenReady(false);
                mSimpleExoPlayer.seekTo(0);


                break;
            case Player.STATE_IDLE:
                str+="idle";
                break;
            case Player.STATE_READY:
                str+="Ready to play.";
                if (mProgressBar != null && playWhenReady) {
                    mProgressBar.setVisibility(GONE);
                }

                if(!playWhenReady){
                    mImageViewArtWork.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(),idDrawable));
                    mImageViewArtWork.setVisibility(View.VISIBLE);
                    mImageViewPlay.setVisibility(View.VISIBLE);
                    mPlayerView.hideController();


                }else{

                    mImageViewArtWork.setVisibility(View.GONE);
                    mImageViewPlay.setVisibility(View.GONE);
                    mPlayerView.showController();



                }


                break;
            default:
                str+="unknown";
                break;
        }
        Log.d(TAG, str);


    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

        Log.e(TAG,"onPlayerError:"+error.toString());

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
