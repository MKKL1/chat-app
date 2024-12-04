package Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.szampchat.R;

import java.util.Collections;
import java.util.List;

import Data.Models.Community;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.CommunityViewHolder> {
    Activity activity;
    List<Community> communitiesList;

    private LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;

    public CommunityAdapter(Activity activity) {
        this.layoutInflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.communitiesList = null;
        try {
            onItemClickListener = (OnItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage() + " must implements CommunityAdapter.OnItemClickListener interface");
        }
    }

    public interface OnItemClickListener {
        void onItemClickListener(Community community);
    }

    @NonNull
    @Override
    public CommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_grid_community, parent, false);
        return new CommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityViewHolder holder, int position) {
        Community community = communitiesList.get(position);
        holder.setCommunityName(community.getCommunityName());
    }

    @Override
    public int getItemCount() {
        return (communitiesList != null) ? communitiesList.size() : 0;
    }
    public void setCommunitiesList(List<Community> communitiesList) {
        Collections.reverse(communitiesList);
        this.communitiesList = communitiesList;
        notifyDataSetChanged();
    }

    public class CommunityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textView;

        public CommunityViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.itemGridCommunityName);
            itemView.setTag(this);
            itemView.setOnClickListener(this);
        }
        public void setCommunityName(String communityName){
            textView.setText(communityName);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
//                Log.d("TESTOWANIE FUNKCJONALNOSCI", "test pozycji adaptera: " + String.valueOf(position));
                Community community = communitiesList.get(position);
                onItemClickListener.onItemClickListener(community);
            }
        }
    }
}
