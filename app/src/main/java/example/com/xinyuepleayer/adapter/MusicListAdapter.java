package example.com.xinyuepleayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.bean.MusicInfoBean;

/**
 * Created by caobin on 2017/1/12.
 */
public class MusicListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<MusicInfoBean> mList;

    public MusicListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View containView, ViewGroup parent) {
        View view = containView;
        ViewHolder viewHolder = null;
        if (containView == null) {
            view = mInflater.inflate(R.layout.item_music_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.tv_music_name);
            viewHolder.author = (TextView) view.findViewById(R.id.tv_music_author);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        MusicInfoBean bean = (MusicInfoBean) getItem(position);
        viewHolder.name.setText(bean.getTitle());
        viewHolder.author.setText(bean.getArtist());
        return view;
    }

    class ViewHolder {
        TextView name;
        TextView author;
    }
}
