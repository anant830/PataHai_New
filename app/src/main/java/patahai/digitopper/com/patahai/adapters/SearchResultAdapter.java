package patahai.digitopper.com.patahai.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.devs.readmoreoption.ReadMoreOption;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import patahai.digitopper.com.patahai.MainActivity;
import patahai.digitopper.com.patahai.R;
import patahai.digitopper.com.patahai.constants.Constants;
import patahai.digitopper.com.patahai.fragment.CategoryFragment;
import patahai.digitopper.com.patahai.fragment.FollowingFrag;
import patahai.digitopper.com.patahai.fragment.None_Fragment;
import patahai.digitopper.com.patahai.fragment.PublisherNameFragment;
import patahai.digitopper.com.patahai.interfaces.CheckPermission;
import patahai.digitopper.com.patahai.interfaces.MainApiRecall;
import patahai.digitopper.com.patahai.interfaces.ReLoadFollowers;
import patahai.digitopper.com.patahai.model.FollowInterestRequestNew;
import patahai.digitopper.com.patahai.model.MultiViewInterest;
import patahai.digitopper.com.patahai.model.MultiViewRequest;
import patahai.digitopper.com.patahai.model.NewsRequest;
import patahai.digitopper.com.patahai.views.activity.RelatedStories;
import patahai.digitopper.com.patahai.views.activity.WebViewAct_Advance;
import patahai.digitopper.com.patahai.webservice.APIsClient;
import patahai.digitopper.com.patahai.webservice.PataHaiUrls;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeMultiViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private YouTubePlayer youTubePlayer;
    private Context context;
    private ArrayList<MultiViewRequest> list;
    private MainApiRecall recall;
    private ReLoadFollowers reLoadFollowers;
    private CheckPermission permission;
    private  ReadMoreOption readMoreOption;
    private FirebaseAnalytics mFirebaseAnalytics;
    public HomeMultiViewAdapter(Context context, ArrayList<MultiViewRequest> list) {
    this.context = context;
    this.list    = list;
    recall   = (MainApiRecall) context;
    reLoadFollowers = (ReLoadFollowers) context;
    permission      = (CheckPermission) context;
    readMoreOption  = new ReadMoreOption.Builder(context).moreLabel(context.getResources().getString(R.string.read_more))
            .lessLabel(context.getResources().getString(R.string.read_less))
            .moreLabelColor(context.getResources().getColor(R.color.time_color))
            .lessLabelColor(context.getResources().getColor(R.color.time_color))
            .labelUnderLine(true)
            .build();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
         case Constants.VIDEO_PLAYER_VIEW_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_view_for_video, parent, false);
                return new VideoTypeViewHolder(view);
         case Constants.IMAGE__WITH_TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_view_for_image, parent, false);
                return new ImageTypeViewHolder(view);
         case Constants.ONLY__WITH_TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_view_for_text, parent, false);
                return new TextTypeViewHolder(view);

         case Constants.ONLY__WITH_INSTA_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_view_for_instagram, parent, false);
                return new InstaTypeViewHolder(view);
         case Constants.ONLY__WITH_FACEBOOK_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_view_for_facebook, parent, false);
                return new FacebookTypeViewHolder(view);
         case Constants.ONLY__WITH_TWEETER_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_view_for_tweeter, parent, false);
                return new TwitterTypeViewHolder(view);

         default:
              view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item_view_for_blank, parent, false);
              return new TextTypeViewHolderBlank(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getFlashcard().equals(1)) {
            return MultiViewRequest.IMAGE__WITH_TEXT_TYPE;
        } else if (list.get(position).getFlashcard().equals(2)) {
            return MultiViewRequest.VIDEO_PLAYER_VIEW_TYPE;
        } else if (list.get(position).getFlashcard().equals(3)) {
            return MultiViewRequest.TEXT_TYPE;
        }
        else if (list.get(position).getFlashcard().equals(4)) {
            return MultiViewRequest.INSTAGRAM_TYPE;
        }
        else if (list.get(position).getFlashcard().equals(5)) {
            return MultiViewRequest.FACEBOOK_TYPE;
        }
        else if (list.get(position).getFlashcard().equals(6)) {
            return MultiViewRequest.TWITTER_TYPE;
        }
        else if (list.get(position).getVideoid().trim().isEmpty() && list.get(position).getFlashcard().equals(2)) {
            return -2;
        } else {
            return -1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int pos) {
        final MultiViewRequest object = list.get(pos);

        if (object != null) {
            switch (object.getFlashcard()) {
                case MultiViewRequest.IMAGE__WITH_TEXT_TYPE:

                      Constants.MainCategory(context,((ImageTypeViewHolder) holder).newsText,((ImageTypeViewHolder) holder).publish_time,((ImageTypeViewHolder) holder).view,object.getCat_id());
                    if (object.getHeadline().trim() == null || object.getHeadline().trim().equalsIgnoreCase("Null"))
                        ((ImageTypeViewHolder) holder).heading.setVisibility(View.GONE);


                    ((ImageTypeViewHolder) holder).heading.setText(Html.fromHtml(object.getHeadline().trim()));
                    ((ImageTypeViewHolder) holder).content.setText(Html.fromHtml(object.getFact().trim()));


                    ((ImageTypeViewHolder) holder).category_TV.setText("#" + Html.fromHtml(object.getCategory().trim()));


                    ((ImageTypeViewHolder) holder).publisher_Name.setText(Constants.setDot(object.getSource().trim()));
                    ((ImageTypeViewHolder) holder).publish_time.setText(Constants.getAgo(object.getPublish_dateon().trim()));
                    ((ImageTypeViewHolder) holder).publish_time.setVisibility(View.VISIBLE);


                       try {


                    //((ImageTypeViewHolder) holder).feed_from_source.setText("via " + object.getDiscoverer_origin().trim());
                    Glide.with(context).load(object.getDiscoverer_origin().trim()).into(((ImageTypeViewHolder) holder).source_logo);
                    Constants.setPubLogo(context, object.getLogo(), ((ImageTypeViewHolder) holder).publisher_icon);

                    if (object.getAvatar().trim().contains("https://sb.scorecardresearch.com/p?")) {
                        ((ImageTypeViewHolder) holder).image.setVisibility(View.GONE);
                        ((ImageTypeViewHolder) holder).btnPlay.setVisibility(View.GONE);
                    } else {
                        ((ImageTypeViewHolder) holder).image.setVisibility(View.VISIBLE);
                        RequestOptions requestOptions = new RequestOptions();

                        requestOptions.placeholder(R.drawable.placeholder_img);
                        requestOptions.error(R.drawable.placeholder_img);

                        //((ImageTypeViewHolder) holder).image.setMinimumHeight(200);
                        // Glide.with(context).load(object.getAvatar()).into(((ImageTypeViewHolder) holder).image);
                        Glide.with(context).setDefaultRequestOptions(requestOptions).load(object.getAvatar()).into(((ImageTypeViewHolder) holder).image);


                        if (!object.getVideolink().trim().isEmpty() && object.getVideolink() != null) {
                            ((ImageTypeViewHolder) holder).btnPlay.setVisibility(View.VISIBLE);
                        } else {
                            ((ImageTypeViewHolder) holder).btnPlay.setVisibility(View.GONE);
                        }

                        ((ImageTypeViewHolder) holder).Re_View.setVisibility(View.GONE);
                        ((ImageTypeViewHolder) holder).image.setVisibility(View.VISIBLE);
                        ((ImageTypeViewHolder) holder).heading.setVisibility(View.VISIBLE);

                         }

                       }catch (Exception e){}

                    //(ImageTypeViewHolder) holder).webview.loadUrl("");
                    ((ImageTypeViewHolder) holder).category_TV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.CardInterest(mFirebaseAnalytics,object);

                            Constants.catList.addAll(list);
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.CAT_ID, object.getCat_id());
                            bundle.putString(Constants.CAT_NAME, object.getCategory());
                            bundle.putString(Constants.CAT_NAME_SHOW, object.getCategory());
                            bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                            bundle.putString(Constants.CAT_FOLLOW_STATUS, "1");
                            Fragment frg = new CategoryFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();
                        }
                    });

                    ((ImageTypeViewHolder) holder).publisher_Name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pauseVideo();


                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.CAT_ID, object.getCat_id());
                            bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                            bundle.putString(Constants.CAT_NAME, object.getSource());
                            bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                            bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                            Fragment frg = new PublisherNameFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();



                        }
                    });


                ((ImageTypeViewHolder) holder).ReadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                Constants.IMAGE    = ((BitmapDrawable) ((ImageTypeViewHolder) holder).image.getDrawable()).getBitmap();
                Constants.PUB_LOGO = ((BitmapDrawable) ((ImageTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                if (((ImageTypeViewHolder) holder).image.getVisibility() == View.GONE)
                Constants.IMAGE    = null;

                Intent intent = new Intent(context, WebViewAct_Advance.class);
                intent.putExtra(Constants.WEBLINK, object.getSourcelink());
                intent.putExtra(Constants.WEBPAGEID, object.getId());
                intent.putExtra(Constants.Search_Data, object);
                context.startActivity(intent);

                        }
                    });


                    ((ImageTypeViewHolder) holder).bottomOptions.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View view) {
                     bottomView(pos);

                        }
                    });
                  ((ImageTypeViewHolder) holder).sharePost.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                    Constants.shareEvent(mFirebaseAnalytics,object);


                    Bitmap bitmapImage = ((BitmapDrawable) ((ImageTypeViewHolder) holder).image.getDrawable()).getBitmap();
                    Bitmap bitmapLogo  = ((BitmapDrawable) ((ImageTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                    Bitmap sourceLogo  = ((BitmapDrawable) ((ImageTypeViewHolder) holder).source_logo.getDrawable()).getBitmap();

                    if (((ImageTypeViewHolder) holder).image.getVisibility() == View.GONE)
                          bitmapImage = null;

                    shareCard(context, Constants.WEBVIEWURL + object.getId(),((ImageTypeViewHolder) holder).category_TV.getText().toString(),((ImageTypeViewHolder) holder).heading.getText().toString(),((ImageTypeViewHolder) holder).content.getText().toString(),
                    ((ImageTypeViewHolder) holder).publisher_Name.getText().toString(),bitmapImage,bitmapLogo,sourceLogo,"",object);

                            }


                    });
                    ((ImageTypeViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //  ((ImageTypeViewHolder) holder).image.setClickable(false);
                           // new FinestWebView.Builder(context).show("https://blueprint.digitopper.com/1/1.swf");

                            ((ImageTypeViewHolder) holder).ReadMore.performClick();

                        }
                    });

                    ((ImageTypeViewHolder) holder).publisher_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pauseVideo();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.CAT_ID, object.getCat_id());
                            bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                            bundle.putString(Constants.CAT_NAME, object.getSource());
                            bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                            bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                            Fragment frg = new PublisherNameFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                        }
                    });


                    // ((ImageTypeViewHolder) holder).related_stories.setVisibility(View.VISIBLE);
                    ((ImageTypeViewHolder) holder).related_stories.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, RelatedStories.class);
                            i.putExtra(Constants.Search_Data, object);
                            context.startActivity(i);
                        }
                    });

                    Constants.bookMarkView(((ImageTypeViewHolder) holder).bookmark_img, ((ImageTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                    Constants.relatedStoryView(((ImageTypeViewHolder) holder).related_stories,object.getNum_children());
                    ((ImageTypeViewHolder)holder).agegroup.setText(object.getAgebar());

                    ((ImageTypeViewHolder) holder).bookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String state;
                            String message;
                            if(object.getBookmarked().trim().equalsIgnoreCase("0")) {
                                state = "1";
                                message = context.getString(R.string.bookmark_message);
                            }
                            else {
                                state = "0";
                                message = context.getString(R.string.bookmark_message_un);
                            }
                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();


                            Constants.bookMarkView(((ImageTypeViewHolder) holder).bookmark_img, ((ImageTypeViewHolder) holder).bookmark_txt,state);


                            object.setBookmarked(state);
                            NewsRequest obj = new NewsRequest();
                            obj.setPostid(String.valueOf(object.getId()));
                            obj.setDeviceid(Constants.DeviceID);
                            obj.setStatus(state);
                            hitbookmark(obj);
                           // notifyDataSetChanged();
                        }
                    });

                    ((ImageTypeViewHolder) holder).Whatsapp_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.shareEvent(mFirebaseAnalytics,object);

                            Bitmap bitmapImage = ((BitmapDrawable) ((ImageTypeViewHolder) holder).image.getDrawable()).getBitmap();
                            Bitmap bitmapLogo = ((BitmapDrawable) ((ImageTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                            Bitmap sourceLogo = ((BitmapDrawable) ((ImageTypeViewHolder) holder).source_logo.getDrawable()).getBitmap();
                            if (((ImageTypeViewHolder) holder).image.getVisibility() == View.GONE)
                                bitmapImage = null;

                            shareCard(context, Constants.WEBVIEWURL + object.getId(), ((ImageTypeViewHolder) holder).category_TV.getText().toString(), ((ImageTypeViewHolder) holder).heading.getText().toString(), ((ImageTypeViewHolder) holder).content.getText().toString(),
                                    ((ImageTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapImage, bitmapLogo,sourceLogo,Constants.WhatsApp,object);
                        }
                    });

                    ((ImageTypeViewHolder)holder).newsText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mainCategoryClick(object);
                        }
                    });

                    break;

                case MultiViewRequest.TEXT_TYPE:

                    if (object.getFact().trim() == null || object.getFact().trim().equalsIgnoreCase("Null") || object.getFact().equalsIgnoreCase("NULL"))
                        ((TextTypeViewHolder) holder).content.setVisibility(View.GONE);
                    if (object.getHeadline() == null || object.getHeadline().equalsIgnoreCase("Null"))
                        ((TextTypeViewHolder) holder).heading.setVisibility(View.GONE);

                    //  if(object.getAvatar().isEmpty()||object.getAvatar().equalsIgnoreCase(""))
                    //  ((ImageTypeViewHolder) holder).image.setVisibility(View.GONE);
                    //  else
                    //  ((ImageTypeViewHolder) holder).image.setVisibility(View.VISIBLE);

                     Constants.MainCategory(context,((TextTypeViewHolder) holder).newsText,((TextTypeViewHolder) holder).publish_time,((TextTypeViewHolder) holder).view,object.getCat_id());

                    ((TextTypeViewHolder) holder).heading.setText(Html.fromHtml(object.getHeadline().trim().replaceAll("&lt;p style=\\\"text-align: justify;", "")));
                    ((TextTypeViewHolder) holder).content.setText(Html.fromHtml(object.getFact().trim().replaceAll("<p style=\"text-align: justify;\"><strong>", "")));

                    ((TextTypeViewHolder) holder).category_TV.setText("#" + Html.fromHtml(object.getCategory().trim()));
                    ((TextTypeViewHolder) holder).publisher_Name.setText(Constants.setDot(object.getSource().trim()));
                    ((TextTypeViewHolder) holder).publish_time.setText(Constants.getAgo(object.getEntryDate().trim()));
                    // ((TextTypeViewHolder) holder).feed_from_source.setText("via " + object.getDiscoverer_origin().trim());
                    ((TextTypeViewHolder) holder).publish_time.setVisibility(View.VISIBLE);
                    ((TextTypeViewHolder) holder).view.setVisibility(View.VISIBLE);

                    Constants.bookMarkView( ((TextTypeViewHolder) holder).bookmark_img, ((TextTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                    Constants.relatedStoryView(((TextTypeViewHolder) holder).related_stories,object.getNum_children());

                     try{
                    Glide.with(context).load(PataHaiUrls.PUBLISHERURL + object.getLogo()).into(((TextTypeViewHolder) holder).publisher_icon);
                    Glide.with(context).load(object.getDiscoverer_origin().trim()).into(((TextTypeViewHolder) holder).source_logo);
                     }catch (Exception e){}

                    ((TextTypeViewHolder) holder).category_TV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.CardInterest(mFirebaseAnalytics,object);

                            pauseVideo();

                            Constants.catList.addAll(list);
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.CAT_ID, object.getCat_id());
                            bundle.putString(Constants.CAT_NAME, object.getCategory());
                            bundle.putString(Constants.CAT_NAME_SHOW, object.getCategory());
                            bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                            bundle.putString(Constants.CAT_FOLLOW_STATUS, "1");
                            Fragment frg = new CategoryFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                           /* FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                            CategoryDialogFragment addfallow = CategoryDialogFragment.newInstance(object.getCat_id(),object.getCategory(),object.getCategory());
                            addfallow.show(fm, "fragment_edit_name");*/

                        }
                    });

                    ((TextTypeViewHolder) holder).publisher_Name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pauseVideo();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.CAT_ID, object.getCat_id());
                            bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                            bundle.putString(Constants.CAT_NAME, object.getSource());
                            bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                            bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                            Fragment frg = new PublisherNameFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                        }
                    });

                    ((TextTypeViewHolder) holder).publisher_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pauseVideo();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.CAT_ID, object.getCat_id());
                            bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                            bundle.putString(Constants.CAT_NAME, object.getSource());
                            bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                            bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                            Fragment frg = new PublisherNameFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                        }
                    });

                    ((TextTypeViewHolder) holder).ReadMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Constants.IMAGE = ((BitmapDrawable) ((TextTypeViewHolder) holder).image.getDrawable()).getBitmap();
                            Constants.PUB_LOGO = ((BitmapDrawable) ((TextTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                            if (((TextTypeViewHolder) holder).image.getVisibility() == View.GONE)
                                Constants.IMAGE = null;

                            Intent intent = new Intent(context, WebViewAct_Advance.class);
                            intent.putExtra(Constants.WEBLINK, object.getSourcelink());
                            intent.putExtra(Constants.WEBPAGEID, object.getId());
                            intent.putExtra(Constants.Search_Data, object);
                            context.startActivity(intent);

                        }
                    });


                    ((TextTypeViewHolder) holder).bottomOptions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomView(pos);

                        }


                    });
                    ((TextTypeViewHolder) holder).sharePost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.shareEvent(mFirebaseAnalytics,object);

                            Bitmap bitmapLogo = ((BitmapDrawable) ((TextTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                            shareCard_Text(context, Constants.WEBVIEWURL + object.getId(), ((TextTypeViewHolder) holder).category_TV.getText().toString(), ((TextTypeViewHolder) holder).heading.getText().toString(), ((TextTypeViewHolder) holder).content.getText().toString(),
                                    ((TextTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo,((BitmapDrawable) ((TextTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(),"",object);

                            // Constants.takeScreenshot((Activity) context,Constants.WEBVIEWURL+object.getId());
                        }
                    });
                    ((TextTypeViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // ((TextTypeViewHolder) holder).image.setClickable(false);
                            // new FinestWebView.Builder(context).show(object.getSourcelink());
                            ((TextTypeViewHolder) holder).ReadMore.performClick();
                        }
                    });
                    ((TextTypeViewHolder) holder).related_stories.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, RelatedStories.class);
                            i.putExtra(Constants.Search_Data, object);
                            context.startActivity(i);
                        }
                    });

                    Constants.bookMarkView(((TextTypeViewHolder) holder).bookmark_img, ((TextTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                    Constants.relatedStoryView(((TextTypeViewHolder) holder).related_stories,object.getNum_children());
                    ((TextTypeViewHolder)holder).agegroup.setText(object.getAgebar());
                    ((TextTypeViewHolder) holder).bookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String state;
                            String message;
                            if(object.getBookmarked().trim().equalsIgnoreCase("0")) {
                                state = "1";
                                message = context.getString(R.string.bookmark_message);
                            }
                            else {
                                state = "0";
                                message = context.getString(R.string.bookmark_message_un);
                            }
                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();


                            Constants.bookMarkView(((TextTypeViewHolder) holder).bookmark_img, ((TextTypeViewHolder) holder).bookmark_txt,state);


                            object.setBookmarked(state);
                            NewsRequest obj = new NewsRequest();
                            obj.setPostid(String.valueOf(object.getId()));
                            obj.setDeviceid(Constants.DeviceID);
                            obj.setStatus(state);
                            hitbookmark(obj);
                            // notifyDataSetChanged();
                        }
                    });
                    ((TextTypeViewHolder) holder).Whatsapp_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.shareEvent(mFirebaseAnalytics,object);

                            Bitmap bitmapLogo = ((BitmapDrawable) ((TextTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                            shareCard_Text(context, Constants.WEBVIEWURL + object.getId(), ((TextTypeViewHolder) holder).category_TV.getText().toString(), ((TextTypeViewHolder) holder).heading.getText().toString(), ((TextTypeViewHolder) holder).content.getText().toString(),
                                    ((TextTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo,((BitmapDrawable) ((TextTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(),Constants.WhatsApp,object);

                        }
                    });
                    ((TextTypeViewHolder)holder).newsText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mainCategoryClick(object);
                        }
                    });

                    break;


          /*  }
            switch (object.getFlashcard()) {*/


                /*******    Instagram************/
                case MultiViewRequest.INSTAGRAM_TYPE:

                    if (object.getFact().trim() == null || object.getFact().trim().equalsIgnoreCase("Null") || object.getFact().equalsIgnoreCase("NULL"))
                        ((InstaTypeViewHolder) holder).content.setVisibility(View.GONE);
                    if (object.getHeadline() == null || object.getHeadline().equalsIgnoreCase("Null"))
                        ((InstaTypeViewHolder) holder).heading.setVisibility(View.GONE);

                    //  if(object.getAvatar().isEmpty()||object.getAvatar().equalsIgnoreCase(""))
                    //  ((ImageTypeViewHolder) holder).image.setVisibility(View.GONE);
                    //  else
                    //  ((ImageTypeViewHolder) holder).image.setVisibility(View.VISIBLE);

                    Constants.MainCategory(context,((InstaTypeViewHolder) holder).newsText,((InstaTypeViewHolder) holder).publish_time,((InstaTypeViewHolder) holder).view,object.getCat_id());
                    ((InstaTypeViewHolder) holder).insta_web.getSettings().setJavaScriptEnabled(true);
                    ((InstaTypeViewHolder) holder).insta_web.loadUrl("file:///android_asset/instagram.html?" + "src="+object.getVideoid());

                    ((InstaTypeViewHolder) holder).heading.setText(Html.fromHtml(object.getHeadline().trim()));
                    ((InstaTypeViewHolder) holder).content.setText(Html.fromHtml(object.getFact().trim().replaceAll("<p style=\"text-align: justify;\"><strong>", "")));

                    ((InstaTypeViewHolder) holder).category_TV.setText("#" + Html.fromHtml(object.getCategory().trim()));
                    ((InstaTypeViewHolder) holder).publisher_Name.setText(Constants.setDot(object.getSource().trim()));
                    ((InstaTypeViewHolder) holder).publish_time.setText(Constants.getAgo(object.getEntryDate().trim()));
                    // ((InstaTypeViewHolder) holder).feed_from_source.setText("via " + object.getDiscoverer_origin().trim());

                    ((InstaTypeViewHolder) holder).publish_time.setVisibility(View.VISIBLE);
                    ((InstaTypeViewHolder) holder).view.setVisibility(View.VISIBLE);

                    Constants.bookMarkView( ((InstaTypeViewHolder) holder).bookmark_img, ((InstaTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                    Constants.relatedStoryView(((InstaTypeViewHolder) holder).related_stories,object.getNum_children());


                    try{
                    Glide.with(context).load(PataHaiUrls.PUBLISHERURL + object.getLogo()).into(((InstaTypeViewHolder) holder).publisher_icon);
                    Glide.with(context).load(object.getDiscoverer_origin().trim()).into(((InstaTypeViewHolder) holder).source_logo);
                    RequestOptions requestOptions1 = new RequestOptions();

                    requestOptions1.placeholder(R.drawable.placeholder_img);
                    requestOptions1.error(R.drawable.placeholder_img);
                    //requestOptions1.fitCenter();


                      Glide.with(context).setDefaultRequestOptions(requestOptions1).load(object.getAvatar()).into(((InstaTypeViewHolder) holder).image);
                  }catch (Exception e){
                      e.printStackTrace();
                  }
                    readMoreOption.addReadMoreTo(((InstaTypeViewHolder) holder).heading,object.getHeadline().trim());
                 //  readMore(((InstaTypeViewHolder) holder).heading.getText().toString(),((InstaTypeViewHolder) holder).heading);
                // makeTextViewResizable  (context,((InstaTypeViewHolder) holder).heading, 3, context.getResources().getString(R.string.read_more), true);

                 ((InstaTypeViewHolder) holder).category_TV.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                    Constants.CardInterest(mFirebaseAnalytics,object);

                     Constants.catList.addAll(list);
                     Bundle bundle = new Bundle();
                     bundle.putString(Constants.CAT_ID, object.getCat_id());
                     bundle.putString(Constants.CAT_NAME, object.getCategory());
                     bundle.putString(Constants.CAT_NAME_SHOW, object.getCategory());
                     bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                     bundle.putString(Constants.CAT_FOLLOW_STATUS, "1");
                     Fragment frg = new CategoryFragment();
                     frg.setArguments(bundle);
                     final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                     fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                     /*FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                     CategoryDialogFragment addfallow = CategoryDialogFragment.newInstance(object.getCat_id(),object.getCategory(),object.getCategory());
                     addfallow.show(fm, "fragment_edit_name");*/

                        }
                    });

                ((InstaTypeViewHolder) holder).publisher_Name.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                  pauseVideo();
                  Bundle bundle = new Bundle();
                  bundle.putString(Constants.CAT_ID, object.getCat_id());
                  bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                  bundle.putString(Constants.CAT_NAME, object.getSource());
                  bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                  bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                  Fragment frg = new PublisherNameFragment();
                  frg.setArguments(bundle);
                  final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                  fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                        }
                    });

                  ((InstaTypeViewHolder) holder).publisher_icon.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                    pauseVideo();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.CAT_ID, object.getCat_id());
                    bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                    bundle.putString(Constants.CAT_NAME, object.getSource());
                    bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                    bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                    Fragment frg = new PublisherNameFragment();
                    frg.setArguments(bundle);
                    final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                        }
                    });

                    if(!object.getVideoid().isEmpty()||!object.getVideoid().equalsIgnoreCase(""))
                        ((InstaTypeViewHolder) holder).btnPlay.setVisibility(View.VISIBLE);
                    else
                        ((InstaTypeViewHolder) holder).btnPlay.setVisibility(View.GONE);

                    ((InstaTypeViewHolder) holder).ReadMore.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                 Constants.IMAGE    = ((BitmapDrawable) ((InstaTypeViewHolder) holder).image.getDrawable()).getBitmap();
                 Constants.PUB_LOGO = ((BitmapDrawable) ((InstaTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                 if (((InstaTypeViewHolder) holder).image.getVisibility() == View.GONE)
                  Constants.IMAGE = null;
                if(((InstaTypeViewHolder) holder).btnPlay.getVisibility()==View.VISIBLE)
                     {
                 Constants.insta_View(context, object.getSourcelink(),object.getId(),object,Constants.IMAGE, Constants.PUB_LOGO);
                     }
                     else{
                 Constants.InstaView(context,object);
                     }

                        }
                    });
                ((InstaTypeViewHolder) holder).bottomOptions.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                  bottomView(pos);

                   }
                });
               ((InstaTypeViewHolder) holder).sharePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Constants.shareEvent(mFirebaseAnalytics,object);

                if(!object.getVideoid().trim().isEmpty()){
                Bitmap bitmapLogo = ((BitmapDrawable) ((InstaTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                shareCard_YouTube(context, Constants.WEBVIEWURL + object.getId(), ((InstaTypeViewHolder) holder).category_TV.getText().toString(), ((InstaTypeViewHolder) holder).heading.getText().toString(), ((InstaTypeViewHolder) holder).content.getText().toString(),
                ((InstaTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo,((BitmapDrawable) ((InstaTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(),"",object);

                }
               else {
                Bitmap bitmapLogo = ((BitmapDrawable) ((InstaTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                Bitmap bitmapImage = ((BitmapDrawable) ((InstaTypeViewHolder) holder).image.getDrawable()).getBitmap();

                shareCard_Insta(context, Constants.WEBVIEWURL + object.getId(), ((InstaTypeViewHolder) holder).category_TV.getText().toString(), ((InstaTypeViewHolder) holder).heading.getText().toString(), ((InstaTypeViewHolder) holder).content.getText().toString(),
                ((InstaTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapImage, bitmapLogo, ((BitmapDrawable) ((InstaTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(), "", object);
                    }

                     /*  Bitmap bitmapLogo = ((BitmapDrawable) ((InstaTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                    shareCard_Text(context, Constants.WEBVIEWURL + object.getId(), ((InstaTypeViewHolder) holder).category_TV.getText().toString(), ((InstaTypeViewHolder) holder).heading.getText().toString(), ((InstaTypeViewHolder) holder).content.getText().toString(),
                    ((InstaTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo,((BitmapDrawable) ((InstaTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(),"",object);
                    */
                    // Constants.takeScreenshot((Activity) context,Constants.WEBVIEWURL+object.getId());
                        }
                    });
                    ((InstaTypeViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    //((InstaTypeViewHolder) holder).image.setClickable(false);
                    //new FinestWebView.Builder(context).show(object.getSourcelink());
                     ((InstaTypeViewHolder) holder).ReadMore.performClick();
                        }
                    });
                 ((InstaTypeViewHolder) holder).related_stories.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                   Intent i = new Intent(context, RelatedStories.class);
                   i.putExtra(Constants.Search_Data, object);
                   context.startActivity(i);
                        }
                    });

                    Constants.bookMarkView(((InstaTypeViewHolder) holder).bookmark_img, ((InstaTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                    Constants.relatedStoryView(((InstaTypeViewHolder) holder).related_stories,object.getNum_children());
                    ((InstaTypeViewHolder)holder).agegroup.setText(object.getAgebar());

                    ((InstaTypeViewHolder) holder).bookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String state;
                            String message;
                            if(object.getBookmarked().trim().equalsIgnoreCase("0")) {
                                state = "1";
                                message = context.getString(R.string.bookmark_message);
                            }
                            else {
                                state = "0";
                                message = context.getString(R.string.bookmark_message_un);
                            }
                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();


                            Constants.bookMarkView(((InstaTypeViewHolder) holder).bookmark_img, ((InstaTypeViewHolder) holder).bookmark_txt,state);


                            object.setBookmarked(state);
                            NewsRequest obj = new NewsRequest();
                            obj.setPostid(String.valueOf(object.getId()));
                            obj.setDeviceid(Constants.DeviceID);
                            obj.setStatus(state);
                            hitbookmark(obj);
                            // notifyDataSetChanged();
                        }
                    });

                    ((InstaTypeViewHolder) holder).Whatsapp_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.shareEvent(mFirebaseAnalytics,object);

                         if(!object.getVideoid().trim().isEmpty()){
                         Bitmap bitmapLogo = ((BitmapDrawable) ((InstaTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                         shareCard_YouTube(context, Constants.WEBVIEWURL + object.getId(), ((InstaTypeViewHolder) holder).category_TV.getText().toString(), ((InstaTypeViewHolder) holder).heading.getText().toString(), ((InstaTypeViewHolder) holder).content.getText().toString(),
                         ((InstaTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo,((BitmapDrawable) ((InstaTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(),Constants.WhatsApp,object);

                            }
                            else {

                       Bitmap bitmapLogo = ((BitmapDrawable) ((InstaTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                       Bitmap bitmapImage = ((BitmapDrawable) ((InstaTypeViewHolder) holder).image.getDrawable()).getBitmap();

                      shareCard_Insta(context, Constants.WEBVIEWURL + object.getId(), ((InstaTypeViewHolder) holder).category_TV.getText().toString(), ((InstaTypeViewHolder) holder).heading.getText().toString(), ((InstaTypeViewHolder) holder).content.getText().toString(),
                      ((InstaTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapImage, bitmapLogo, ((BitmapDrawable) ((InstaTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(), Constants.WhatsApp, object);

                            }
                   /* Bitmap bitmapLogo = ((BitmapDrawable) ((InstaTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                    shareCard_Text(context, Constants.WEBVIEWURL + object.getId(), ((InstaTypeViewHolder) holder).category_TV.getText().toString(), ((InstaTypeViewHolder) holder).heading.getText().toString(), ((InstaTypeViewHolder) holder).content.getText().toString(),
                    ((InstaTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo,((BitmapDrawable) ((InstaTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(),Constants.WhatsApp,object);
                     */
                        }
                    });
                    ((InstaTypeViewHolder)holder).newsText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mainCategoryClick(object);
                        }
                    });

                    ((InstaTypeViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(((InstaTypeViewHolder) holder).btnPlay.getVisibility()==View.VISIBLE)
                            {
                                Constants.insta_View(context, object.getSourcelink(),object.getId(),object,Constants.IMAGE, Constants.PUB_LOGO);
                            }
                            else{
                                Constants.InstaView(context,object);
                            }
                        }
                    });

                    break;

           /* }
            switch (object.getFlashcard()) {*/
                /********************** Facebook*********************/


                case MultiViewRequest.FACEBOOK_TYPE:

                    Constants.MainCategory(context,((FacebookTypeViewHolder) holder).newsText,((FacebookTypeViewHolder) holder).publish_time,((FacebookTypeViewHolder) holder).view,object.getCat_id());
                    if (object.getFact().trim() == null || object.getFact().trim().equalsIgnoreCase("Null") || object.getFact().equalsIgnoreCase("NULL"))
                        ((FacebookTypeViewHolder) holder).content.setVisibility(View.GONE);
                    if (object.getHeadline() == null || object.getHeadline().equalsIgnoreCase("Null"))
                        ((FacebookTypeViewHolder) holder).heading.setVisibility(View.GONE);

                    ((FacebookTypeViewHolder) holder).facebook_web.getSettings().setJavaScriptEnabled(true);

                    if(!object.getVideoid().isEmpty()||!object.getVideoid().equalsIgnoreCase("")) {
                        ((FacebookTypeViewHolder) holder).image.setVisibility(View.GONE);
                        ((FacebookTypeViewHolder) holder).facebook_web.setVisibility(View.VISIBLE);

                        ((FacebookTypeViewHolder) holder).facebook_web.loadUrl("https://patahai.com/discoverer/fbtest.php?fbid="+object.getVideoid().trim());

                        // ((FacebookTypeViewHolder) holder).facebook_web.loadUrl("file:///android_asset/facebook.html?" + "src="+object.getVideoid());
                    }
                    else {
                        ((FacebookTypeViewHolder) holder).facebook_web.setVisibility(View.GONE);
                        ((FacebookTypeViewHolder) holder).image.setVisibility(View.VISIBLE);
                        Glide.with(context).load(object.getAvatar().trim()).into(((FacebookTypeViewHolder) holder).image);
                    }

                    ((FacebookTypeViewHolder) holder).heading.setText(Html.fromHtml(object.getHeadline().trim()));
                    ((FacebookTypeViewHolder) holder).content.setText(Html.fromHtml(object.getFact().trim()));

                    ((FacebookTypeViewHolder) holder).category_TV.setText("#" + Html.fromHtml(object.getCategory().trim()));
                    ((FacebookTypeViewHolder) holder).publisher_Name.setText(Constants.setDot(object.getSource().trim()));
                    ((FacebookTypeViewHolder) holder).publish_time.setText(Constants.getAgo(object.getEntryDate().trim()));
                    // ((TextTypeViewHolder) holder).feed_from_source.setText("via " + object.getDiscoverer_origin().trim());

                    ((FacebookTypeViewHolder) holder).publish_time.setVisibility(View.VISIBLE);
                    ((FacebookTypeViewHolder) holder).view.setVisibility(View.VISIBLE);

                    Constants.bookMarkView( ((FacebookTypeViewHolder) holder).bookmark_img, ((FacebookTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                    Constants.relatedStoryView(((FacebookTypeViewHolder) holder).related_stories,object.getNum_children());

                    try {

                        readMoreOption.addReadMoreTo(((FacebookTypeViewHolder) holder).heading,object.getHeadline().trim());
                        Glide.with(context).load(PataHaiUrls.PUBLISHERURL + object.getLogo()).into(((FacebookTypeViewHolder) holder).publisher_icon);
                        Glide.with(context).load(object.getDiscoverer_origin().trim()).into(((FacebookTypeViewHolder) holder).source_logo);


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    ((FacebookTypeViewHolder) holder).category_TV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.CardInterest(mFirebaseAnalytics,object);

                            Constants.catList.addAll(list);
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.CAT_ID, object.getCat_id());
                            bundle.putString(Constants.CAT_NAME, object.getCategory());
                            bundle.putString(Constants.CAT_NAME_SHOW, object.getCategory());
                            bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                            bundle.putString(Constants.CAT_FOLLOW_STATUS, "1");
                            Fragment frg = new CategoryFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                           /* FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                            CategoryDialogFragment addfallow = CategoryDialogFragment.newInstance(object.getCat_id(),object.getCategory(),object.getCategory());
                            addfallow.show(fm, "fragment_edit_name");*/

                        }
                    });

                    ((FacebookTypeViewHolder) holder).publisher_Name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pauseVideo();

                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.CAT_ID, object.getCat_id());
                            bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                            bundle.putString(Constants.CAT_NAME, object.getSource());
                            bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                            bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                            Fragment frg = new PublisherNameFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                        }
                    });

                    ((FacebookTypeViewHolder) holder).publisher_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pauseVideo();

                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.CAT_ID, object.getCat_id());
                            bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                            bundle.putString(Constants.CAT_NAME, object.getSource());
                            bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                            bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                            Fragment frg = new PublisherNameFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                        }
                    });

                    ((FacebookTypeViewHolder) holder).ReadMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.IMAGE = ((BitmapDrawable) ((FacebookTypeViewHolder) holder).image.getDrawable()).getBitmap();
                            Constants.PUB_LOGO = ((BitmapDrawable) ((FacebookTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                            if (((FacebookTypeViewHolder) holder).image.getVisibility() == View.GONE) {
                                Constants.IMAGE = null;
                                Constants.facebook_View(context, object.getSourcelink(), object.getId(), object, Constants.IMAGE, Constants.PUB_LOGO);
                            }else{
                                Constants.InstaView(context,object);
                            }

                        }
                    });


                    ((FacebookTypeViewHolder) holder).bottomOptions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomView(pos);

                        }


                    });

                    ((FacebookTypeViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    // ((TextTypeViewHolder) holder).image.setClickable(false);
                    // new FinestWebView.Builder(context).show(object.getSourcelink());
                    ((FacebookTypeViewHolder) holder).ReadMore.performClick();
                        }
                    });

                  ((FacebookTypeViewHolder) holder).related_stories.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                  Intent i = new Intent(context, RelatedStories.class);
                         i.putExtra(Constants.Search_Data, object);
                         context.startActivity(i);
                        }
                    });

                 Constants.bookMarkView(((FacebookTypeViewHolder) holder).bookmark_img, ((FacebookTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                 Constants.relatedStoryView(((FacebookTypeViewHolder) holder).related_stories,object.getNum_children());
                 ((FacebookTypeViewHolder)holder).agegroup.setText(object.getAgebar());
                 ((FacebookTypeViewHolder) holder).bookmark.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                 String state;
                 String message;
                 if(object.getBookmarked().trim().equalsIgnoreCase("0")) {
                   state = "1";
                   message = context.getString(R.string.bookmark_message);
                    }
                 else {
                  state = "0";
                  message = context.getString(R.string.bookmark_message_un);
                            }
                 Toast.makeText(context,message,Toast.LENGTH_SHORT).show();


                 Constants.bookMarkView(((FacebookTypeViewHolder) holder).bookmark_img, ((FacebookTypeViewHolder) holder).bookmark_txt,state);


                 object.setBookmarked(state);
                 NewsRequest obj = new NewsRequest();
                 obj.setPostid(String.valueOf(object.getId()));
                 obj.setDeviceid(Constants.DeviceID);
                 obj.setStatus(state);
                 hitbookmark(obj);
                 //notifyDataSetChanged();
                        }
                    });




                    readMoreOption.addReadMoreTo(((FacebookTypeViewHolder) holder).heading,object.getHeadline().trim());
                    ((FacebookTypeViewHolder) holder).sharePost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.shareEvent(mFirebaseAnalytics,object);

                            Bitmap bitmapLogo = ((BitmapDrawable) ((FacebookTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                            Bitmap sourceLogo = ((BitmapDrawable) ((FacebookTypeViewHolder) holder).source_logo.getDrawable()).getBitmap();

                            if( ((FacebookTypeViewHolder) holder).image.getVisibility()==View.GONE){

                                shareCard_YouTube(context, Constants.WEBVIEWURL + object.getId(), ((FacebookTypeViewHolder) holder).category_TV.getText().toString(), ((FacebookTypeViewHolder) holder).heading.getText().toString(), ((FacebookTypeViewHolder) holder).content.getText().toString(),
                                        ((FacebookTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo,((BitmapDrawable) ((FacebookTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(),"",object);

                            }
                            else{
                                Bitmap bitmapImage = ((BitmapDrawable) ((FacebookTypeViewHolder) holder).image.getDrawable()).getBitmap();
                                shareCard(context, Constants.WEBVIEWURL + object.getId(), ((FacebookTypeViewHolder) holder).category_TV.getText().toString(), ((FacebookTypeViewHolder) holder).heading.getText().toString(), ((FacebookTypeViewHolder) holder).content.getText().toString(),
                                        ((FacebookTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapImage, bitmapLogo, sourceLogo, "", object);
                            }
                          //Constants.takeScreenshot((Activity) context,Constants.WEBVIEWURL+object.getId());
                        }
                    });

                ((FacebookTypeViewHolder) holder).Whatsapp_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Constants.shareEvent(mFirebaseAnalytics,object);

                Bitmap bitmapLogo = ((BitmapDrawable) ((FacebookTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                Bitmap sourceLogo = ((BitmapDrawable) ((FacebookTypeViewHolder) holder).source_logo.getDrawable()).getBitmap();

                if( ((FacebookTypeViewHolder) holder).image.getVisibility()==View.GONE)
                     {

               shareCard_YouTube(context, Constants.WEBVIEWURL + object.getId(), ((FacebookTypeViewHolder) holder).category_TV.getText().toString(), ((FacebookTypeViewHolder) holder).heading.getText().toString(), ((FacebookTypeViewHolder) holder).content.getText().toString(),
               ((FacebookTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo,((BitmapDrawable) ((FacebookTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(),Constants.WhatsApp,object);

                     }
                else {
               Bitmap bitmapImage = ((BitmapDrawable) ((FacebookTypeViewHolder) holder).image.getDrawable()).getBitmap();
               shareCard(context, Constants.WEBVIEWURL + object.getId(), ((FacebookTypeViewHolder) holder).category_TV.getText().toString(), ((FacebookTypeViewHolder) holder).heading.getText().toString(), ((FacebookTypeViewHolder) holder).content.getText().toString(),
               ((FacebookTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapImage, bitmapLogo, sourceLogo, Constants.WhatsApp, object);

                            }

                        }
                    });




              ((FacebookTypeViewHolder)holder).newsText.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
               mainCategoryClick(object);
                        }
                    });
                    break;
          /*  }

            switch (object.getFlashcard()) {*/
                /********************* Twitter*********************/

                case MultiViewRequest.TWITTER_TYPE:

                    Constants.MainCategory(context,((TwitterTypeViewHolder) holder).newsText,((TwitterTypeViewHolder) holder).publish_time,((TwitterTypeViewHolder) holder).view,object.getCat_id());
                    if (object.getFact().trim() == null || object.getFact().trim().equalsIgnoreCase("Null") || object.getFact().equalsIgnoreCase("NULL"))
                        ((TwitterTypeViewHolder) holder).content.setVisibility(View.GONE);
                    if (object.getHeadline() == null || object.getHeadline().equalsIgnoreCase("Null"))
                        ((TwitterTypeViewHolder) holder).heading.setVisibility(View.GONE);

                    long tweeId= Long.valueOf(object.getVideoid());
                    Constants.loadTweet(context, ((TwitterTypeViewHolder) holder).Re_View, tweeId);

                    /*  if(((TwitterTypeViewHolder) holder).Re_View.getChildCount()==0) {

                      }*/



                    ((TwitterTypeViewHolder) holder).heading.setText(Html.fromHtml(object.getHeadline().trim().replaceAll("&lt;p style=\\\"text-align: justify;", "")));
                    ((TwitterTypeViewHolder) holder).content.setText(Html.fromHtml(object.getFact().trim().replaceAll("<p style=\"text-align: justify;\"><strong>", "")));

                    ((TwitterTypeViewHolder) holder).category_TV.setText("#" + Html.fromHtml(object.getCategory().trim()));
                    ((TwitterTypeViewHolder) holder).publisher_Name.setText(Constants.setDot(object.getSource().trim()));
                    ((TwitterTypeViewHolder) holder).publish_time.setText(Constants.getAgo(object.getEntryDate().trim()));
                    // ((TextTypeViewHolder) holder).feed_from_source.setText("via " + object.getDiscoverer_origin().trim());
                    ((TwitterTypeViewHolder) holder).heading.setVisibility(View.GONE);

                    ((TwitterTypeViewHolder) holder).publish_time.setVisibility(View.VISIBLE);
                    ((TwitterTypeViewHolder) holder).view.setVisibility(View.VISIBLE);

                  /*  if(object.getCat_id().trim().equalsIgnoreCase("1"))
                    {
                        ((TwitterTypeViewHolder) holder).publish_time.setVisibility(View.VISIBLE);
                        ((TwitterTypeViewHolder) holder).view.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        ((TwitterTypeViewHolder) holder).publish_time.setVisibility(View.GONE);
                        ((TwitterTypeViewHolder) holder).view.setVisibility(View.GONE);
                    }*/

                    Constants.bookMarkView( ((TwitterTypeViewHolder) holder).bookmark_img, ((TwitterTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                    Constants.relatedStoryView(((TwitterTypeViewHolder) holder).related_stories,object.getNum_children());

                    try {
                        Glide.with(context).load(object.getLogo()).into(((TwitterTypeViewHolder) holder).publisher_icon);
                        Glide.with(context).load(object.getDiscoverer_origin().trim()).into(((TwitterTypeViewHolder) holder).source_logo);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(!object.getAvatar().trim().isEmpty()) {
                     Glide.with(context).load(object.getAvatar()).into(((TwitterTypeViewHolder) holder).image);
                      }
                      else {
                   ((TwitterTypeViewHolder) holder).image.setVisibility(View.GONE);
                    }

                ((TwitterTypeViewHolder) holder).category_TV.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                      Constants.CardInterest(mFirebaseAnalytics,object);

                   Constants.catList.addAll(list);
                   Bundle bundle = new Bundle();
                   bundle.putString(Constants.CAT_ID, object.getCat_id());
                   bundle.putString(Constants.CAT_NAME, object.getCategory());
                   bundle.putString(Constants.CAT_NAME_SHOW, object.getCategory());
                      bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                   bundle.putString(Constants.CAT_FOLLOW_STATUS, "1");
                   Fragment frg = new CategoryFragment();
                   frg.setArguments(bundle);
                   final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                   fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                   /*FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                    CategoryDialogFragment addfallow = CategoryDialogFragment.newInstance(object.getCat_id(),object.getCategory(),object.getCategory());
                    addfallow.show(fm, "fragment_edit_name");*/

                        }
                    });

                    ((TwitterTypeViewHolder) holder).publisher_Name.setOnClickListener(new View.OnClickListener() {
                    @Override
                   public void onClick(View view) {
                   pauseVideo();
                   Bundle bundle = new Bundle();
                   bundle.putString(Constants.CAT_ID, object.getCat_id());
                   bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                   bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                   bundle.putString(Constants.CAT_NAME, object.getSource());
                            bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                            Fragment frg = new PublisherNameFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                        }
                    });

                    ((TwitterTypeViewHolder) holder).publisher_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pauseVideo();

                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.CAT_ID, object.getCat_id());
                            bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                            bundle.putString(Constants.CAT_NAME, object.getSource());
                            bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                            bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                            Fragment frg = new PublisherNameFragment();
                            frg.setArguments(bundle);
                            final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                        }
                    });

                    ((TwitterTypeViewHolder) holder).ReadMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Constants.IMAGE = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).image.getDrawable()).getBitmap();
                            Constants.PUB_LOGO = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                            if (((TwitterTypeViewHolder) holder).image.getVisibility() == View.GONE)
                                Constants.IMAGE = null;

                            Intent intent = new Intent(context, WebViewAct_Advance.class);
                            intent.putExtra(Constants.WEBLINK, object.getSourcelink());
                            intent.putExtra(Constants.WEBPAGEID, object.getId());
                            intent.putExtra(Constants.Search_Data, object);
                            context.startActivity(intent);

                        }
                    });


                    ((TwitterTypeViewHolder) holder).bottomOptions.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomView(pos);

                        }


                    });
                    ((TwitterTypeViewHolder) holder).sharePost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.shareEvent(mFirebaseAnalytics,object);

                            // Bitmap bitmapImage = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).image.getDrawable()).getBitmap();
                            Bitmap bitmapLogo = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();


                         if(!object.getAvatar().trim().isEmpty()){
                         Bitmap bitmapImage = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).image.getDrawable()).getBitmap();
                         Bitmap sourceLogo = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).source_logo.getDrawable()).getBitmap();
                         shareCard(context, Constants.WEBVIEWURL + object.getId(), ((TwitterTypeViewHolder) holder).category_TV.getText().toString(), ((TwitterTypeViewHolder) holder).heading.getText().toString(), ((TwitterTypeViewHolder) holder).content.getText().toString(),
                         ((TwitterTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapImage, bitmapLogo, sourceLogo, "", object);


                         }else {
                     shareCard_Text(context, Constants.WEBVIEWURL + object.getId(), ((TwitterTypeViewHolder) holder).category_TV.getText().toString(), ((TwitterTypeViewHolder) holder).heading.getText().toString(), ((TwitterTypeViewHolder) holder).content.getText().toString(),
                     ((TwitterTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo, ((BitmapDrawable) ((TwitterTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(), "", object);
                            }
                    //Constants.takeScreenshot((Activity) context,Constants.WEBVIEWURL+object.getId());
                        }
                    });
                    ((TwitterTypeViewHolder) holder).image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    //((TextTypeViewHolder) holder).image.setClickable(false);
                    //new FinestWebView.Builder(context).show(object.getSourcelink());

                    ((TwitterTypeViewHolder) holder).ReadMore.performClick();
                        }
                    });

                    ((TwitterTypeViewHolder) holder).Re_View.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.PUB_LOGO = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                            if (((TwitterTypeViewHolder) holder).image.getVisibility() == View.GONE)
                                Constants.IMAGE = null;
                            else
                                Constants.IMAGE     = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).image.getDrawable()).getBitmap();
                            Constants.twitter_WebView(context, object.getSourcelink(),object.getId(),object,Constants.IMAGE, Constants.PUB_LOGO);

                        }
                    });


                    ((TwitterTypeViewHolder) holder).related_stories.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, RelatedStories.class);
                            i.putExtra(Constants.Search_Data, object);
                            context.startActivity(i);
                        }
                    });

                    Constants.bookMarkView(((TwitterTypeViewHolder) holder).bookmark_img, ((TwitterTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                    Constants.relatedStoryView(((TwitterTypeViewHolder) holder).related_stories,object.getNum_children());
                    ((TwitterTypeViewHolder)holder).agegroup.setText(object.getAgebar());

                    ((TwitterTypeViewHolder) holder).bookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String state;
                            String message;
                            if(object.getBookmarked().trim().equalsIgnoreCase("0")) {
                                state = "1";
                                message = context.getString(R.string.bookmark_message);
                            }
                            else {
                                state = "0";
                                message = context.getString(R.string.bookmark_message_un);
                            }
                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();


                            Constants.bookMarkView(((TwitterTypeViewHolder) holder).bookmark_img, ((TwitterTypeViewHolder) holder).bookmark_txt,state);


                            object.setBookmarked(state);
                            NewsRequest obj = new NewsRequest();
                            obj.setPostid(String.valueOf(object.getId()));
                            obj.setDeviceid(Constants.DeviceID);
                            obj.setStatus(state);
                            hitbookmark(obj);
                            // notifyDataSetChanged();
                        }
                    });

                    ((TwitterTypeViewHolder) holder).Whatsapp_share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Constants.shareEvent(mFirebaseAnalytics,object);

                     Bitmap bitmapLogo = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                     if(!object.getAvatar().trim().isEmpty()){
                        Bitmap bitmapImage = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).image.getDrawable()).getBitmap();
                        Bitmap sourceLogo = ((BitmapDrawable) ((TwitterTypeViewHolder) holder).source_logo.getDrawable()).getBitmap();



                        shareCard(context, Constants.WEBVIEWURL + object.getId(), ((TwitterTypeViewHolder) holder).category_TV.getText().toString(), ((TwitterTypeViewHolder) holder).heading.getText().toString(), ((TwitterTypeViewHolder) holder).content.getText().toString(),
                        ((TwitterTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapImage, bitmapLogo, sourceLogo, Constants.WhatsApp, object);


                            }else {
                                shareCard_Text(context, Constants.WEBVIEWURL + object.getId(), ((TwitterTypeViewHolder) holder).category_TV.getText().toString(), ((TwitterTypeViewHolder) holder).heading.getText().toString(), ((TwitterTypeViewHolder) holder).content.getText().toString(),
                                        ((TwitterTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo, ((BitmapDrawable) ((TwitterTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(), Constants.WhatsApp, object);
                            }
                        }
                    });

                    ((TwitterTypeViewHolder)holder).newsText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mainCategoryClick(object);
                        }
                    });

                    break;


                /* }
                 *//*************   Video and Youtube Case*********//*
            switch (object.getFlashcard()) {*/
                case MultiViewRequest.VIDEO_PLAYER_VIEW_TYPE:
                    if (!object.getVideoid().trim().isEmpty()) {
                        Constants.MainCategory(context,((VideoTypeViewHolder) holder).newsText,((VideoTypeViewHolder) holder).publish_time,((VideoTypeViewHolder) holder).view,object.getCat_id());
                        if (object.getVideoid().trim().equalsIgnoreCase(null) || object.getVideoid().trim().isEmpty()) {
                            ((VideoTypeViewHolder) holder).mainVideo.setVisibility(View.GONE);
                        } else {
                            ((VideoTypeViewHolder) holder).mainVideo.setVisibility(View.VISIBLE);
                        }

                        if (object.getFact() == null || object.getFact().equalsIgnoreCase("Null") || object.getFact().equalsIgnoreCase("NULL"))
                            ((VideoTypeViewHolder) holder).content.setVisibility(View.GONE);

                        if (object.getHeadline() == null || object.getHeadline().equalsIgnoreCase("Null"))
                            ((VideoTypeViewHolder) holder).heading.setVisibility(View.GONE);



                        ((VideoTypeViewHolder) holder).heading.setText(Html.fromHtml(object.getHeadline().trim()));
                        ((VideoTypeViewHolder) holder).content.setText(Html.fromHtml(object.getFact().trim()));
                        ((VideoTypeViewHolder) holder).category_TV.setText("#" + Html.fromHtml(object.getCategory().trim()));
                        ((VideoTypeViewHolder) holder).publisher_Name.setText(Constants.setDot(object.getSource().trim()));
                        //((VideoTypeViewHolder) holder).feed_from_source.setText("via " + object.getDiscoverer_origin().trim());



                      ((VideoTypeViewHolder) holder).publish_time.setText(Constants.getAgo(object.getEntryDate().trim()));
                      ((VideoTypeViewHolder) holder).publish_time.setVisibility(View.VISIBLE);
                      ((VideoTypeViewHolder) holder).view.setVisibility(View.VISIBLE);

                        Constants.bookMarkView( ((VideoTypeViewHolder) holder).bookmark_img, ((VideoTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                        Constants.relatedStoryView(((VideoTypeViewHolder) holder).related_stories,object.getNum_children());

                        try {
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.placeholder_img);
                        requestOptions.error(R.drawable.placeholder_img);

                        Glide.with(context).setDefaultRequestOptions(requestOptions)
                        .load("https://i.ytimg.com/vi/" + object.getVideoid().trim() + Constants.YouTube_Image).
                        into(((VideoTypeViewHolder) holder).imageViewItem);

                        Glide.with(context).load(object.getLogo()).into(((VideoTypeViewHolder) holder).publisher_icon);
                        Glide.with(context).load(object.getDiscoverer_origin()).into(((VideoTypeViewHolder) holder).source_logo);
                            }
                        catch (Exception e){
                         e.printStackTrace();
                        }




                        if (!object.getVideoid().isEmpty() || object.getVideoid() != null && object.getDiscoverer_origin().contains("youtube.png")) {




                            ((VideoTypeViewHolder) holder).imageViewItem.setVisibility(View.VISIBLE);
                            ((VideoTypeViewHolder) holder).btnPlay.setVisibility(View.VISIBLE);
                            ((VideoTypeViewHolder) holder).youtube_view.setVisibility(View.GONE);

                            ((VideoTypeViewHolder) holder).btnPlay.setOnClickListener(view -> {
                                ((VideoTypeViewHolder) holder).imageViewItem.setVisibility(View.GONE);
                                ((VideoTypeViewHolder) holder).youtube_view.setVisibility(View.VISIBLE);
                                ((VideoTypeViewHolder) holder).btnPlay.setVisibility(View.GONE);

                                vId = object.getVideoid().trim();
                                videoUrl(vId, ((VideoTypeViewHolder) holder).youtube_view);

                               /* ((VideoTypeViewHolder) holder).youtube_view.initialize(initializedYouTubePlayer -> initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                                    @Override
                                    public void onReady() {
                                        initializedYouTubePlayer.loadVideo(object.getVideoid().trim(), 0);
                                        youTubePlayer = initializedYouTubePlayer;
                                        Constants.youTubePlayer = initializedYouTubePlayer;
                                    }
                                }), true);
                                ((VideoTypeViewHolder) holder).youtube_view.getPlayerUIController().setCustomFullScreenButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                         Uri.parse("http://www.youtube.com/watch?v=" + object.getVideoid().trim()));
                                        intent.putExtra("force_fullscreen", true);

                                        context.startActivity(intent);
                                    }
                                });
                                */

                            });
                        } else {
                            //((VideoTypeViewHolder) holder).Re_View.setVisibility(View.GONE);
                            ((VideoTypeViewHolder) holder).imageViewItem.setVisibility(View.VISIBLE);
                            ((VideoTypeViewHolder) holder).heading.setVisibility(View.VISIBLE);
                            //Constants.ViewManagement(context,((EntMultiViewAdapter.VideoTypeViewHolder) holder).imageViewItem,((EntMultiViewAdapter.VideoTypeViewHolder) holder).Re_View,((EntMultiViewAdapter.VideoTypeViewHolder) holder).heading,object.getDiscoverer_origin());
                        }

                        ((VideoTypeViewHolder) holder).heading.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            /* if(youTubePlayer==null)
                                ((VideoTypeViewHolder) holder).btnPlay.performClick();
                                else
                                youTubePlayer.play();*/

                            }
                        });

                        ((VideoTypeViewHolder) holder).category_TV.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Constants.CardInterest(mFirebaseAnalytics,object);

                                pauseVideo();
                                Constants.catList.addAll(list);
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.CAT_ID, object.getCat_id());
                                bundle.putString(Constants.CAT_NAME, object.getCategory());
                                bundle.putString(Constants.CAT_NAME_SHOW, object.getCategory());
                                bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                                bundle.putString(Constants.CAT_FOLLOW_STATUS, "1");
                                Fragment frg = new CategoryFragment();
                                frg.setArguments(bundle);
                                final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();


                            }
                        });

                        ((VideoTypeViewHolder) holder).publisher_Name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pauseVideo();
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.CAT_ID, object.getCat_id());
                                bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                                bundle.putString(Constants.CAT_NAME, object.getSource());
                                bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                                bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                                Fragment frg = new PublisherNameFragment();
                                frg.setArguments(bundle);
                                final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                            }
                        });

                        ((VideoTypeViewHolder) holder).publisher_icon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                pauseVideo();
                                // Constants.catList.addAll(list);
                                Bundle bundle = new Bundle();
                                bundle.putString(Constants.CAT_ID, object.getCat_id());
                                bundle.putString(Constants.SOURCE_ID, object.getSourceid());
                                bundle.putString(Constants.CAT_NAME, object.getSource());
                                bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
                                bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
                                Fragment frg = new PublisherNameFragment();
                                frg.setArguments(bundle);
                                final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();

                            }
                        });
                        ((VideoTypeViewHolder) holder).ReadMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Constants.PUB_LOGO = ((BitmapDrawable)((VideoTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                                if(((VideoTypeViewHolder) holder).image.getVisibility()==View.GONE)
                                    Constants.IMAGE = null;
                                else
                                    Constants.IMAGE = ((BitmapDrawable)((VideoTypeViewHolder) holder).image.getDrawable()).getBitmap();

                                Constants.twitter_youTube(context, object.getSourcelink(),object.getId(),object,Constants.IMAGE, Constants.PUB_LOGO);


                            }
                        });

                        ((VideoTypeViewHolder) holder).sharePost.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Constants.shareEvent(mFirebaseAnalytics,object);

                                Bitmap bitmapLogo = ((BitmapDrawable) ((VideoTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                                shareCard_YouTube(context, Constants.WEBVIEWURL + object.getId(), ((VideoTypeViewHolder) holder).category_TV.getText().toString(), ((VideoTypeViewHolder) holder).heading.getText().toString(), ((VideoTypeViewHolder) holder).content.getText().toString(),
                                        ((VideoTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo,((BitmapDrawable) ((VideoTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(),"",object);

                            }
                        });
                        ((VideoTypeViewHolder) holder).bottomOptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomView(pos);

                            }


                        });
                    }

                    ((VideoTypeViewHolder) holder).related_stories.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(context, RelatedStories.class);
                            i.putExtra(Constants.Search_Data, object);
                            context.startActivity(i);
                        }
                    });

                    Constants.bookMarkView(((VideoTypeViewHolder) holder).bookmark_img, ((VideoTypeViewHolder) holder).bookmark_txt,object.getBookmarked().toString().trim());
                    Constants.relatedStoryView(((VideoTypeViewHolder) holder).related_stories,object.getNum_children());
                    ((VideoTypeViewHolder)holder).agegroup.setText(object.getAgebar());

                    ((VideoTypeViewHolder) holder).bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                     String state;
                     String message;
                     if(object.getBookmarked().trim().equalsIgnoreCase("0")) {
                                state = "1";
                                message = context.getString(R.string.bookmark_message);
                            }
                            else {
                                state = "0";
                                message = context.getString(R.string.bookmark_message_un);
                            }
                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();


                            Constants.bookMarkView(((VideoTypeViewHolder) holder).bookmark_img, ((VideoTypeViewHolder) holder).bookmark_txt,state);


                            object.setBookmarked(state);
                            NewsRequest obj = new NewsRequest();
                            obj.setPostid(String.valueOf(object.getId()));
                            obj.setDeviceid(Constants.DeviceID);
                            obj.setStatus(state);
                            hitbookmark(obj);
                            // notifyDataSetChanged();
                        }
                    });

                    ((VideoTypeViewHolder) holder).Whatsapp_share.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Constants.shareEvent(mFirebaseAnalytics,object);

                            Bitmap bitmapLogo = ((BitmapDrawable) ((VideoTypeViewHolder) holder).publisher_icon.getDrawable()).getBitmap();
                            shareCard_YouTube(context, Constants.WEBVIEWURL + object.getId(), ((VideoTypeViewHolder) holder).category_TV.getText().toString(), ((VideoTypeViewHolder) holder).heading.getText().toString(), ((VideoTypeViewHolder) holder).content.getText().toString(),
                                    ((VideoTypeViewHolder) holder).publisher_Name.getText().toString(), bitmapLogo,((BitmapDrawable) ((VideoTypeViewHolder) holder).source_logo.getDrawable()).getBitmap(),Constants.WhatsApp,object);

                        }
                    });

                 ((VideoTypeViewHolder)holder).newsText.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                 mainCategoryClick(object);
                        }
                });

                    break;

            }

        }
    }




    @Override
    public int getItemCount() {

    return list.size();
      }
    //protected abstract void clear();

    private void bottomView(final int pos) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View bottomViuew = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, null);
        final MultiViewRequest obj = list.get(pos);
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(bottomViuew);
        dialog.show();


        LinearLayout copyLink = (LinearLayout) bottomViuew.findViewById(R.id.copyLink);
        LinearLayout unfollow = (LinearLayout) bottomViuew.findViewById(R.id.unfollow);
        TextView unfollowTitle = (TextView) bottomViuew.findViewById(R.id.unfollowTitle);

        unfollowTitle.setText(context.getResources().getString(R.string.unfollow) + " #" + Constants.CapsLetter(obj.getCategory()));
        unfollow.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        doubleButton(obj.getCategory(), context, pos, dialog);
        }
        });
        copyLink.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
         ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
         ClipData clip = ClipData.newPlainText("Text", Constants.WEBVIEWURL + obj.getId());
         clipboard.setPrimaryClip(clip);
         Toast.makeText(context, context.getResources().getString(R.string.link_copy), Toast.LENGTH_SHORT).show();
         dialog.dismiss();
            }
        });
    }

    private final class TextTypeViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, publisher_icon, bottomOptions,source_logo;
        //RelativeLayout sharePostLay;
        private LinearLayout sharePost,related_stories,Whatsapp_share,bookmark;
        private ImageView bookmark_img;
        private TextView bookmark_txt,agegroup,newsText;

        private LinearLayout ReadMore;
        private View view;
        private TextView heading, content, category_TV, publisher_Name, publish_time, comment_count, likes_count;


        public TextTypeViewHolder(View itemView) {
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.flash_card_IV);
            this.publisher_icon = (ImageView) itemView.findViewById(R.id.publisher_icon);
            this.category_TV    = (TextView) itemView.findViewById(R.id.hashName);
            this.heading        = (TextView) itemView.findViewById(R.id.heading);
            this.content        = (TextView) itemView.findViewById(R.id.content);
            this.publisher_Name = (TextView) itemView.findViewById(R.id.publisher_Name);
            this.publish_time   = (TextView) itemView.findViewById(R.id.publish_time);
            this.comment_count  = (TextView) itemView.findViewById(R.id.comment_count);
            this.likes_count    = (TextView) itemView.findViewById(R.id.likes_count);
            this.ReadMore       = (LinearLayout) itemView.findViewById(R.id.ReadMore);
            this.bottomOptions  = (ImageView) itemView.findViewById(R.id.bottomOptions);
            this.sharePost      = (LinearLayout) itemView.findViewById(R.id.shareLay);

            this.source_logo    = (ImageView) itemView.findViewById(R.id.source_logo);

            this.bookmark        = (LinearLayout) itemView.findViewById(R.id.bookmark);
            this.bookmark_img    = (ImageView) itemView.findViewById(R.id.bookmark_img);
            this.bookmark_txt    = (TextView) itemView.findViewById(R.id.bookmark_txt_new);
            this.related_stories = (LinearLayout) itemView.findViewById(R.id.related_stories);
            this.Whatsapp_share  = (LinearLayout) itemView.findViewById(R.id.Whatsapp_share);
            this.agegroup        = (TextView) itemView.findViewById(R.id.agegroup);
            this.newsText        = (TextView) itemView.findViewById(R.id.newsText);
            this.view            = (View)itemView.findViewById(R.id.view);
        }
    }

    private  class InstaTypeViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, publisher_icon, bottomOptions,source_logo,btnPlay;
        //RelativeLayout sharePostLay;
        private LinearLayout sharePost,related_stories,Whatsapp_share,bookmark;
        private ImageView bookmark_img;
        private TextView bookmark_txt,agegroup,newsText;


        private LinearLayout ReadMore;
        private View view;
        private TextView heading, content, category_TV, publisher_Name, publish_time, comment_count, likes_count;
        private WebView insta_web;


        public InstaTypeViewHolder(View itemView) {
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.flash_card_IV);
            this.publisher_icon = (ImageView) itemView.findViewById(R.id.publisher_icon);
            this.category_TV = (TextView) itemView.findViewById(R.id.hashName);
            this.heading = (TextView) itemView.findViewById(R.id.heading);
            this.content = (TextView) itemView.findViewById(R.id.content);
            this.publisher_Name = (TextView) itemView.findViewById(R.id.publisher_Name);
            this.publish_time = (TextView) itemView.findViewById(R.id.publish_time);
            this.comment_count = (TextView) itemView.findViewById(R.id.comment_count);
            this.likes_count = (TextView) itemView.findViewById(R.id.likes_count);
            this.ReadMore = (LinearLayout) itemView.findViewById(R.id.ReadMore);
            this.bottomOptions = (ImageView) itemView.findViewById(R.id.bottomOptions);
            this.sharePost = (LinearLayout) itemView.findViewById(R.id.shareLay);

            this.source_logo = (ImageView) itemView.findViewById(R.id.source_logo);
            this.insta_web = (WebView) itemView.findViewById(R.id.insta_web);
            this.btnPlay = (ImageView) itemView.findViewById(R.id.btnPlay);

            this.bookmark        = (LinearLayout) itemView.findViewById(R.id.bookmark);
            this.bookmark_img    = (ImageView) itemView.findViewById(R.id.bookmark_img);
            this.bookmark_txt    = (TextView) itemView.findViewById(R.id.bookmark_txt_new);
            this.related_stories = (LinearLayout) itemView.findViewById(R.id.related_stories);
            this.Whatsapp_share  = (LinearLayout) itemView.findViewById(R.id.Whatsapp_share);
            this.agegroup        = (TextView) itemView.findViewById(R.id.agegroup);
            this.newsText        = (TextView) itemView.findViewById(R.id.newsText);
            this.view            = (View)itemView.findViewById(R.id.view);
        }
    }

    private  class FacebookTypeViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, publisher_icon, bottomOptions,source_logo;
        //RelativeLayout sharePostLay;
        private LinearLayout sharePost,related_stories,Whatsapp_share,bookmark;
        private ImageView bookmark_img;
        private TextView bookmark_txt,agegroup,newsText;

        private LinearLayout ReadMore;
        private TextView heading, content, category_TV, publisher_Name, publish_time, comment_count, likes_count;
        private WebView facebook_web;
        private View view;


        public FacebookTypeViewHolder(View itemView) {
            super(itemView);

            this.image          = (ImageView) itemView.findViewById(R.id.flash_card_IV);
            this.publisher_icon = (ImageView) itemView.findViewById(R.id.publisher_icon);
            this.category_TV    = (TextView) itemView.findViewById(R.id.hashName);
            this.heading        = (TextView) itemView.findViewById(R.id.heading);
            this.content        = (TextView) itemView.findViewById(R.id.content);
            this.publisher_Name = (TextView) itemView.findViewById(R.id.publisher_Name);
            this.publish_time   = (TextView) itemView.findViewById(R.id.publish_time);
            this.comment_count  = (TextView) itemView.findViewById(R.id.comment_count);
            this.likes_count    = (TextView) itemView.findViewById(R.id.likes_count);
            this.ReadMore       = (LinearLayout) itemView.findViewById(R.id.ReadMore);
            this.bottomOptions  = (ImageView) itemView.findViewById(R.id.bottomOptions);
            this.sharePost      = (LinearLayout) itemView.findViewById(R.id.shareLay);

            this.source_logo    = (ImageView) itemView.findViewById(R.id.source_logo);
            this.facebook_web   = (WebView) itemView.findViewById(R.id.facebook_web);

            this.bookmark        = (LinearLayout) itemView.findViewById(R.id.bookmark);
            this.bookmark_img    = (ImageView) itemView.findViewById(R.id.bookmark_img);
            this.bookmark_txt    = (TextView) itemView.findViewById(R.id.bookmark_txt_new);
            this.related_stories = (LinearLayout) itemView.findViewById(R.id.related_stories);
            this.Whatsapp_share  = (LinearLayout) itemView.findViewById(R.id.Whatsapp_share);
            this.agegroup        = (TextView) itemView.findViewById(R.id.agegroup);
            this.newsText        = (TextView) itemView.findViewById(R.id.newsText);
            this.view            = (View)itemView.findViewById(R.id.view);
        }
    }

    private  class TwitterTypeViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, publisher_icon, bottomOptions,source_logo;
        //RelativeLayout sharePostLay;
        private LinearLayout sharePost,related_stories,Whatsapp_share,bookmark;
        private ImageView bookmark_img;
        private TextView bookmark_txt,agegroup,newsText;


        private LinearLayout ReadMore,Re_View;
        private TextView heading,content, category_TV, publisher_Name, publish_time;
        private View view;


        public TwitterTypeViewHolder(View itemView) {
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.flash_card_IV);
            this.publisher_icon = (ImageView) itemView.findViewById(R.id.publisher_icon);
            this.category_TV = (TextView) itemView.findViewById(R.id.hashName);
            this.heading = (TextView) itemView.findViewById(R.id.heading);
            this.content = (TextView) itemView.findViewById(R.id.content);
            this.publisher_Name = (TextView) itemView.findViewById(R.id.publisher_Name);
            this.publish_time = (TextView) itemView.findViewById(R.id.publish_time);
            this.ReadMore = (LinearLayout) itemView.findViewById(R.id.ReadMore);
            this.bottomOptions = (ImageView) itemView.findViewById(R.id.bottomOptions);
            this.sharePost = (LinearLayout) itemView.findViewById(R.id.shareLay);
            this.Re_View = (LinearLayout) itemView.findViewById(R.id.Re_View);

            this.source_logo = (ImageView) itemView.findViewById(R.id.source_logo);
            this.view = (View) itemView.findViewById(R.id.view);

            this.bookmark        = (LinearLayout) itemView.findViewById(R.id.bookmark);
            this.bookmark_img    = (ImageView) itemView.findViewById(R.id.bookmark_img);
            this.bookmark_txt    = (TextView) itemView.findViewById(R.id.bookmark_txt_new);
            this.related_stories = (LinearLayout) itemView.findViewById(R.id.related_stories);
            this.Whatsapp_share  = (LinearLayout) itemView.findViewById(R.id.Whatsapp_share);
            this.agegroup        = (TextView) itemView.findViewById(R.id.agegroup);
            this.newsText        = (TextView) itemView.findViewById(R.id.newsText);
        }
    }

    private  class TextTypeViewHolderBlank extends RecyclerView.ViewHolder {
        public TextTypeViewHolderBlank(View itemView) {
            super(itemView);
        }
    }


    private  class ImageTypeViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, publisher_icon, bottomOptions, btnPlay,source_logo,bookmark_img;
        //RelativeLayout sharePostLay;
        private LinearLayout sharePost,related_stories,Whatsapp_share;
        private TextView bookmark_txt,agegroup,newsText;
        private LinearLayout ReadMore;
        private FrameLayout mainFrame;
        private TextView heading, content, category_TV, publisher_Name, publish_time, comment_count, likes_count;
        private WebView webview;
        private View view;

        LinearLayout Re_View,bookmark;


        public ImageTypeViewHolder(View itemView) {
            super(itemView);

            this.image = (ImageView) itemView.findViewById(R.id.flash_card_IV);
            this.publisher_icon = (ImageView) itemView.findViewById(R.id.publisher_icon);
            this.category_TV = (TextView) itemView.findViewById(R.id.hashName);
            this.heading = (TextView) itemView.findViewById(R.id.heading);
            this.content = (TextView) itemView.findViewById(R.id.content);
            this.publisher_Name = (TextView) itemView.findViewById(R.id.publisher_Name);
            this.publish_time = (TextView) itemView.findViewById(R.id.publish_time);

            this.source_logo = (ImageView) itemView.findViewById(R.id.source_logo);

            this.comment_count = (TextView) itemView.findViewById(R.id.comment_count);
            this.likes_count   = (TextView) itemView.findViewById(R.id.likes_count);
            this.ReadMore      = (LinearLayout) itemView.findViewById(R.id.ReadMore);
            this.bottomOptions = (ImageView) itemView.findViewById(R.id.bottomOptions);
            this.sharePost     = (LinearLayout) itemView.findViewById(R.id.shareLay);
            this.webview       = (WebView) itemView.findViewById(R.id.webview);
            this.mainFrame     = itemView.findViewById(R.id.mainFrame);

            this.btnPlay   = (ImageView) itemView.findViewById(R.id.btnPlay);
            this.Re_View   = (LinearLayout) itemView.findViewById(R.id.Re_View);
            this.view   = (View) itemView.findViewById(R.id.view);

            this.bookmark        = (LinearLayout) itemView.findViewById(R.id.bookmark);
            this.bookmark_img    = (ImageView) itemView.findViewById(R.id.bookmark_img);
            this.bookmark_txt    = (TextView) itemView.findViewById(R.id.bookmark_txt_new);
            this.agegroup        = (TextView) itemView.findViewById(R.id.agegroup);
            this.newsText        = (TextView) itemView.findViewById(R.id.newsText);
            this.related_stories = (LinearLayout) itemView.findViewById(R.id.related_stories);
            this.Whatsapp_share  = (LinearLayout) itemView.findViewById(R.id.Whatsapp_share);

        }
    }

    public  class VideoTypeViewHolder extends RecyclerView.ViewHolder {
        FrameLayout mainVideo;
        TextView heading, content, category_TV, publisher_Name, publish_time, dateTime_TV;
        RelativeLayout load_cat_layout;
        // YouTubePlayerView youTubePlayerView;
        ImageView image, publisher_icon, bottomOptions, btnPlay, imageViewItem,source_logo;
        private YouTubePlayerView youtube_view;
        private ToggleButton toggle_bookmark, toggle_dislike, toggle_like;
        private LinearLayout sharePost,related_stories,Whatsapp_share,bookmark;
        private ImageView bookmark_img;
        private TextView bookmark_txt,agegroup,newsText;
        LinearLayout ReadMore;
        private View view;

        public VideoTypeViewHolder(View itemView) {
            super(itemView);


            this.btnPlay           = (ImageView) itemView.findViewById(R.id.btnPlay);
            this.imageViewItem     = (ImageView) itemView.findViewById(R.id.imageViewItem);
            this.youtube_view      = (YouTubePlayerView) itemView.findViewById(R.id.youtube_view);

            this.image             = (ImageView) itemView.findViewById(R.id.flash_card_IV);
            this.publisher_icon    = (ImageView) itemView.findViewById(R.id.publisher_icon);
            this.category_TV       = (TextView) itemView.findViewById(R.id.hashName);
            this.heading           = (TextView) itemView.findViewById(R.id.heading);
            this.content           = (TextView) itemView.findViewById(R.id.content);
            this.publisher_Name    = (TextView) itemView.findViewById(R.id.publisher_Name);

            this.source_logo       = (ImageView) itemView.findViewById(R.id.source_logo);

            this.publish_time  = (TextView) itemView.findViewById(R.id.publish_time);
            this.ReadMore      = (LinearLayout) itemView.findViewById(R.id.ReadMore);
            this.bottomOptions = (ImageView) itemView.findViewById(R.id.bottomOptions);
            this.sharePost     = (LinearLayout) itemView.findViewById(R.id.shareLay);
            this.mainVideo     = (FrameLayout) itemView.findViewById(R.id.mainVideo);

            this.view          = (View) itemView.findViewById(R.id.view);
            this.youtube_view.getPlayerUIController().showFullscreenButton(true);
            this.youtube_view.getPlayerUIController().showYouTubeButton(false);

            this.bookmark        = (LinearLayout) itemView.findViewById(R.id.bookmark);
            this.bookmark_img    = (ImageView) itemView.findViewById(R.id.bookmark_img);
            this.bookmark_txt    = (TextView) itemView.findViewById(R.id.bookmark_txt_new);
            this.related_stories = (LinearLayout) itemView.findViewById(R.id.related_stories);
            this.Whatsapp_share  = (LinearLayout) itemView.findViewById(R.id.Whatsapp_share);
            this.agegroup        = (TextView) itemView.findViewById(R.id.agegroup);
            this.newsText        = (TextView) itemView.findViewById(R.id.newsText);
        }
    }


    private void doubleButton(String msg, final Context context, final int pos, final BottomSheetDialog buttondialog) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alertdialogueunfollow);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialog.setTitle("Title...");

        final MultiViewRequest obj = list.get(pos);

        TextView unfollow = (TextView) dialog.findViewById(R.id.unfollow);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView msz = (TextView) dialog.findViewById(R.id.message);
        title.setText(context.getResources().getString(R.string.unfollow_hash) + "" + Constants.CapsLetter(msg));
        msg = context.getResources().getString(R.string.unfollow_msz) + " #" + Constants.CapsLetter(msg) + " " + context.getResources().getString(R.string.in_your_feed);
        msz.setText(msg);
        // if button is clicked, close the custom dialog
        unfollow.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        if (youTubePlayer != null)
        youTubePlayer.pause();


        if (FollowingFrag.FOLLOWLIST != null && FollowingFrag.FOLLOWLIST.size() > 0)
        FollowingFrag.FOLLOWLIST.remove(list.get(pos));


       FollowInterestRequestNew loginRequest = new FollowInterestRequestNew();
       loginRequest.setCat_id(obj.getCat_id());
       loginRequest.setCategory(obj.getCategory());
       loginRequest.setKeyword(obj.getKeyword());
       loginRequest.setDeviceid(Constants.DeviceID);
       loginRequest.setLanguage(Constants.Language);

       hitunFollowApi(loginRequest);

       //list.remove(2);


       for (int i = 0; i < list.size(); i++) {
         if (list.get(i).getCategory().trim()
         .equalsIgnoreCase(obj.getCategory().trim())) {
         list.remove(i);
             }
            }

       notifyDataSetChanged();

       dialog.dismiss();
       buttondialog.dismiss();

            }
        });
      cancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      dialog.dismiss();
      buttondialog.dismiss();
            }
        });
        dialog.show();
    }

    private Call<FollowInterestRequestNew> call;

    private void hitunFollowApi(FollowInterestRequestNew loginRequest) {
        if (call != null) {
            call.cancel();
        }
        loginRequest.setUserid(Prefs.getString(Constants.USERID,""));
        call = APIsClient.getInstance().getApiService().followcategory(loginRequest);
        call.enqueue(new Callback<FollowInterestRequestNew>() {
            @Override
            public void onResponse(Call<FollowInterestRequestNew> call, Response<FollowInterestRequestNew> response) {
                try {
                    if (response.code() == 200) {
                        recall.RecallApi(true);
                        reLoadFollowers.reLoadFollower(true);
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FollowInterestRequestNew> call, Throwable t) {

            }
        });
    }

    private void hitbookmark(NewsRequest loginRequest) {
        if (call != null) {
            call.cancel();
        }
        loginRequest.setUserid(Prefs.getString(Constants.USERID,""));
        call = APIsClient.getInstance().getApiService().bookmark(loginRequest);
        call.enqueue(new Callback<FollowInterestRequestNew>() {
            @Override
            public void onResponse(Call<FollowInterestRequestNew> call, Response<FollowInterestRequestNew> response) {
                try {
                    if (response.code() == 200) {
                   // recall.RecallApi(true);
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<FollowInterestRequestNew> call, Throwable t) {

            }
        });
    }



    private void shareCard_Insta(final Context context, String cardLink, String hash, String head, String cont, String pub_name,
                                 Bitmap mainImage, Bitmap logo,Bitmap source,String From,MultiViewRequest object) {
        View v = LayoutInflater.from(context).inflate(R.layout.share_image_image, null, false);

        TextView hashName        = (TextView) v.findViewById(R.id.hashName);
        TextView heading         = (TextView) v.findViewById(R.id.heading);
        TextView content         = (TextView) v.findViewById(R.id.content);
        TextView publisher_Name  = (TextView) v.findViewById(R.id.publisher_Name);

        ImageView flash_card_IV  = (ImageView) v.findViewById(R.id.flash_card_IV);
        ImageView publisher_icon = (ImageView) v.findViewById(R.id.publisher_icon);
        ImageView source_logo    = (ImageView) v.findViewById(R.id.source_logo);


        TextView newsText     = (TextView) v.findViewById(R.id.newsText);
        TextView publish_time = (TextView) v.findViewById(R.id.publish_time);
        View view = (View) v.findViewById(R.id.view);
        publish_time.setText(Constants.getAgo(object.getEntryDate().trim()));
        Constants.MainCategory(context,newsText,publish_time,view,object.getCat_id());
        content.setVisibility(View.GONE);

        if(mainImage==null){
            flash_card_IV.setVisibility(View.GONE);
        }
        else {
        //Glide.with(context).load(object.getAvatar().trim()).into(flash_card_IV);
        flash_card_IV.setImageBitmap(mainImage);
         }
        publisher_icon.setImageBitmap(logo);

        source_logo.setImageBitmap(source);


        hashName.setText(Html.fromHtml(hash));
        heading.setText(Html.fromHtml(head));
        content.setText(Html.fromHtml(cont));
        publisher_Name.setText(Constants.setDot(pub_name));

        boolean b = permission.checkPermission(true);
        if (b) {
        Constants.takeScreenshotold((Activity) context, Constants.viewToBitmap(v, context), cardLink, head, From,object);
        }

        }

    private void shareCard(final Context context, String cardLink, String hash, String head, String cont, String pub_name, Bitmap mainImage, Bitmap logo,Bitmap sourceLogo,String From,MultiViewRequest object) {

        View v = LayoutInflater.from(context).inflate(R.layout.share_card_image, null, false);


        TextView hashName = (TextView) v.findViewById(R.id.hashName);
        TextView heading  = (TextView) v.findViewById(R.id.heading);
        TextView content  = (TextView) v.findViewById(R.id.content);
        TextView publisher_Name = (TextView) v.findViewById(R.id.publisher_Name);

        ImageView flash_card_IV  = (ImageView) v.findViewById(R.id.flash_card_IV);
        ImageView publisher_icon = (ImageView) v.findViewById(R.id.publisher_icon);
        ImageView source_logo    = (ImageView) v.findViewById(R.id.source_logo);

        TextView newsText     = (TextView) v.findViewById(R.id.newsText);
        TextView publish_time = (TextView) v.findViewById(R.id.publish_time);
        View view = (View) v.findViewById(R.id.view);
        publish_time.setText(Constants.getAgo(object.getPublish_dateon().trim()));
        Constants.MainCategory(context,newsText,publish_time,view,object.getCat_id());

        if (cont.isEmpty())
            content.setVisibility(View.GONE);

        if (mainImage == null) {
            flash_card_IV.setVisibility(View.GONE);
        } else {
            flash_card_IV.setImageBitmap(mainImage);
        }
        source_logo.setImageBitmap(sourceLogo);
        publisher_icon.setImageBitmap(logo);

        hashName.setText(Html.fromHtml(hash));
        heading.setText(Html.fromHtml(head));
        content.setText(Html.fromHtml(cont));
        publisher_Name.setText(Constants.setDot(pub_name));
        publish_time.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);

        boolean b = permission.checkPermission(true);
        if (b) {

           // Constants.takeScreenshotold((Activity) context, Constants.viewToBitmap(v, context), cardLink, head, From,object);
            Constants.takeScreenshotold((Activity) context, Constants.viewToBitmap(v, context), object.getSourcelink(), head, From,object);
        }
    }

     private void shareCard_Text(final Context context, String cardLink, String hash, String head, String cont, String pub_name, Bitmap logo,Bitmap source,String From,MultiViewRequest object) {
        View v = LayoutInflater.from(context).inflate(R.layout.share_card_text, null, false);

        TextView hashName       = (TextView) v.findViewById(R.id.hashName);
        TextView heading        = (TextView) v.findViewById(R.id.heading);
        TextView content        = (TextView) v.findViewById(R.id.content);
        TextView publisher_Name = (TextView) v.findViewById(R.id.publisher_Name);



        ImageView publisher_icon = (ImageView) v.findViewById(R.id.publisher_icon);
        ImageView source_logo    = (ImageView) v.findViewById(R.id.source_logo);

        TextView newsText        = (TextView) v.findViewById(R.id.newsText);
        TextView publish_time    = (TextView) v.findViewById(R.id.publish_time);
        View view                = (View) v.findViewById(R.id.view);
        publish_time.setText(Constants.getAgo(object.getEntryDate().trim()));
        Constants.MainCategory(context,newsText,publish_time,view,object.getCat_id());


        publisher_icon.setImageBitmap(logo);
        source_logo.setImageBitmap(source);
        hashName.setText(Html.fromHtml(hash));
        heading.setText(Html.fromHtml(head));
        content.setText(Html.fromHtml(cont));
        publisher_Name.setText(Constants.setDot(pub_name));
         publish_time.setVisibility(View.VISIBLE);
         view.setVisibility(View.VISIBLE);

         boolean b = permission.checkPermission(true);
         if (b) {

            // Constants.takeScreenshotold((Activity) context, Constants.viewToBitmap(v, context), cardLink, head, From,object);
             Constants.takeScreenshotold((Activity) context, Constants.viewToBitmap(v, context), object.getSourcelink(), head, From,object);
         }

    }

    private void shareCard_YouTube(final Context context, String cardLink, String hash, String head, String cont, String pub_name, Bitmap logo,Bitmap source,String From,MultiViewRequest object) {
        View v = LayoutInflater.from(context).inflate(R.layout.share_card_text, null, false);

        TextView hashName       = (TextView) v.findViewById(R.id.hashName);
        TextView heading        = (TextView) v.findViewById(R.id.heading);
        TextView content        = (TextView) v.findViewById(R.id.content);
        TextView publisher_Name = (TextView) v.findViewById(R.id.publisher_Name);



        ImageView publisher_icon = (ImageView) v.findViewById(R.id.publisher_icon);
        ImageView source_logo    = (ImageView) v.findViewById(R.id.source_logo);

        TextView newsText        = (TextView) v.findViewById(R.id.newsText);
        TextView publish_time    = (TextView) v.findViewById(R.id.publish_time);
        View view                = (View) v.findViewById(R.id.view);
        publish_time.setText(Constants.getAgo(object.getEntryDate().trim()));
        Constants.MainCategory(context,newsText,publish_time,view,object.getCat_id());


        publisher_icon.setImageBitmap(logo);
        source_logo.setImageBitmap(source);
        hashName.setText(Html.fromHtml(hash));
        heading.setText(Html.fromHtml(head));
        content.setText(Html.fromHtml(cont));
        publisher_Name.setText(Constants.setDot(pub_name));

        boolean b = permission.checkPermission(true);
        if (b) {
       Constants.takeScreenshot_YouTube((Activity) context, Constants.viewToBitmap(v, context), cardLink, head, From, object);
        }

    }


    private Bitmap test(String img_link){
        Bitmap image =null;
        try {
            URL url = new URL(img_link);
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }
        return image;
    }








    public static String vId;
    private void videoUrl(String getYoutubeVideoUrl,YouTubePlayerView youtube_player_view) {
        vId = getYoutubeVideoUrl.trim();
        youtube_player_view.initialize(new YouTubePlayerInitListener() {
            @Override
            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                // youTubePlayer = initializedYouTubePlayer;
                Constants.youTubePlayer = initializedYouTubePlayer;
                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady() {
                        initializedYouTubePlayer.loadVideo(vId, 0);
                        youTubePlayer = initializedYouTubePlayer;
                    }
                });
            }
        }, true);
        youtube_player_view.getPlayerUIController().setCustomFullScreenButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + vId));
                intent.putExtra("force_fullscreen", true);
                context.startActivity(intent);

            }
        });
    }

   private void pauseVideo(){
       if(youTubePlayer!=null)
           youTubePlayer.pause();
       if(Constants.youTubePlayer!=null)
           Constants.youTubePlayer.pause();
   }

    private void mainCategoryClick(MultiViewRequest object) {
        pauseVideo();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.CAT_ID, object.getCat_id());
        bundle.putString(Constants.SOURCE_ID, object.getSourceid());
        bundle.putString(Constants.CAT_NAME, "");
        bundle.putString(Constants.CAT_NAME_SHOW, "");
        bundle.putString(Constants.CAT_KEYWORD, object.getKeyword());
        bundle.putString(Constants.PUBLISHER_LOGO, object.getLogo());
        Fragment frg = new None_Fragment();
        frg.setArguments(bundle);
        //final FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();

        final FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.containerHome, frg).addToBackStack("1").commit();
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }


        /********************** View More ********************/

        private void readMore(String textValue,TextView textView) {
            Log.e("TextDAta==>Before",textValue);



            // OR using options to customize

            ReadMoreOption readMoreOption = new ReadMoreOption.Builder(context)
                    .textLength(300)
                    .moreLabel("MORE")
                    .lessLabel("LESS")
                    .moreLabelColor(Color.RED)
                    .lessLabelColor(Color.BLUE)
                    .labelUnderLine(true)
                    .build();

            readMoreOption.addReadMoreTo(textView, textValue);
            Log.e("TextDAta==>After",textView.getText().toString());
        }

    public static void makeTextViewResizable(Context context,final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                try{
                    Log.e("TestVV=>",tv.getText().toString());
                    String text;
                    int lineEndIndex;
                    ViewTreeObserver obs = tv.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
                    if (maxLine == 0) {
                        Log.e("TestV1=>",tv.getText().toString());
                        lineEndIndex = tv.getLayout().getLineEnd(0);
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                        Log.e("TestV2=>",tv.getText().toString());
                        lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                        text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    } else {
                        Log.e("TestV-Else=>",tv.getText().toString());
                        lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                        text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    }
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(context,Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }catch (Exception e){
            e.printStackTrace();
                }

            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(Context context,final Spanned strSpanned, final TextView tv,
     final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);


        if (str.contains(spanableText)) {

            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                    // tv.setTextColor(context.getResources().getColor(R.color.time_color));

                    if (viewMore)
                     {
                        Log.e("Test-Spann-If=>",tv.getText().toString());

                     }
                     else
                       {
                     Log.e("Test-Spann-Else=>",tv.getText().toString());
                    makeTextViewResizable(context,tv, 3, context.getResources().getString(R.string.read_more), true);
                     }

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(R.color.time_color));
                    ds.bgColor = context.getResources().getColor(R.color.whiteColor);
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }


}
