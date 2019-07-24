package com.example.packlest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class TripParameterRecyclerViewAdapter extends RecyclerView.Adapter<TripParameterRecyclerViewAdapter.TripParameterRecyclerViewViewHolder> {
    private final LayoutInflater layoutInflater;
    private final ArrayList<TripParameter> tripParameters;
    private final HashMap<UUID, Boolean> tripParametersInUse;

    TripParameterRecyclerViewAdapter(Context context, ArrayList<UUID> tripParameterUuidsUsed) {
        this.layoutInflater = LayoutInflater.from(context);
        this.tripParameters = PacklestApplication.getInstance().packlestData.getTripParameters();
        tripParametersInUse = new HashMap<>();
        for (TripParameter tripParameter : tripParameters) {
            tripParametersInUse.put(tripParameter.uuid, false);
        }

        if (tripParameterUuidsUsed != null) {
            for (UUID tripParameterUuid : tripParameterUuidsUsed) {
                tripParametersInUse.put(tripParameterUuid, true);
            }
        }
    }

    ArrayList<UUID> getTripParametersInUse() {
        ArrayList<UUID> tripParametersInUseList = new ArrayList<>();
        for (TripParameter tripParameter : tripParameters) {
            if (tripParametersInUse.get(tripParameter.uuid)) {
                tripParametersInUseList.add(tripParameter.uuid);
            }
        }
        return tripParametersInUseList;
    }

    @Override
    @NonNull
    public TripParameterRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new TripParameterRecyclerViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripParameterRecyclerViewViewHolder holder, int position) {
        TripParameter tripParameter = tripParameters.get(position);
        holder.update(tripParameter.name, tripParametersInUse.get(tripParameter.uuid));
    }

    private Boolean toggleTripParameterUsage(int position) {
        UUID tripParameterUuid = tripParameters.get(position).uuid;
        Boolean inUse = tripParametersInUse.get(tripParameterUuid);
        tripParametersInUse.put(tripParameterUuid, !Objects.requireNonNull(inUse));
        return !inUse;
    }

    @Override
    public int getItemCount() {
        return tripParameters.size();
    }

    public class TripParameterRecyclerViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView textView;
        Boolean packingListUsesTripParameter;

        TripParameterRecyclerViewViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.recyclerViewItemName);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            packingListUsesTripParameter = toggleTripParameterUsage(getAdapterPosition());
            setBackground();
        }

        void update(String name, Boolean inUse) {
            textView.setText(name);
            packingListUsesTripParameter = inUse;
            setBackground();
        }

        void setBackground() {
            if (packingListUsesTripParameter) {
                textView.setBackground(textView.getResources().getDrawable(R.drawable.rounded_button_background_selected, null));
            } else {
                textView.setBackground(textView.getResources().getDrawable(R.drawable.rounded_button_background, null));
            }

        }
    }
}
