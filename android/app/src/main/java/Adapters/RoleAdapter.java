package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.szampchat.R;

import java.util.List;

import Config.env;
import Data.Models.Role;
import Data.Models.Token;
import Services.CommunityService;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.RoleViewHolder> {
    Activity activity;
    List<Role> roleList;

    private LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;

    public RoleAdapter(Activity activity) {
        this.activity = activity;
        this.layoutInflater = LayoutInflater.from(activity);
        roleList = null;
        try {
            onItemClickListener = (RoleAdapter.OnItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.getMessage() +
                    " must implements RoleAdapter.OnItemClickListener and OnItemLongClickListener interface"
            );
        }
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_list_users, parent, false);
        return new RoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoleViewHolder holder, int position) {
        Role role = roleList.get(position);
        holder.setRoleName(role.getName());
    }

    @Override
    public int getItemCount() {
        return (roleList != null) ? roleList.size() : 0;
    }


    public interface OnItemClickListener {
        void onItemClickListener(Role role);
        void onItemLongClickListener(Role role);
        void onSwipeDeleteRole(Role role);
    }

    public class RoleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView roleNameTextView;
        ImageView icon;
        Button delete;

        public RoleViewHolder(@NonNull View itemView) {
            super(itemView);
            roleNameTextView = itemView.findViewById(R.id.username);
            icon = itemView.findViewById(R.id.channelIcon);
            icon.setVisibility(View.GONE);


            itemView.setTag(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        public void setRoleName(String roleName){
            roleNameTextView.setText(roleName);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                Role role = roleList.get(position);
                onItemClickListener.onItemClickListener(role);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Role role = roleList.get(position);
                onItemClickListener.onItemLongClickListener(role);
            }
            return true;
        }
    }
    public ItemTouchHelper.SimpleCallback getItemTouchHelperCallback() {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(
                    @NonNull RecyclerView recyclerView,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Role role = roleList.get(position);
                    onItemClickListener.onSwipeDeleteRole(role);
                }
            }
        };
    }
}
