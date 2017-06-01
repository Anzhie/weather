package ru.khasanova.weatherhh;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Анжелика.
 */

public class ItemDivider extends RecyclerView.ItemDecoration {
    private final Drawable divider;

    public ItemDivider(Context context){
        int[] attrs = {android.R.attr.listDivider};
        divider     = context.obtainStyledAttributes(attrs).getDrawable(0);
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state){
        super.onDrawOver(canvas, parent, state);

        //левая и правая точки (начало и конец) линии; для всех одинаковые
        int left    = parent.getPaddingLeft();
        int right   = parent.getWidth() - parent.getPaddingRight();

        //для каждого элемента RecyclerView
        for (int i = 0; i < parent.getChildCount(); i++){
            View item = parent.getChildAt(i);

            //верхняя и нижняя границы
            int top = item.getBottom() + ((RecyclerView.LayoutParams) item.getLayoutParams()).bottomMargin;
            int bottom  = top + divider.getIntrinsicHeight();

            //указываем границы линии
            divider.setBounds(left, top, right, bottom);
            //и собственно рисуем
            divider.draw(canvas);
        }
    }
}
