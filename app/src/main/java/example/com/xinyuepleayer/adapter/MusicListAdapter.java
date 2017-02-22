package example.com.xinyuepleayer.adapter;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.bean.MusicInfoBean;
import example.com.xinyuepleayer.utils.MyLogUtil;

/**
 * Created by caobin on 2017/1/12.
 */
public class MusicListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<MusicInfoBean> mList;
    private Context mContext;
    private MyClickListener clickListener;
    private int currentPosition;

    public MusicListAdapter(Context context, MyClickListener clickListener) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }


    public void setList(List<MusicInfoBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mList == null) {
            return 0;
        }
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View containView, ViewGroup parent) {
        View view = containView;
        ViewHolder viewHolder = null;
        if (containView == null) {
            view = mInflater.inflate(R.layout.item_music_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.tv_music_name);
            viewHolder.author = (TextView) view.findViewById(R.id.tv_music_author);
            viewHolder.isPlayingImg = (TextView) view.findViewById(R.id.tv_music_list_head);
            viewHolder.more = (ImageView) view.findViewById(R.id.iv_more_menu);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final MusicInfoBean bean = (MusicInfoBean) getItem(position);
        viewHolder.name.setText(bean.getTitle());
        viewHolder.author.setText(bean.getArtist());
        viewHolder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPosition = position;
                showDialog();
            }
        });
        return view;
    }

    /**
     * 弹出底部dialog
     */
    private void showDialog() {
        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        View view = View.inflate(mContext, R.layout.item_list_bottom_dialog, null);
        //这里可以自定义View，不一定非要是listView；recyclerView也可以，
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(new BottomListAdapter(mContext));
        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String musicName = mList.get(currentPosition).getTitle();
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("温馨提示");
                        builder.setMessage("确认要删除歌曲《" + musicName + "》吗?");
                        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                clickListener.myOnClick(currentPosition);

                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.create().show();
                        dialog.dismiss();
                        break;
                    case 1:
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    class ViewHolder {
        TextView name;
        TextView author;
        TextView isPlayingImg;
        ImageView more;
    }


    /**
     * 用于回调的抽象类
     */
    public static abstract class MyClickListener {
        /**
         * 基类的onClick方法
         */
        public abstract void myOnClick(int position);
    }
}
