package com.weberbox.pifire.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonSyntaxException;
import com.skydoves.powerspinner.DefaultSpinnerAdapter;
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.control.GrillControl;
import com.weberbox.pifire.databinding.FragmentPelletsBinding;
import com.weberbox.pifire.databinding.LayoutPelletsBinding;
import com.weberbox.pifire.databinding.LayoutPelletsProfileAddBinding;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.model.PelletProfileModel;
import com.weberbox.pifire.model.PelletResponseModel;
import com.weberbox.pifire.recycler.adapter.PelletItemsAdapter;
import com.weberbox.pifire.recycler.adapter.PelletProfileEditAdapter;
import com.weberbox.pifire.recycler.adapter.PelletsLogAdapter;
import com.weberbox.pifire.recycler.viewmodel.PelletItemViewModel;
import com.weberbox.pifire.recycler.viewmodel.PelletLogViewModel;
import com.weberbox.pifire.ui.dialogs.PelletPickerDialog;
import com.weberbox.pifire.ui.dialogs.PelletsAddDialog;
import com.weberbox.pifire.ui.dialogs.PelletsDeleteDialog;
import com.weberbox.pifire.ui.model.DataModel;
import com.weberbox.pifire.ui.model.MainViewModel;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.FadeTransition;
import com.weberbox.pifire.ui.views.CardViewHeaderButton;
import com.weberbox.pifire.ui.views.PelletsCardViewRecycler;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class PelletsFragment extends Fragment implements PelletsCallbackInterface {

    private MainViewModel mMainViewModel;
    private FragmentPelletsBinding mBinding;
    private LayoutPelletsBinding mPelletsBinding;
    private RelativeLayout mRootContainer;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mLoadingBar;
    private RecyclerView mPelletBrandsRecycler;
    private RecyclerView mPelletWoodsRecycler;
    private RecyclerView mPelletLogRecycler;
    private RecyclerView mPelletProfileEditRecycler;
    private PelletItemsAdapter mPelletBrandsAdapter;
    private PelletItemsAdapter mPelletWoodsAdapter;
    private PelletsLogAdapter mPelletsLogAdapter;
    private PelletProfileEditAdapter mPelletProfileEditAdapter;
    private DefaultSpinnerAdapter mBrandsSpinnerAdapter;
    private DefaultSpinnerAdapter mWoodsSpinnerAdapter;
    private List<PelletItemViewModel> mBrandsEditList;
    private List<PelletItemViewModel> mWoodsEditList;
    private List<PelletLogViewModel> mLogsList;
    private List<PelletProfileModel> mProfileList;
    private List<PelletProfileModel> mProfileEditList;
    private List<String> mBrandsList;
    private List<String> mWoodsList;
    private LinearLayout mCurrentView;
    private LinearLayout mAddProfileCard;
    private LinearLayout mCurrentPlaceholder;
    private LinearLayout mBrandsPlaceholder;
    private LinearLayout mWoodsPlaceholder;
    private LinearLayout mProfilePlaceholder;
    private LinearLayout mLogsPlaceholder;
    private AppCompatButton mSaveLoadProfile;
    private AppCompatButton mSaveProfile;
    private PowerSpinnerView mPelletProfileBrand;
    private PowerSpinnerView mPelletProfileWood;
    private PowerSpinnerView mPelletProfileRating;
    private EditText mProfileAddComments;
    private TextView mAddNewProfile;
    private String mCurrentPelletId;
    private Socket mSocket;

    private boolean mIsLoading = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            PiFireApplication app = (PiFireApplication) getActivity().getApplication();
            mSocket = app.getSocket();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPelletsBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBrandsList = new ArrayList<>();
        mWoodsList = new ArrayList<>();
        mBrandsEditList = new ArrayList<>();
        mWoodsEditList = new ArrayList<>();
        mLogsList = new ArrayList<>();
        mProfileList = new ArrayList<>();
        mProfileEditList = new ArrayList<>();

        mPelletsBinding = mBinding.pelletsLayout;

        mRootContainer = mPelletsBinding.pelletsRootContainer;
        mSwipeRefresh = mBinding.pelletsPullRefresh;
        mLoadingBar = mPelletsBinding.loadingProgressbar;
        mCurrentView = mPelletsBinding.currentView;
        mCurrentPlaceholder = mPelletsBinding.currentHolder;
        mProfilePlaceholder = mPelletsBinding.profileHolder;
        mLogsPlaceholder = mPelletsBinding.logsHolder;;
        mAddNewProfile = mPelletsBinding.addProfileButton;

        CardViewHeaderButton currentHeader = mPelletsBinding.loadOutHeader;
        PelletsCardViewRecycler brandsCardView = mPelletsBinding.brandsCardView;
        PelletsCardViewRecycler woodsCardView = mPelletsBinding.woodsCardView;

        mBrandsPlaceholder = brandsCardView.getHolderView();
        mWoodsPlaceholder = woodsCardView.getHolderView();
        TextView addNewBrand = brandsCardView.getHeaderButton();
        TextView addNewWood = woodsCardView.getHeaderButton();

        TextView loadNewPellets = currentHeader.getButton();

        LayoutPelletsProfileAddBinding pelletsProfileAddBinding =
                mPelletsBinding.pelletsAddProfileContainer;

        mSaveProfile = pelletsProfileAddBinding.pelletAddSave;
        mSaveLoadProfile = pelletsProfileAddBinding.pelletAddLoad;
        mPelletProfileBrand = pelletsProfileAddBinding.pelletEditContainer.pelletEditBrandText;
        mPelletProfileWood = pelletsProfileAddBinding.pelletEditContainer.pelletEditWoodText;
        mPelletProfileRating = pelletsProfileAddBinding.pelletEditContainer.pelletEditRatingText;
        mProfileAddComments = pelletsProfileAddBinding.pelletEditContainer.pelletEditCommentsText;


        mPelletBrandsRecycler = brandsCardView.getRecycler();
        mPelletWoodsRecycler = woodsCardView.getRecycler();
        mPelletLogRecycler = mPelletsBinding.logsRecycler;
        mPelletProfileEditRecycler = mPelletsBinding.editorRecycler;
        mAddProfileCard = mPelletsBinding.pelletsAddProfile;

        mPelletBrandsAdapter = new PelletItemsAdapter(mBrandsEditList, this);

        mPelletBrandsRecycler.setAdapter(mPelletBrandsAdapter);

        mPelletWoodsAdapter = new PelletItemsAdapter(mWoodsEditList, this);

        mPelletWoodsRecycler.setAdapter(mPelletWoodsAdapter);

        mPelletsLogAdapter = new PelletsLogAdapter(mLogsList);

        mPelletLogRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mPelletLogRecycler.setItemAnimator(new DefaultItemAnimator());
        mPelletLogRecycler.setAdapter(mPelletsLogAdapter);

        mPelletProfileEditAdapter = new PelletProfileEditAdapter(mBrandsList, mWoodsList,
                mProfileEditList, this);

        mPelletProfileEditRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        mPelletProfileEditRecycler.setItemAnimator(new DefaultItemAnimator());
        mPelletProfileEditRecycler.setAdapter(mPelletProfileEditAdapter);

        mPelletProfileEditRecycler.setNestedScrollingEnabled(false);
        mPelletLogRecycler.setNestedScrollingEnabled(false);


        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mSocket != null && mSocket.connected()) {
                    forceRefreshData();
                } else {
                    mSwipeRefresh.setRefreshing(false);
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        loadNewPellets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSocket != null && mSocket.connected()) {
                    if (mProfileList != null && mCurrentPelletId != null) {
                        PelletPickerDialog pelletPickerDialog = new PelletPickerDialog(getActivity(),
                                PelletsFragment.this, mProfileList, mCurrentPelletId);
                        pelletPickerDialog.showDialog();
                    }
                } else {
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        addNewBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    if (mSocket != null && mSocket.connected()) {
                        PelletsAddDialog pelletsAddDialog = new PelletsAddDialog(getActivity(),
                                PelletsFragment.this, Constants.PELLET_BRAND,
                                getString(R.string.pellets_add_brand));
                        pelletsAddDialog.showDialog();
                    } else {
                        AnimUtils.shakeOfflineBanner(getActivity());
                    }
                }
            }
        });

        addNewWood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSocket != null && mSocket.connected()) {
                    PelletsAddDialog pelletsAddDialog = new PelletsAddDialog(getActivity(),
                            PelletsFragment.this, Constants.PELLET_WOOD,
                            getString(R.string.pellets_add_woods));
                    pelletsAddDialog.showDialog();
                } else {
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        mAddNewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSocket != null && mSocket.connected()) {
                    toggleCardView();
                } else {
                    AnimUtils.shakeOfflineBanner(getActivity());
                }
            }
        });

        mSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPelletProfileBrand.getText().length() == 0) {
                    mPelletProfileBrand.setError(getString(R.string.settings_blank_error));
                } else if (mPelletProfileWood.getText().length() == 0) {
                    mPelletProfileWood.setError(getString(R.string.settings_blank_error));
                } else if (mPelletProfileRating.getText().length() == 0) {
                    mPelletProfileRating.setError(getString(R.string.settings_blank_error));
                } else {
                    mAddProfileCard.setVisibility(View.GONE);
                    mAddNewProfile.setText(R.string.pellets_add);
                    if (mSocket != null && mSocket.connected()) {
                        GrillControl.setAddPelletProfile(mSocket,
                                new PelletProfileModel(
                                        mPelletProfileBrand.getText().toString(),
                                        mPelletProfileWood.getText().toString(),
                                        StringUtils.getRatingInt(mPelletProfileRating.getText().toString()),
                                        mProfileAddComments.getText().toString(),
                                        "None"
                                ));
                        forceRefreshData();
                    } else {
                        AnimUtils.shakeOfflineBanner(getActivity());
                    }
                }
            }
        });

        mSaveLoadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPelletProfileBrand.getText().length() == 0) {
                    mPelletProfileBrand.setError(getString(R.string.settings_blank_error));
                } else if (mPelletProfileWood.getText().length() == 0) {
                    mPelletProfileWood.setError(getString(R.string.settings_blank_error));
                } else if (mPelletProfileRating.getText().length() == 0) {
                    mPelletProfileRating.setError(getString(R.string.settings_blank_error));
                } else {
                    mAddProfileCard.setVisibility(View.GONE);
                    mAddNewProfile.setText(R.string.pellets_add);
                    if (mSocket != null && mSocket.connected()) {
                        GrillControl.setAddPelletProfileLoad(mSocket,
                                new PelletProfileModel(
                                        mPelletProfileBrand.getText().toString(),
                                        mPelletProfileWood.getText().toString(),
                                        StringUtils.getRatingInt(mPelletProfileRating.getText().toString()),
                                        mProfileAddComments.getText().toString(),
                                        "None"
                                ));
                        forceRefreshData();
                    } else {
                        AnimUtils.shakeOfflineBanner(getActivity());
                    }
                }
            }
        });

        mPelletProfileBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    mPelletProfileBrand.setError(getString(R.string.settings_blank_error));
                } else {
                    mPelletProfileBrand.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mPelletProfileWood.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    mPelletProfileWood.setError(getString(R.string.settings_blank_error));
                } else {
                    mPelletProfileWood.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mPelletProfileRating.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    mPelletProfileRating.setError(getString(R.string.settings_blank_error));
                } else {
                    mPelletProfileRating.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mPelletProfileBrand.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
                mPelletProfileBrand.dismiss();
            }
        });


        mPelletProfileWood.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
                mPelletProfileWood.dismiss();
            }
        });


        mPelletProfileRating.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
                mPelletProfileRating.dismiss();
            }
        });

        if (getActivity() != null) {
            mMainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
            mMainViewModel.getPelletData().observe(getViewLifecycleOwner(), new Observer<DataModel>() {
                @Override
                public void onChanged(@Nullable DataModel pelletData) {
                    mSwipeRefresh.setRefreshing(false);
                    if (pelletData != null && pelletData.getLiveData() != null) {
                        if (pelletData.getIsNewData()) {
                            FileUtils.saveJSONFile(getActivity(), Constants.JSON_PELLETS,
                                    pelletData.getLiveData());
                        }
                        updateUIWithData(pelletData.getLiveData());
                    }
                }
            });

            mMainViewModel.getServerConnected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(@Nullable Boolean enabled) {
                    if (enabled != null && enabled) {
                        if (!mIsLoading) {
                            mIsLoading = true;
                            if (mSocket != null && mSocket.connected()) {
                                toggleLoading(true);
                                requestDataUpdate();
                            }
                        }
                    }
                }
            });
        }

        checkViewModelData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    private void checkViewModelData() {
        if (mMainViewModel.getEventsData().getValue() == null) {
            toggleLoading(true);
            loadStoredDataRequestUpdate();
        }
    }

    private void requestDataUpdate() {
        if (mSocket != null && mSocket.connected()) {
            mIsLoading = true;
            mSocket.emit(ServerConstants.REQUEST_PELLET_DATA, new Ack() {
                @Override
                public void call(Object... args) {
                    if (mMainViewModel != null) {
                        mMainViewModel.setPelletData(args[0].toString(), true);
                        mIsLoading = false;
                    }
                }
            });
        }
    }

    private void loadStoredDataRequestUpdate() {
        String jsonData = FileUtils.loadJSONFile(getActivity(), Constants.JSON_PELLETS);
        if (jsonData != null && mMainViewModel != null) {
            mMainViewModel.setPelletData(jsonData, false);
        }
        if (!mIsLoading) {
            requestDataUpdate();
        }
    }

    private void forceRefreshData() {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit(ServerConstants.REQUEST_PELLET_DATA, new Ack() {
                @Override
                public void call(Object... args) {
                    if (mMainViewModel != null) {
                        mMainViewModel.setPelletData(args[0].toString(), true);
                    }
                }
            });
        }
    }

    private void toggleLoading(boolean show) {
        if (show && mSocket != null && mSocket.connected()) {
            mLoadingBar.setVisibility(View.VISIBLE);
        } else {
            mLoadingBar.setVisibility(View.GONE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUIWithData(String response_data) {
        mBrandsList.clear();
        mWoodsList.clear();
        mBrandsEditList.clear();
        mWoodsEditList.clear();
        mLogsList.clear();
        mProfileList.clear();
        mProfileEditList.clear();

        try {
            PelletResponseModel pelletResponseModel = PelletResponseModel.parseJSON(response_data);

            PelletResponseModel.Current current =  pelletResponseModel.getCurrent();

            mBrandsList.addAll(pelletResponseModel.getBrands());
            mWoodsList.addAll(pelletResponseModel.getWoods());

            Map<String, String> logs = pelletResponseModel.getLogs();
            Map<String, PelletProfileModel> profiles = pelletResponseModel.getProfiles();

            TransitionManager.beginDelayedTransition(mRootContainer, new FadeTransition());

            mProfileList.addAll(profiles.values());

            if (current.getDateLoaded() != null) {
                mPelletsBinding.currentInclude.currentDateLoadedText.setText(current.getDateLoaded());
            }

            if (current.getPelletId() != null) {
                mCurrentPelletId = current.getPelletId();
            }

            if (current.getPelletId() != null) {
                PelletProfileModel currentProfile = profiles.get(current.getPelletId());
                if (currentProfile != null) {
                    mPelletsBinding.currentInclude.currentBrandText.setText(currentProfile.getBrand());
                    mPelletsBinding.currentInclude.currentWoodText.setText(currentProfile.getWood());
                    mPelletsBinding.currentInclude.currentCommentsText.setText(currentProfile.getComments());
                    mPelletsBinding.currentInclude.currentRatingText.setText(StringUtils.getRatingText(
                            currentProfile.getRating()));
                }
            }

            for (int i = 0; i < mBrandsList.size(); i++) {
                PelletItemViewModel brandsList = new PelletItemViewModel(
                        mBrandsList.get(i),
                        Constants.PELLET_BRAND
                );
                mBrandsEditList.add(brandsList);
            }

            mPelletBrandsAdapter.notifyDataSetChanged();


            for (int i = 0; i < mWoodsList.size(); i++) {
                PelletItemViewModel woodsList = new PelletItemViewModel(
                        mWoodsList.get(i),
                        Constants.PELLET_WOOD
                );
                mWoodsEditList.add(woodsList);
            }

            mPelletWoodsAdapter.notifyDataSetChanged();

            for (PelletProfileModel profile:profiles.values()) {

                if (profile != null) {
                    PelletProfileModel profileEditList = new PelletProfileModel(
                            profile.getBrand(),
                            profile.getWood(),
                            profile.getRating(),
                            profile.getComments(),
                            profile.getId()
                    );
                    mProfileEditList.add(profileEditList);
                }

                mPelletProfileEditAdapter.notifyDataSetChanged();

            }

            for (String item:logs.keySet()) {
                String logPelletId = logs.get(item);

                PelletProfileModel pelletProfile = profiles.get(logPelletId);

                if (pelletProfile != null) {
                    PelletLogViewModel logList = new PelletLogViewModel(
                            item,
                            pelletProfile.getBrand() + " " + pelletProfile.getWood(),
                            pelletProfile.getRating()
                    );
                    mLogsList.add(logList);
                }

                mPelletsLogAdapter.notifyDataSetChanged();

            }

            mBrandsSpinnerAdapter = new DefaultSpinnerAdapter(mPelletProfileBrand);
            mPelletProfileBrand.setSpinnerAdapter(mBrandsSpinnerAdapter);
            mPelletProfileBrand.setItems(mBrandsList);

            mWoodsSpinnerAdapter = new DefaultSpinnerAdapter(mPelletProfileWood);
            mPelletProfileWood.setSpinnerAdapter(mWoodsSpinnerAdapter);
            mPelletProfileWood.setItems(mWoodsList);


        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w(e,"JSON Error");
            showSnackBarMessage(getActivity(), R.string.json_error_pellets);
        }

        mCurrentPlaceholder.setVisibility(View.GONE);
        mBrandsPlaceholder.setVisibility(View.GONE);
        mWoodsPlaceholder.setVisibility(View.GONE);
        mProfilePlaceholder.setVisibility(View.GONE);
        mLogsPlaceholder.setVisibility(View.GONE);

        toggleLoading(false);
    }

    private void toggleCardView() {
        boolean visibility = mAddProfileCard.getVisibility() == View.VISIBLE;
        if (visibility) {
            AnimUtils.slideUp(mAddProfileCard);
        } else {
            AnimUtils.slideDown(mAddProfileCard);
        }
        mAddNewProfile.setText(visibility ? R.string.pellets_add : R.string.close);
    }

    @Override
    public void onItemDelete(String type, String item, int position) {
        if (getActivity() != null) {
            if (mSocket != null && mSocket.connected()) {
                PelletsDeleteDialog pelletsDialog = new PelletsDeleteDialog(getActivity(),
                        this, type, item, position);
                pelletsDialog.showDialog();
            } else {
                AnimUtils.shakeOfflineBanner(getActivity());
            }
        }
    }

    @Override
    public void onDeleteConfirmed(String type, String item, int position) {
        if (mSocket != null && mSocket.connected()) {
            switch (type) {
                case Constants.PELLET_WOOD:
                    GrillControl.setDeletePelletWood(mSocket, item);
                    mWoodsEditList.remove(position);
                    mPelletWoodsAdapter.notifyItemRemoved(position);
                    mPelletWoodsAdapter.notifyItemRangeChanged(position, mWoodsEditList.size());
                    break;
                case Constants.PELLET_BRAND:
                    GrillControl.setDeletePelletBrand(mSocket, item);
                    mBrandsEditList.remove(position);
                    mPelletBrandsAdapter.notifyItemRemoved(position);
                    mPelletBrandsAdapter.notifyItemRangeChanged(position, mBrandsEditList.size());
                    break;
                case Constants.PELLET_PROFILE:
                    deletePelletProfile(item, position);
                    break;
            }
        } else {
            if (getActivity() != null) AnimUtils.shakeOfflineBanner(getActivity());
        }
    }

    @Override
    public void onItemAdded(String type, String item) {
        if (mSocket != null && mSocket.connected()) {
            switch (type) {
                case Constants.PELLET_WOOD:
                    GrillControl.setAddPelletWood(mSocket, item);
                    forceRefreshData();

                    //PelletItemViewModel wood = new PelletItemViewModel(
                            //item,
                            //Constants.PELLET_WOOD
                    //);
                    //mWoodsList.add(wood);
                    //mPelletWoodsAdapter.notifyItemInserted(mWoodsList.size() - 1);
                    break;
                case Constants.PELLET_BRAND:
                    GrillControl.setAddPelletBrand(mSocket, item);
                    forceRefreshData();

                    //PelletItemViewModel brand = new PelletItemViewModel(
                            //item,
                            //Constants.PELLET_BRAND
                    //);
                    //mBrandsList.add(brand);
                    //mPelletBrandsAdapter.notifyItemInserted(mBrandsList.size() - 1);
                    break;
            }
        } else {
            AnimUtils.shakeOfflineBanner(getActivity());
        }
    }

    @Override
    public void onProfileSelected(String profileName, String profileId) {
        if (profileId != null && mSocket != null) {
            GrillControl.setLoadPelletProfile(mSocket, profileId);
            forceRefreshData();
        }
    }

    @Override
    public void onProfileEdit(PelletProfileModel model) {
        if (mSocket != null && mSocket.connected()) {
            GrillControl.setEditPelletProfile(mSocket, model);
        } else {
            AnimUtils.shakeOfflineBanner(getActivity());
        }
    }

    @Override
    public void onProfileDelete(String profileId, int position) {
        deletePelletProfile(profileId, position);
    }

    private void deletePelletProfile(String profile, int position) {
        if (mSocket != null && mSocket.connected()) {
            if (mCurrentPelletId != null && !mCurrentPelletId.equals(profile)) {
                GrillControl.setDeletePelletProfile(mSocket, profile);
                mProfileEditList.remove(position);
                mPelletProfileEditAdapter.notifyItemRemoved(position);
                mPelletProfileEditAdapter.notifyItemRangeChanged(position,
                        mProfileEditList.size());
            } else {
                showSnackBarMessage(getActivity(), R.string.pellets_cannot_delete_profile);
            }
        } else {
            AnimUtils.shakeOfflineBanner(getActivity());
        }
    }

    private void showSnackBarMessage(Activity activity, int message) {
        if (activity != null) {
            Snackbar snack = Snackbar.make(mBinding.getRoot(), message, Snackbar.LENGTH_LONG);
            snack.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorAccentRed)));
            snack.setTextColor(activity.getColor(R.color.colorWhite));
            snack.show();
        }
    }
}