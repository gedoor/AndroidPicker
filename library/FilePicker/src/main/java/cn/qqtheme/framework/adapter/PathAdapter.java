package cn.qqtheme.framework.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.LinkedList;

import cn.qqtheme.framework.icons.FilePickerIcon;
import cn.qqtheme.framework.util.ConvertUtils;

public class PathAdapter extends RecyclerView.Adapter<PathAdapter.MyViewHolder> {
    private static final String ROOT_HINT = "SD";
    private LinkedList<String> paths = new LinkedList<>();
    private Drawable arrowIcon = null;
    private String sdCardDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public String getItem(int position) {
        StringBuilder tmp = new StringBuilder(sdCardDirectory + "/");
        //忽略根目录
        if (position == 0) {
            return tmp.toString();
        }
        for (int i = 1; i <= position; i++) {
            tmp.append(paths.get(i)).append("/");
        }
        return tmp.toString();
    }

    public void setArrowIcon(Drawable arrowIcon) {
        this.arrowIcon = arrowIcon;
    }

    public void updatePath(String path) {
        path = path.replace(sdCardDirectory, "");
        if (arrowIcon == null) {
            arrowIcon = ConvertUtils.toDrawable(FilePickerIcon.getARROW());
        }
        paths.clear();
        if (!path.equals("/") && !path.equals("")) {
            String[] tmps = path.substring(path.indexOf("/") + 1).split("/");
            Collections.addAll(paths, tmps);
        }
        paths.addFirst(ROOT_HINT);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
        int wrapContent = ViewGroup.LayoutParams.WRAP_CONTENT;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        // fixed: 17-1-8 #79 安卓4.x兼容问题，java.lang.ClassCastException……onMeasure……
        if (parent instanceof AbsListView) {
            layout.setLayoutParams(new AbsListView.LayoutParams(wrapContent, matchParent));
        } else {
            layout.setLayoutParams(new ViewGroup.LayoutParams(wrapContent, matchParent));
        }

        TextView textView = new TextView(context);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(wrapContent, matchParent);
        textView.setLayoutParams(tvParams);
        textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        int padding = ConvertUtils.toPx(context, 5);
        textView.setPadding(padding, 0, padding, 0);
        layout.addView(textView);

        ImageView imageView = new ImageView(context);
        int width = ConvertUtils.toPx(context, 20);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(width, matchParent));
        layout.addView(imageView);
        MyViewHolder myViewHolder = new MyViewHolder(layout);
        myViewHolder.imageView = imageView;
        myViewHolder.textView = textView;
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView.setText(paths.get(position));
        holder.imageView.setImageDrawable(arrowIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.onPathClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface CallBack {
        void onPathClick(int position);
    }
}
