package com.example.koreatechfairy4.util;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class NotifyItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public NotifyItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // 첫 번째 아이템에만 위쪽 여백 추가
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space;
        }
    }
}
