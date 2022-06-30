package com.asprime.techmatesupport.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.asprime.techmatesupport.R;
import com.asprime.techmatesupport.databinding.NotificationSetupBinding;
import java.util.Objects;

public class NotificationSetUpDialog extends DialogFragment implements View.OnClickListener {
    NotificationSetupBinding notificationSetupBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        notificationSetupBinding = NotificationSetupBinding.inflate(getLayoutInflater());
        View v = notificationSetupBinding.getRoot();
        dialog.setContentView(v);

        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        window.setAttributes(wlp);

        notificationSetupBinding.closeDialogBtn.setOnClickListener(this);
        return dialog;
    }

    @Override
    public int getTheme() {
        return R.style.CenterDialogStyleTheme;
    }

    @Override
    public void onClick(View v) {
        Objects.requireNonNull(getDialog()).dismiss();
    }
}
