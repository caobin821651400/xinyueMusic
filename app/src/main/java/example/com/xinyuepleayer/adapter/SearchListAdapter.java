package example.com.xinyuepleayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.bean.SearchBean;

/**
 * Created by caobin on 2017/1/12.
 */
public class SearchListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<SearchBean.SongBean> mList;
    private Context mContext;

    public SearchListAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }


    public void setList(List<SearchBean.SongBean> list) {
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
            view = mInflater.inflate(R.layout.item_rank_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.tv_music_name);
            viewHolder.author = (TextView) view.findViewById(R.id.tv_music_author);
            viewHolder.more = (ImageView) view.findViewById(R.id.iv_more_menu);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        SearchBean.SongBean bean = (SearchBean.SongBean) getItem(position);
        viewHolder.name.setText(bean.getSongname());
        viewHolder.author.setText(bean.getArtistname());
        return view;
    }

    class ViewHolder {
        TextView name;
        TextView author;
        ImageView more;
    }
}
