package patahai.digitopper.com.patahai.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.shimmer.ShimmerFrameLayout;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import patahai.digitopper.com.patahai.MainActivity;
import patahai.digitopper.com.patahai.R;
import patahai.digitopper.com.patahai.adapters.HomeMultiViewAdapter;
import patahai.digitopper.com.patahai.adapters.Main_Following_Adapter;
import patahai.digitopper.com.patahai.constants.Constants;
import patahai.digitopper.com.patahai.interfaces.IFragmentManager;
import patahai.digitopper.com.patahai.interfaces.MainApiRecall;
import patahai.digitopper.com.patahai.interfaces.ReLoadFollowers;
import patahai.digitopper.com.patahai.interfaces.RecallFragments;
import patahai.digitopper.com.patahai.interfaces.ReloadAllFrag;
import patahai.digitopper.com.patahai.interfaces.ToggleManager;
import patahai.digitopper.com.patahai.model.Facts;
import patahai.digitopper.com.patahai.model.Facts_Interest;
import patahai.digitopper.com.patahai.model.FollowInterestRequestNew;
import patahai.digitopper.com.patahai.model.MultiViewInterest;
import patahai.digitopper.com.patahai.model.MultiViewRequest;
import patahai.digitopper.com.patahai.model.NewsRequest;
import patahai.digitopper.com.patahai.views.activity.Splash;
import patahai.digitopper.com.patahai.webservice.APIsClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements IFragmentManager,View.OnClickListener,RecallFragments, MainApiRecall, ToggleManager, ReLoadFollowers,ReloadAllFrag {
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;
    @BindView(R.id.no_Data_View)
    LinearLayout no_Data_View;
    @BindView(R.id.no_inernet)
    LinearLayout no_inernet;
    @BindView(R.id.mainView)
    LinearLayout mainView;
    @BindView(R.id.horizontal_list)
    RecyclerView horizontal_list;
    @BindView(R.id.main_horizontal)
    LinearLayout main_horizontal;
    @BindView(R.id.try_again)
    Button try_again;

    private static final int PERMISSION_REQUEST_CODE = 1;
    String wantPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static int DEFAULTCOUNT = 15;

    private Call<Facts> callLoadEntertainmentApi;
    private String getAuthToken;
    boolean loading, isDataAvailable, isNewsEnable, isSubCatDataEnable;
    int pageNo = 1, loadRegex = 5;
    private Facts reviewDataResponseList;
    private ArrayList<MultiViewRequest> list;
    private HomeMultiViewAdapter adapter;
    private ProgressBar progressBar;
    private boolean refreshStatus = false;
    MainApiRecall mainApiRecall;

    //@BindView(R.id.simpleSwipeRefreshLayout)
    SwipeRefreshLayout simpleSwipeRefreshLayout;
    //@BindView(R.id.mainList)
    RecyclerView recycleViewReviewItem;
    RelativeLayout updating_layout;
    private TextView tapUpdate,update_txt;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    LinearLayoutManager linearLayoutManager;
    private ImageView img_home;


    @Override
    public void onResume() {
        super.onResume();
       }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.homefragtab, container, false);
      ButterKnife.bind(this, view);
       keywordManager(getArguments().get("SelectedTab").toString());

        pref          = getActivity().getApplicationContext().getSharedPreferences(Constants.APPNAME, 0);
        editor = pref.edit();

        mainApiRecall =(MainApiRecall)getActivity();
        list = new ArrayList<>();
        list.addAll(Splash.HomeFeed);
        recycleViewReviewItem    = (RecyclerView)      view.findViewById(R.id.recycleViewReviewItem);
        simpleSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.simpleSwipeRefreshLayout);
        progressBar              = (ProgressBar)       view.findViewById(R.id.progressBar);


        tapUpdate                = (TextView)       view. findViewById(R.id.tapUpdate);
        update_txt               = (TextView)       view. findViewById(R.id.update_txt);
        updating_layout          = (RelativeLayout) view.findViewById(R.id.updating_layout);
        img_home                 = (ImageView) getActivity().findViewById(R.id.img);

        tapUpdate.setOnClickListener(this);
        update_txt.setOnClickListener(this);
        try_again.setOnClickListener(this);

        if(!pref.getBoolean("OneTimeAlert",false)&&Constants.ALERTMANAGEMENT){
            Constants.ALERTMANAGEMENT = false;
            oneTime_alert(getActivity());
        }

      /*  else{
            if(Constants.ALERTMANAGEMENT) {
                Constants.ALERTMANAGEMENT = false;
                if (!Constants.checkPermission(wantPermission, getActivity())) {
                    requestPermission(wantPermission);
                }
            }

        }*/


        defaultMgnt();



        /*img_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tapUpdate.performLongClick();

                linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                recycleViewReviewItem.getLayoutManager().getChildCount();
                recycleViewReviewItem.getLayoutManager().getItemCount();
                recycleViewReviewItem.getLayoutManager().scrollToPosition(0);



                tapUpdate.setVisibility(View.GONE);
                refreshStatus = true;
                pageNo = 1;
                sendData(Constants.DeviceID, Constants.Language);
                recycleViewReviewItem.getLayoutManager().scrollToPosition(0);
                tapUpdate.setVisibility(View.GONE);
                tapUpdate.performLongClick();
            }
        });*/

        recycleViewReviewItem.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (linearLayoutManager.findFirstVisibleItemPosition() >= list.size() - 3) {
                    tapUpdate.setVisibility(View.GONE);
                    updating_layout.setVisibility(View.GONE);
                    if (Constants.isNetworkAvailable(getActivity())) {
                        //Constants.setProgress(progressBar);
                        progressBar.setVisibility(View.VISIBLE);
                        if(callAgain)
                            sendData(Constants.DeviceID, Constants.Language);
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

      //  horizontal_View();

     return  view;
    }
    private String keyword = "";
    private void keywordManager(String value){
        pageNo = 1;
        if(Integer.valueOf(value)==0)
            keyword = "";
        else
            keyword = MainActivity.keywordNameList.get(Integer.valueOf(value));
         }

    Main_Following_Adapter horizontal_adapter;
    LinearLayoutManager horizontal;
    private void horizontal_View()
       {

        // horizontal_adapter = new Following_Adapter_Main(MainActivity.this, list_hori,"0");

        //  horizontal_list.setAdapter(adapter);
        FollowInterestRequestNew dataRequest = new FollowInterestRequestNew();
        dataRequest.setDeviceid(Constants.DeviceID);
        dataRequest.setLanguage(pref.getString(Constants.LANGUAGEKEY,"0"));
        dataRequest.setUserid(pref.getString(Constants.USERID,""));
        callFollow_hori(dataRequest);
    }

    private void defaultMgnt()
      {
       //adapter = new NewsMultiViewAdapter(list,(MainActivity)getActivity());

        adapter             = new HomeMultiViewAdapter(getActivity(),list);
        linearLayoutManager = new LinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
        recycleViewReviewItem.setLayoutManager(linearLayoutManager);
        recycleViewReviewItem.setItemAnimator(new DefaultItemAnimator());
        recycleViewReviewItem.setHasFixedSize(true);
        recycleViewReviewItem.setAdapter(adapter);

        recycleViewReviewItem.addItemDecoration(new RecyclerView.ItemDecoration() {
         @Override
          public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int adapterPosition = parent.getChildAdapterPosition(view);
        if (adapterPosition == 0) {
        outRect.top = (int) (4 * scale);
           }
        outRect.bottom = (int) (10 * scale);
                 }
             });
        if(list.size()==0)
          {
        mShimmerViewContainer.setVisibility(View.VISIBLE);
          }
        mShimmerViewContainer.startShimmerAnimation();
        sendData(Constants.DeviceID,Constants.Language);

        simpleSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
        @Override
         public void run() {
           simpleSwipeRefreshLayout.setRefreshing(false);
                        // Generate a random integer number
            refreshStatus = true;
            pageNo = 1;
            sendData(Constants.DeviceID, Constants.Language);
                    }
                }, 1000);
            }
        });
    }



    private void sendData(String deviceID,String lanuage)
             {
          NewsRequest loginRequest = new NewsRequest();
          loginRequest.setCat_id("0");
          loginRequest.setUserid(pref.getString(Constants.USERID,""));
          loginRequest.setLanguage(pref.getString(Constants.LANGUAGEKEY,"0"));
          loginRequest.setCatname(keyword);
          loginRequest.setDeviceid(deviceID);
          loginRequest.setPage(String.valueOf(pageNo));

          if(Constants.isNetworkAvailable(getActivity())) {
         callNewsDataApi(loginRequest);
             }
          else if(!Constants.isNetworkAvailable(getActivity())&&list.size()==0)
            {
         Constants.manageViews(no_inernet,no_Data_View,mainView,mShimmerViewContainer,true,false,false);
            }

          // callNewsDataApi(loginRequest);
      }


    private   ArrayList<MultiViewInterest> list_hori = new ArrayList<MultiViewInterest>();;
    private Call<Facts_Interest> callLoadEntertainmentApi_hori;
    private Facts_Interest reviewDataResponseList_hori;
    boolean loading_hori, isDataAvailable_hori, isNewsEnable_hori, isSubCatDataEnable_hori;
    int pageNoNew_hori = 1;
    public void callFollow_hori(FollowInterestRequestNew dataRequest)
    {
        if (callLoadEntertainmentApi_hori != null) {
            callLoadEntertainmentApi_hori.cancel();
        }


        if (!isNewsEnable_hori) {
            pageNoNew_hori = 1;
        }
        isNewsEnable_hori = true;
        isSubCatDataEnable_hori = false;
        callLoadEntertainmentApi_hori = APIsClient.getInstance().getApiService().userinterest(dataRequest);
        callLoadEntertainmentApi_hori.enqueue(new Callback<Facts_Interest>() {
            @Override
            public void onResponse(Call<Facts_Interest> call, Response<Facts_Interest> response) {
                isNewsEnable_hori = true;

                try {
                    TextView textView=null;


                    if (response.code() == 200) {
                        reviewDataResponseList_hori = response.body();
                        loading_hori = false;
                        if(!list_hori.isEmpty())
                            list_hori.clear();
                        if (reviewDataResponseList_hori.getFacts() != null && reviewDataResponseList_hori.getFacts().size() > 0) {
                            if (reviewDataResponseList_hori.getFacts().size() < 10) {
                                isDataAvailable_hori = false;
                            }
                            isDataAvailable_hori = true;

                            list_hori.addAll(reviewDataResponseList_hori.getFacts());

                            horizontal_adapter = new Main_Following_Adapter(getActivity(),list_hori,"1");
                            horizontal = new LinearLayoutManager(getActivity(), OrientationHelper.HORIZONTAL, false);
                            horizontal_list.setLayoutManager(horizontal);
                            horizontal_list.setItemAnimator(new DefaultItemAnimator());
                            horizontal_list.setHasFixedSize(true);
                            horizontal_list.setAdapter(horizontal_adapter);
                            horizontal_adapter.notifyDataSetChanged();
                            main_horizontal.setVisibility(View.VISIBLE);
                            pageNoNew_hori = pageNoNew_hori + 1;
                            if(list_hori.isEmpty()){
                                main_horizontal.setVisibility(View.GONE);
                                horizontal_list.setVisibility(View.GONE);

                            }

                        } else {
                            if(list_hori.isEmpty()){
                                main_horizontal.setVisibility(View.GONE);
                                horizontal_list.setVisibility(View.GONE);
                            }

                            isDataAvailable_hori = false;
                        }


                    } else if (response.code() == 401) {

                    } else {
                        // hideProgress();
                        //((BaseActivity)getActivity()).showMsg("Something went worng");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //hideProgress();
                    //((BaseActivity)getActivity()).showMsg("Something went worng");
                }
            }

            @Override
            public void onFailure(Call<Facts_Interest> call, Throwable t) {
                // hideProgress();
                // ((BaseActivity)getActivity()).showMsg("Something went worng");
                if (call.isCanceled()) {
                } else {
                }
            }
        });

    }


    private boolean callAgain = true;
    private void callNewsDataApi(NewsRequest loginRequest)
    {
        if (callLoadEntertainmentApi != null) {
            callLoadEntertainmentApi.cancel();
        }


        isNewsEnable = true;
        isSubCatDataEnable = false;
        callAgain = false;
        callLoadEntertainmentApi = APIsClient.getInstance().getApiService().factNewsList(loginRequest);
        callLoadEntertainmentApi.enqueue(new Callback<Facts>() {
            @Override
            public void onResponse(Call<Facts> call, Response<Facts> response) {
                isNewsEnable = true;
                try {
                    callAgain = true;
                    mShimmerViewContainer.startShimmerAnimation();
                    mShimmerViewContainer.setVisibility(View.GONE);

                    updating_layout.setVisibility(View.GONE);
                    if(progressBar.getVisibility()==View.VISIBLE)
                        progressBar.setVisibility(View.GONE);


                    if (response.code() == 200) {
                        reviewDataResponseList = response.body();
                        loading = false;

                        if (!list.isEmpty() && refreshStatus) {
                            refreshStatus = false;
                            list.clear();
                        }
                        if (reviewDataResponseList.getFacts() != null && reviewDataResponseList.getFacts().size() > 0) {
                            if (reviewDataResponseList.getFacts().size() < 10) {
                                isDataAvailable = false;
                            }

                            isDataAvailable = true;
                            list.addAll(reviewDataResponseList.getFacts());
                            adapter.notifyDataSetChanged();
                            pageNo = pageNo + 1;
                            Constants.APICALL = false;

                            Constants.manageViews(no_inernet,no_Data_View,mainView,mShimmerViewContainer,false,false,true);
                        } else if(list.size()==0){
                            isDataAvailable = false;
                            Constants.manageViews(no_inernet,no_Data_View,mainView,mShimmerViewContainer,false,true,false);

                        }



                    } else if (response.code() == 401) {

                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callAgain = true;
                }
            }

            @Override
            public void onFailure(Call<Facts> call, Throwable t) {
                callAgain = true;
                if (call.isCanceled()) {
                } else {
                }
            }
        });

    }

    private void oneTime_alert(Context context)
    {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialog.setTitle("Title...");
        //set the custom dialog components - text, image and button


        TextView txt_msz        = (TextView) dialog.findViewById(R.id.txt_msz);
        TextView _settings      = (TextView) dialog.findViewById(R.id._settings);
        TextView title          = (TextView) dialog.findViewById(R.id.title);
        RelativeLayout btn_not_now    = (RelativeLayout) dialog.findViewById(R.id.btn_not_now);
        RelativeLayout btn_settings   = (RelativeLayout) dialog.findViewById(R.id.btn_settings);
        txt_msz.setText(context.getResources().getString(R.string.about_permission));
        title.setText(context.getResources().getString(R.string.permission_req));
        _settings.setText(context.getResources().getString(R.string.allow));
        //title.setVisibility(View.GONE);
        //if button is clicked, close the custom dialog
        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("OneTimeAlert",true);
                editor.commit();

                dialog.dismiss();
                if (!Constants.checkPermission(wantPermission, getActivity())) {
                    requestPermission(wantPermission);
                }


            }
        });

        btn_not_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor.putBoolean("OneTimeAlert",true);
                editor.commit();
                dialog.dismiss();


            }
        });
        dialog.show();
    }

    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)){
            Toast.makeText(getActivity(), "Write external storage permission allows us to write data.",Toast.LENGTH_SHORT).show();
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission},PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted. Now you can write data.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(),"Permission Denied. You cannot write data.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    @Override
    public FragmentManager getSupportFragmentManager() {
        return getFragmentManager();
    }

    @Override
    public Fragment getSupportFragment() {
        return this;
    }

    float scale;

    public HomeFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        if(list.size()!=0)
            mShimmerViewContainer.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        scale = getResources().getDisplayMetrics().density;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tapUpdate:
                tapUpdate.setVisibility(View.GONE);
                refreshStatus = true;
                pageNo = 1;
                sendData(Constants.DeviceID, Constants.Language);
                recycleViewReviewItem.getLayoutManager().scrollToPosition(0);
                tapUpdate.setVisibility(View.GONE);

                break;
            case R.id.chooseTopics:

                MainActivity.FollowerFrom = "Main";
                Fragment frg = new FollowingFragment();
                final FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.mainContainer, frg).addToBackStack("1").commit();



                break;
            case R.id.try_again:

                tapUpdate.setVisibility(View.GONE);
                refreshStatus = true;
                pageNo = 1;
                sendData(Constants.DeviceID, Constants.Language);
                recycleViewReviewItem.getLayoutManager().scrollToPosition(0);

                break;
        }
    }

    @Override
    public void RecallApi(boolean recall) {
        if (recall) {

            tapUpdate.setVisibility(View.GONE);
            refreshStatus = true;
            pageNo = 1;
            sendData(Constants.DeviceID, Constants.Language);
            recycleViewReviewItem.getLayoutManager().scrollToPosition(0);

            // MainviewPager.getAdapter().notifyDataSetChanged();
            Constants.RefreshStatus = recall;

          //  horizontal_View();



        }
    }

    @Override
    public void reLoadFollower(boolean value) {


    }

    @Override
    public void changeFragment(String s) {

    }

    @Override
    public void reLoad(boolean recall) {

    }

    @Override
    public void buttonStatus(boolean value, Context context) {

    }
}
