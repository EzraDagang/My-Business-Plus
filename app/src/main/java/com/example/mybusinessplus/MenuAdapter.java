package com.example.mybusinessplus;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private List<FoodItem> menuList;
    private OnMenuChangeListener listener;

    public MenuAdapter(List<FoodItem> menuList, OnMenuChangeListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        FoodItem item = menuList.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format("RM %.2f", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Handle Plus Click
        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            listener.onQuantityChanged(item.getPrice(), 1);
        });

        // Handle Minus Click
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 0) {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                listener.onQuantityChanged(-item.getPrice(), -1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, btnPlus, btnMinus;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}
