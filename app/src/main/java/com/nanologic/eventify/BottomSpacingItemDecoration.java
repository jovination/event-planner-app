package com.nanologic.eventify;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int bottomSpacing;

    public BottomSpacingItemDecoration(int bottomSpacing) {
        this.bottomSpacing = bottomSpacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        outRect.bottom = bottomSpacing;

        if (position >= 0) {
            int itemCount = parent.getAdapter().getItemCount();
            if (position == itemCount - 1) {
                outRect.bottom = bottomSpacing;
            }
        }
    }
}
