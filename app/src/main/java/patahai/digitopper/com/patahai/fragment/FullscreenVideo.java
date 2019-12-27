package patahai.digitopper.com.patahai.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.DialogFragment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import patahai.digitopper.com.patahai.MainActivity;
import patahai.digitopper.com.patahai.R;
import patahai.digitopper.com.patahai.adapters.CategoryDialogAdapter;
import patahai.digitopper.com.patahai.adapters.PublisherClickable;
import patahai.digitopper.com.patahai.adapters.Publisher_Adapter;
import patahai.digitopper.com.patahai.constants.Constants;
import patahai.digitopper.com.patahai.interfaces.MainApiRecall;
import patahai.digitopper.com.patahai.interfaces.RecallFragments;
import patahai.digitopper.com.patahai.model.Facts_Follower;
import patahai.digitopper.com.patahai.model.Facts_Interest;
import patahai.digitopper.com.patahai.model.FollowInterestRequest;
import patahai.digitopper.com.patahai.model.MultiViewInterest;
import patahai.digitopper.com.patahai.model.MultiViewRequest;
import patahai.digitopper.com.patahai.model.NewsRequest;
import patahai.digitopper.com.patahai.model.SetInterestRequest;
import patahai.digitopper.com.patahai.webservice.APIsClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDialogFragment extends DialogFragment implements View.OnClickListener,Publisher_Adapter.AdapterInterface {

    private Dialog dialog;
    private Call<Facts_Follower> callLoadEntertainmentApi;

    boolean loading, isDataAvailable, isNewsEnable, isSubCatDataEnable;
    int pageNo = 1, loadRegex = 5;
    private Facts_Follower reviewDataResponseList;
    private ArrayList<MultiViewRequest> list;
    private CategoryDialogAdapter adapter;
    private TextView cat_Name;
    Publisher_Adapter.AdapterInterface buttonListener;
    private ImageView backPress;


    //  @BindView(R.id.simpleSwipeRefreshLayout)
    SwipeRefreshLayout simpleSwipeRefreshLayout;
    //   @BindView(R.id.mainList)
    RecyclerView recycleViewReviewItem;
    private TextView cat_follow_count;
    private MainApiRecall recall;
    private RecallFragments recallFrg;
    @BindView(R.id.following)
    TextView following;
    @BindView(R.id.followingButton)
    ImageView followingButton;
    @BindView(R.id.tapUpdate)
    TextView tapUpdate;
    @BindView(R.id.updating_layout)
    RelativeLayout updating_layout;
    @BindView(R.id.update_txt)
    TextView update_txt;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private boolean refreshStatus = false;
    private LinearLayoutManager linearLayoutManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Override
    public void onStart() {
    super.onStart();
    dialog = getDialog();
    if (dialog != null) {
    int width  = ViewGroup.LayoutParams.MATCH_PARENT;
    int height = ViewGroup.LayoutParams.MATCH_PARENT;
    dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onStop() {
    super.onStop();
       }

    @Override
    public void onAttach(Context context) {
    super.onAttach(context);

     }

    public CategoryDialogFragment()
              {
    // Empty constructor is required for DialogFragment
    // Make sure not to add arguments to the constructor
    // Use `newInstance` instead as shown below
               }
    String catName;
    String catNameShow;
    String cat_Id;
    public static CategoryDialogFragment newInstance(String cat_Id,String catName,String catNameShow) {
       CategoryDialogFragment frag = new CategoryDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.CAT_ID, cat_Id);
        args.putString(Constants.CAT_NAME, catName);
        args.putString(Constants.CAT_NAME_SHOW, catNameShow);
        frag.setArguments(args);

        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.cat_fragment, container);
        ButterKnife.bind(this, view);
        followingButton.setOnClickListener(this);
        tapUpdate.setOnClickListener(this);
        following.setVisibility(View.GONE);
        recall         = (MainApiRecall) getActivity();
        recallFrg      = (RecallFragments) getActivity();
        catName        = getArguments().getString(Constants.CAT_NAME);
        catNameShow    = getArguments().getString(Constants.CAT_NAME_SHOW);
        cat_Id         = getArguments().getString(Constants.CAT_ID);

        recycleViewReviewItem    = (RecyclerView)view.findViewById(R.id.recycleViewReviewItem);
        simpleSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.simpleSwipeRefreshLayout);
        cat_Name                 = (TextView)view.findViewById(R.id.cat_Name);
       // following              = (TextView)view.findViewById(R.id.following);
        cat_follow_count         = (TextView)view.findViewById(R.id.cat_follow_count);
        backPress                = (ImageView) view.findViewById(R.id.backPress);

        simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorAccent);
        backPress.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        dialog.dismiss();
            }
        });



        cat_Name.setText("#"+catNameShow);
        defaultMgnt();
        callPost(catName,cat_Id);
        return view;
        }


     private void test(){
            cat_Name.setText("#"+catNameShow);
            defaultMgnt();

            NewsRequest loginRequest = new NewsRequest();

            loginRequest.setDeviceid(Constants.DeviceID);
            loginRequest.setLanguage(Constants.Language);
            loginRequest.setCat_id(cat_Id);
            loginRequest.setSourceid(catName);
            loginRequest.setPage(String.valueOf(pageNo));
            callFollower(loginRequest);


                }

    private Call<Facts_Follower> callLoadFacts_Follower;
    private Facts_Follower reviewDatacallLoadFacts_Follower;
    private void callFollower(NewsRequest loginRequest ) {
        if (callLoadFacts_Follower != null) {
        callLoadFacts_Follower.cancel();
          }


        if (!isNewsEnable) {

           }
        isNewsEnable = true;
        isSubCatDataEnable = false;
        callLoadFacts_Follower = APIsClient.getInstance().getApiService().followcount(loginRequest);

        callLoadFacts_Follower.enqueue(new Callback<Facts_Follower>() {
            @Override
            public void onResponse(Call<Facts_Follower> call, Response<Facts_Follower> response) {
           isNewsEnable = true;
                try {
            if (response.code() == 200) {

            reviewDatacallLoadFacts_Follower = response.body();
            loading = false;
            if(Integer.valueOf(reviewDatacallLoadFacts_Follower.getFollower_count())==1)
            cat_follow_count.setText(""+reviewDatacallLoadFacts_Follower.getFollower_count()+" "+getResources().getString(R.string.follower));
            else
            cat_follow_count.setText(""+reviewDatacallLoadFacts_Follower.getFollower_count()+" "+getResources().getString(R.string.followers));
             } else if (response.code() == 401) {

             } else {
             // hideProgress();
             //((BaseActivity)getActivity()).showMsg("Something went worng");
                    }
                } catch (Exception e) {
             e.printStackTrace();
             // hideProgress();
             //((BaseActivity)getActivity()).showMsg("Something went worng");
                }
            }

            @Override
            public void onFailure(Call<Facts_Follower> call, Throwable t) {
                // hideProgress();
                // ((BaseActivity)getActivity()).showMsg("Something went worng");
            if (call.isCanceled()) {
             } else {
              }
            }
        });

    }

    private void defaultMgnt()
           {
        if (Constants.youTubePlayer != null)
           Constants.youTubePlayer.pause();

       list = new ArrayList<>();
       adapter = new CategoryDialogAdapter((MainActivity)getActivity(),list);
       linearLayoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
       recycleViewReviewItem.setLayoutManager(linearLayoutManager);
       recycleViewReviewItem.setItemAnimator(new DefaultItemAnimator());
       recycleViewReviewItem.setHasFixedSize(true);
       //recycleViewReviewItem.setNestedScrollingEnabled(true);
      recycleViewReviewItem.setAdapter(adapter);
      adapter.notifyDataSetChanged();
      simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
      updating_layout.setVisibility(View.VISIBLE);
      if (!Constants.isNetworkAvailable(getActivity())) {
      update_txt.setText(getString(R.string.no_internet));
       }
      else {
       update_txt.setText(getString(R.string.update_feed));
      updating_layout.setVisibility(View.VISIBLE);
       }
      new Handler().postDelayed(new Runnable() {
     @Override
    public void run() {
    refreshStatus = true;
    simpleSwipeRefreshLayout.setRefreshing(false);

    pageNo = 1;
    if(update_txt.getText().toString().trim().equalsIgnoreCase(getString(R.string.update_feed).trim()))
     updating_layout.setVisibility(View.GONE);
     callPost(catName,cat_Id);
         }
       }, 1000);
        }
       });
      simpleSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorAccent, R.color.colorAccent);
      recycleViewReviewItem.addOnScrollListener(new RecyclerView.OnScrollListener() {
                   @Override
                   public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                       super.onScrollStateChanged(recyclerView, newState);
                       if (linearLayoutManager.findFirstVisibleItemPosition() >= list.size() - 3) {
                           tapUpdate.setVisibility(View.GONE);
                           updating_layout.setVisibility(View.GONE);
                           if (Constants.isNetworkAvailable(getActivity())) {

                               // Constants.setProgress(progressBar);
                               progressBar.setVisibility(View.VISIBLE);
                               // sendData(Constants.DeviceID, Constants.Language);
                               callPost(catName,cat_Id);
                           }
                           else
                           {
                               updating_layout.setVisibility(View.VISIBLE);
                               if (!Constants.isNetworkAvailable(getActivity()))
                                   update_txt.setText(getString(R.string.no_internet));
                           }
                       }
                   }

                   @Override
                   public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                       super.onScrolled(recyclerView, dx, dy);
                       Constants.onScrolledUp(linearLayoutManager,tapUpdate,progressBar, dx, dy);
                   }
               });


           }
    private void callPost(String catName,String cat_id){
        SetInterestRequest loginRequest = new SetInterestRequest();

        loginRequest.setLanguage(Constants.Language);
        loginRequest.setDeviceid(Constants.DeviceID);
        loginRequest.setCatname(catName);
        loginRequest.setCat_id(cat_id);
        loginRequest.setUserid(Prefs.getString(Constants.USERID,""));
        loginRequest.setPage(String.valueOf(pageNo));

        callNewsDataApi(loginRequest);
    }
    private void callNewsDataApi( SetInterestRequest loginRequest ) {
        if (callLoadEntertainmentApi != null) {
            callLoadEntertainmentApi.cancel();
        }



        if (!isNewsEnable) {
            pageNo = 1;
        }
        isNewsEnable = true;
        isSubCatDataEnable = false;

        callLoadEntertainmentApi = APIsClient.getInstance().getApiService().catsearch(loginRequest);

        callLoadEntertainmentApi.enqueue(new Callback<Facts_Follower>() {
            @Override
            public void onResponse(Call<Facts_Follower> call, Response<Facts_Follower> response) {
                isNewsEnable = true;
                try {
                    progressBar.setVisibility(View.GONE);
                    updating_layout.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        reviewDataResponseList = response.body();
                        loading = false;
                        if (!list.isEmpty()&&refreshStatus) {
                            list.clear();
                            refreshStatus = false;
                        }
                        if (reviewDataResponseList.getFacts() != null && reviewDataResponseList.getFacts().size() > 0) {
                            if (reviewDataResponseList.getFacts().size() < 10) {
                                isDataAvailable = false;
                            }
                            isDataAvailable = true;
                            list.addAll(reviewDataResponseList.getFacts());
                            adapter.notifyDataSetChanged();
                            pageNo = pageNo + 1;
                        } else {
                            isDataAvailable = false;
                        }


                    } else if (response.code() == 401) {

                    } else {
                        // hideProgress();
                        //((BaseActivity)getActivity()).showMsg("Something went worng");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //  hideProgress();
                    //((BaseActivity)getActivity()).showMsg("Something went worng");
                }
            }

            @Override
            public void onFailure(Call<Facts_Follower> call, Throwable t) {
                // hideProgress();
                // ((BaseActivity)getActivity()).showMsg("Something went worng");
                if (call.isCanceled()) {
                } else {
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
//            if(getChildFragmentManager()!=null)
//                getChildFragmentManager().popBackStack();

            if (getFragmentManager() != null)
                getFragmentManager().popBackStack();
        } catch (IllegalStateException e) {
        }
    }


    Publisher_Adapter adapterpublisher;
    private void bottomView(final int pos, String keyword,String category) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View bottomViuew = inflater.inflate(R.layout.fragment_bottom_sheet_home_dialog, null);
        bottomViuew.setMinimumHeight(Constants.getDeviceHeight(getActivity()));
        final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());

        dialog.setContentView(bottomViuew);
        // dialog.show();
        // getPublisherList(keyword);
        publisherList.clear();
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(((View) bottomViuew.getParent()));
        bottomSheetBehavior.setPeekHeight(Constants.getDeviceHeight(getActivity()));
        RecyclerView recyclerView = (RecyclerView) bottomViuew.findViewById(R.id.publisherList);
        RelativeLayout follow_Cat = (RelativeLayout) bottomViuew.findViewById(R.id.follow_Cat);
        TextView catName          = (TextView) bottomViuew.findViewById(R.id.catName);
        ProgressBar mainProgress  = (ProgressBar) bottomViuew.findViewById(R.id.mainProgress);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        catName.setText("#"+category);
        adapterpublisher = new Publisher_Adapter(getActivity(), publisherList,category,keyword,this, new PublisherClickable() {
            @Override
            public void onItemClick(View v, int position) {
                dialog.dismiss();
            }
        });
        recyclerView.setAdapter(adapterpublisher);
        mainProgress.setVisibility(View.VISIBLE);
        dialog.show();
        getPublisherList(keyword,mainProgress);


        follow_Cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // MultiViewInterest obj = intrestList.get(pos);
                doubleButton(catNameShow, getActivity(), pos,dialog);
            }
        });

    }

    private void doubleButton(String msg, final Context context, final int pos,final BottomSheetDialog buttondialog) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alertdialogueunfollow);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialog.setTitle("Title...");
        //set the custom dialog components - text, image and button

        //final MultiViewInterest obj = intrestList.get(pos);
        TextView unfollow = (TextView) dialog.findViewById(R.id.unfollow);
        TextView title    = (TextView) dialog.findViewById(R.id.title);
        TextView cancel   = (TextView) dialog.findViewById(R.id.cancel);
        TextView msz      = (TextView) dialog.findViewById(R.id.message);
        title.setText(context.getResources().getString(R.string.unfollow_hash) + " " +Constants.CapsLetter(msg));
        msg  = context.getResources().getString(R.string.unfollow_msz) + " #" + Constants.CapsLetter(msg) + " " + context.getResources().getString(R.string.in_your_feed);
        msz.setText(msg);
        //if button is clicked, close the custom dialog
        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FollowInterestRequest loginRequest = new FollowInterestRequest();
                loginRequest.setCat_id(cat_Id);
                //loginRequest.setTopic_id(obj.getId());
                loginRequest.setCategory(catName);

                loginRequest.setDeviceid(Constants.DeviceID);
                loginRequest.setLanguage(Constants.Language);
                loginRequest.setUserid(Prefs.getString(Constants.USERID,""));
                //loginRequest.setKeyword();
                hitunFollowApi(loginRequest);


                dialog.dismiss();
                if(buttondialog!=null)
                    buttondialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(buttondialog!=null)
                    buttondialog.dismiss();

            }
        });
        dialog.show();
    }


    ArrayList<MultiViewInterest> publisherList = new ArrayList<>();
    private Facts_Interest publisherResponse;
    private Call<Facts_Interest> publisher;
    private void getPublisherList(String keyword,ProgressBar mainProgress) {
        if (publisher != null) {
            publisher.cancel();
        }
        SetInterestRequest setInterestRequest = new SetInterestRequest();
        setInterestRequest.setDeviceid(Constants.DeviceID);
        setInterestRequest.setLanguage(Constants.Language);
        setInterestRequest.setUserid(Prefs.getString(Constants.USERID,""));
        setInterestRequest.setCat_id("1");
        setInterestRequest.setKeyword(keyword);

        publisher = APIsClient.getInstance().getApiService().getallpublishers(setInterestRequest);
        publisher.enqueue(new Callback<Facts_Interest>() {
            @Override
            public void onResponse(Call<Facts_Interest> call, Response<Facts_Interest> response) {
                // list.addAll(response.body().)
                try {

                    mainProgress.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        publisherResponse = response.body();

                        loading = false;
                        if (publisherResponse.getFacts() != null && publisherResponse.getFacts().size() > 0) {
                            if (publisherResponse.getFacts().size() < 10) {
                                isDataAvailable = false;
                            }
                            isDataAvailable = true;
                            publisherList.addAll(publisherResponse.getFacts());


                            adapterpublisher.notifyDataSetChanged();
                        } else {
                            isDataAvailable = false;
                        }


                    } else if (response.code() == 401) {

                    } else {
                        // hideProgress();
                        //((BaseActivity)getActivity()).showMsg("Something went worng");
                    }
                }catch (Exception e){

                }


            }

            @Override
            public void onFailure(Call<Facts_Interest> call, Throwable t) {

            }
        });
    }
    private Call<FollowInterestRequest> call;
    private void hitunFollowApi(FollowInterestRequest loginRequest) {
        if (call != null) {
            call.cancel();
        }


        call = APIsClient.getInstance().getApiService().followcategory(loginRequest);
        call.enqueue(new Callback<FollowInterestRequest>() {
            @Override
            public void onResponse(Call<FollowInterestRequest> call, Response<FollowInterestRequest> response) {
            try {

           //hideProgress();
           if (response.code() == 200) {
               recall.RecallApi(true);
               recallFrg.changeFragment("3");
               dialog.dismiss();
                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFailure(Call<FollowInterestRequest> call, Throwable t) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.tapUpdate){
            tapUpdate.setVisibility(View.GONE);

            pageNo = 1;
            callPost(catName,cat_Id);
            recycleViewReviewItem.getLayoutManager().scrollToPosition(0);
            tapUpdate.setVisibility(View.GONE);
        }
     else if(view.getId()==R.id.followingButton){
          if (cat_Id.equalsIgnoreCase("1")) {
      bottomView(0, catName, catNameShow);
            } else {
      optionMenu(view, 0);
            }
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void optionMenu(View view, int pos) {

        PopupMenu popup = new PopupMenu(getActivity(), view, Gravity.NO_GRAVITY, R.attr.actionOverflowMenuStyle, 0);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.unfollow, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.unfollow:


                        doubleButton(catNameShow, getActivity(), pos,null);

                        break;

                }
                return false;
            }
        });
        popup.show();
    }
    @Override
     public void buttonPressed() {
        Log.e("CategoryReload=>","Calling1");
        pageNo =1;
        if(!list.isEmpty())
        list.clear();

        callPost(catName,cat_Id);
    }
}