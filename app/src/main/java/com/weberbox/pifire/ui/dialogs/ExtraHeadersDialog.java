package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogHeadersEditBinding;
import com.weberbox.pifire.databinding.LayoutHeadersEditCardBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogHeadersCallback;
import com.weberbox.pifire.ui.utils.ViewUtils;
public class ExtraHeadersDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogHeadersCallback callback;
    private final Context context;
    private final String key, value;
    private final Integer position;
    private TextInputEditText headerKey, headerValue;

    public ExtraHeadersDialog(Context context, String key, String value, Integer position,
                              DialogHeadersCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.key = key;
        this.value = value;
        this.position = position;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogHeadersEditBinding binding = DialogHeadersEditBinding.inflate(inflater);

        LayoutHeadersEditCardBinding headersEdit = binding.headersEditContainer;

        AppCompatButton headerSave = binding.headersEditSave;
        AppCompatButton headerDelete = binding.headersEditDelete;
        headerKey = headersEdit.headersEditKeyTv;
        headerValue = headersEdit.headersEditValueTv;

        headerKey.setText(key != null ? key : "");
        headerValue.setText(value != null ? value : "");

        if (key != null || value != null) {
            headerDelete.setVisibility(View.VISIBLE);
        } else {
            headerDelete.setVisibility(View.GONE);
        }

        headerKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    headerKey.setError(context.getString(R.string.text_blank_error));
                } else {
                    headerKey.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        headerValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    headerValue.setError(context.getString(R.string.text_blank_error));
                } else {
                    headerValue.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        headerSave.setOnClickListener(v -> {
            Editable keyEdit = headerKey.getText();
            Editable valueEdit = headerValue.getText();
            if (keyEdit != null && valueEdit != null) {
                String key = keyEdit.toString();
                String value = valueEdit.toString();
                if (key.isEmpty()) {
                    headerKey.setError(context.getString(R.string.text_blank_error));
                }
                if (value.isEmpty()) {
                    headerValue.setError(context.getString(R.string.text_blank_error));
                }
                if (!key.isEmpty() && !value.isEmpty()) {
                    callback.onDialogSave(key, value, position);
                    bottomSheetDialog.dismiss();
                }
            }
        });

        headerDelete.setOnClickListener(v -> {
            callback.onDialogDelete(position);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(binding.getRoot());

        bottomSheetDialog.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        bottomSheetDialog.show();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            if (bottomSheetDialog.getWindow() != null) {
                bottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        }

        return bottomSheetDialog;
    }
}
