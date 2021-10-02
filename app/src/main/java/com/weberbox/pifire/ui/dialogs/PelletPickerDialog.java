package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogProfilePickerBinding;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.interfaces.OnScrollStopListener;
import com.weberbox.pifire.model.PelletProfileModel;
import com.weberbox.pifire.recycler.adapter.PelletProfileAdapter;
import com.weberbox.pifire.recycler.manager.PickerLayoutManager;

import java.util.List;

public class PelletPickerDialog {

    private DialogProfilePickerBinding mBinding;
    private final BottomSheetDialog mPelletPickerBottomSheet;
    private final LayoutInflater mInflater;
    private final PelletsCallbackInterface mCallBack;
    private final List<PelletProfileModel> mPelletsList;
    private final Context mContext;
    private RecyclerView mProfileList;
    private PelletProfileAdapter mProfileAdapter;
    private String mCurrentProfile;
    private String mCurrentProfileId;

    public PelletPickerDialog(Context context, Fragment fragment,
                              List<PelletProfileModel> pelletList, String currentProfile) {
        mPelletPickerBottomSheet = new BottomSheetDialog(context, R.style.BottomSheetDialog);
        mInflater = LayoutInflater.from(context);
        mCallBack = (PelletsCallbackInterface) fragment;
        mContext = context;
        mPelletsList = pelletList;
        mCurrentProfileId = currentProfile;
    }

    public BottomSheetDialog showDialog() {
        mBinding = DialogProfilePickerBinding.inflate(mInflater);

        Button confirmButton = mBinding.setProfileLoad;
        Button cancelButton = mBinding.setProfileCancel;

        PickerLayoutManager pelletPickerLayoutManager = new PickerLayoutManager(mContext,
                PickerLayoutManager.VERTICAL, false);
        pelletPickerLayoutManager.setChangeAlpha(true);
        pelletPickerLayoutManager.setScaleDownBy(0.99f);
        pelletPickerLayoutManager.setScaleDownDistance(1.2f);

        mProfileList = mBinding.profileList;

        SnapHelper profileSnapHelper = new LinearSnapHelper();
        profileSnapHelper.attachToRecyclerView(mProfileList);

        mProfileAdapter = new PelletProfileAdapter(mPelletsList);

        mProfileList.setLayoutManager(pelletPickerLayoutManager);
        mProfileList.setAdapter(mProfileAdapter);

        mCurrentProfileId = mPelletsList.get(0).getId();

        pelletPickerLayoutManager.setOnScrollStopListener(
                new OnScrollStopListener() {
                    @Override
                    public void selectedView(View view) {
                        LinearLayout parent = view.findViewById(R.id.profile_item_container);
                        RelativeLayout parent_two = parent.findViewById(R.id.profile_item_container_two);
                        TextView text = parent_two.findViewById(R.id.profile_item_text_view);
                        TextView id = parent_two.findViewById(R.id.profile_item_id);
                        mCurrentProfile = text.getText().toString();
                        mCurrentProfileId = id.getText().toString();
                    }
                });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPelletPickerBottomSheet.dismiss();
                mCallBack.onProfileSelected(mCurrentProfile, mCurrentProfileId);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPelletPickerBottomSheet.dismiss();
            }
        });

        mPelletPickerBottomSheet.setContentView(mBinding.getRoot());

        mPelletPickerBottomSheet.show();

        return mPelletPickerBottomSheet;
    }

    private void setDefaultProfile(int position, boolean smooth){
        if (smooth) {
            mProfileList.smoothScrollToPosition(position);
        } else {
            mProfileList.scrollToPosition(position);
        }
    }
}
