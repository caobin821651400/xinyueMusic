package example.com.xinyuepleayer.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import example.com.xinyuepleayer.R;
import example.com.xinyuepleayer.bean.RankMusicBean;
import example.com.xinyuepleayer.utils.MyLogUtil;

/**
 * 排行榜适配器
 * Created by caobin on 2016/12/22.
 */
public class RankRecycleAdapter extends RecyclerView.Adapter<RankRecycleAdapter.MyRecycleHolder> {

    private onRecyclerViewItemClickListener itemClickListener = null;
    private Context mContext;

    private List<RankMusicBean.SongListBean> mList = new ArrayList<>();

    public RankRecycleAdapter(Context context) {
        this.mContext = context;

    }

    public void setList(List<RankMusicBean.SongListBean> rankList) {
        this.mList = rankList;
        notifyDataSetChanged();
    }

    /**
     * 在这里加载布局
     *
     * @param parent
     * @param viewType 分组类型   CommonItemViewHolder就是viewHolder的构造函数
     * @return return new CommonItemViewHolder(LayoutInflater.from(viewGroup.getContext()).
     * inflate(R.layout.header_common_item, viewGroup, false));
     */
    @Override
    public MyRecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_rank_list, parent, false);
        return new MyRecycleHolder(view);
    }

    /**
     * 得到不同的布局type，
     *
     * @param position
     * @return 返回 int 类型的
     */
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * 在这里绑定数据，也可以说输出数据，或者监听事件
     *
     * @param holder   你自己的holder
     * @param position 在列表中位置
     */
    @Override
    public void onBindViewHolder(MyRecycleHolder holder, final int position) {
        holder.tvName.setText(mList.get(position).getTitle());
        holder.tvAuthor.setText(mList.get(position).getAuthor());
        //加载图片
        Glide.with(mContext).load(mList.get(position).getPic_small()).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.no_music_rotate_img)
                .crossFade()
                .into(holder.musicImg);
        //点击弹出更多菜单
        holder.moreMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyLogUtil.d("点击了");
            }
        });
        holder.musicItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 itemClickListener.onItemClick(mList.get(position).getSong_id());
            }
        });
    }

    /**
     * 有多少个item
     *
     * @return 最好加个判断，mList是否为空
     */
    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * 自定义holder,在这里findViewById();也可以在这里setOnClickListener(this);需要实现OnClickListener接口
     */
    class MyRecycleHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAuthor;
        ImageView musicImg, moreMenu;
        RelativeLayout musicItem;

        public MyRecycleHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_music_name);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_music_author);
            musicImg = (ImageView) itemView.findViewById(R.id.iv_music_image);
            moreMenu = (ImageView) itemView.findViewById(R.id.iv_more_menu);
            musicItem = (RelativeLayout) itemView.findViewById(R.id.rl_rank_list_item);
        }
    }

    public void setOnItemClickListener(onRecyclerViewItemClickListener listener) {
        this.itemClickListener = listener;
    }

    /**
     * 定义一个接口用于点击回调
     */
    public interface onRecyclerViewItemClickListener {

        void onItemClick(String songId);
    }
}
