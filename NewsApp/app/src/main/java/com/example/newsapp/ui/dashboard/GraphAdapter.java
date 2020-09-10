/*package com.example.newsapp.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.newsapp.R;

import java.util.List;

public class GraphAdapter extends ArrayAdapter<relationInfo> {
    private int resourceId;
    private Context myContext;
    private ViewHolder holder;

//        public MyAdapter(Context context, int resource, int textViewResourceId) {
//            super(context, resource, textViewResourceId);
//            resourceId = resource;
//        }

    public GraphAdapter(Context context, int textViewResourceId, List<relationInfo> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        myContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        relationInfo info = getItem(position);
        //Fruit fruit = datalist.get(position);//二者相同
        if (null == convertView){
            convertView = LayoutInflater.from(myContext).inflate(resourceId, parent, false);
                /*　
                参3为false,只让在父布局声明的Layout属性生效，并不会为这个View添加父布局。
                因为View一旦有了父布局之后，就不能添加到ListView中了


            holder = new ViewHolder();
            View view=R.layout.fragment_graph_list_item;
            holder.relation_label = convertView.findViewById(R.layout.fragment_graph_list_item.relation_label);
            holder.relation_name = convertView.findViewById(R.id.relation_name);
            holder.ward_icon=convertView.findViewById(R.id.ward_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.ward_icon.setImageResource(R.mipmap.backward);
        if (info.isForward()) holder.ward_icon.setImageResource(R.mipmap.forwand);
        holder.relation_label.setText(info.getRelation_label());
        holder.relation_name.setText(info.getRelation_name());


        return convertView;
    }
}
class ViewHolder{
    ImageView ward_icon;
    TextView relation_label,relation_name;
}
*/