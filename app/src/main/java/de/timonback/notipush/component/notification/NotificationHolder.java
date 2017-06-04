package de.timonback.notipush.component.notification;

import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RotateDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.timonback.notipush.R;

public class NotificationHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "NotificationHolder";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

    private final TextView mNameField;
    private final TextView mTextField;
    private final FrameLayout mLeftArrow;
    private final FrameLayout mRightArrow;
    private final RelativeLayout mMessageContainer;
    private final LinearLayout mMessage;
    private final int mGreen300;
    private final int mGray300;

    public NotificationHolder(View itemView) {
        super(itemView);
        mNameField = (TextView) itemView.findViewById(R.id.name_text);
        mTextField = (TextView) itemView.findViewById(R.id.message_text);
        mLeftArrow = (FrameLayout) itemView.findViewById(R.id.left_arrow);
        mRightArrow = (FrameLayout) itemView.findViewById(R.id.right_arrow);
        mMessageContainer = (RelativeLayout) itemView.findViewById(R.id.message_container);
        mMessage = (LinearLayout) itemView.findViewById(R.id.message);
        mGreen300 = ContextCompat.getColor(itemView.getContext(), R.color.material_green_300);
        mGray300 = ContextCompat.getColor(itemView.getContext(), R.color.material_gray_300);
    }

    public void setIsSender(boolean isSender) {
        final int color;
        if (isSender) {
            color = mGreen300;
            mLeftArrow.setVisibility(View.GONE);
            mRightArrow.setVisibility(View.VISIBLE);
            mMessageContainer.setGravity(Gravity.END);
        } else {
            color = mGray300;
            mLeftArrow.setVisibility(View.VISIBLE);
            mRightArrow.setVisibility(View.GONE);
            mMessageContainer.setGravity(Gravity.START);
        }

        ((GradientDrawable) mMessage.getBackground()).setColor(color);
        ((RotateDrawable) mLeftArrow.getBackground()).getDrawable()
                .setColorFilter(color, PorterDuff.Mode.SRC);
        ((RotateDrawable) mRightArrow.getBackground()).getDrawable()
                .setColorFilter(color, PorterDuff.Mode.SRC);
    }

    public void setMessage(String message) {
        mTextField.setText(message);
    }

    public void setDate(String date) {
        String text = "Date unknown";
        if (date != null) {
            text = sdf.format(new Date(Long.valueOf(date)));
        }
        mNameField.setText(text);
    }
}
