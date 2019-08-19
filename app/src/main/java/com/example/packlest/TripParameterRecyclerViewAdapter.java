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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class TripParameterRecyclerViewAdapter extends RecyclerView.Adapter<TripParameterRecyclerViewAdapter.TripParameterRecyclerViewViewHolder> {
    private final LayoutInflater layoutInflater;
    private final ArrayList<TripParameter> tripParameters;
    private final HashMap<UUID, Boolean> tripParametersSelectedForUse;

    TripParameterRecyclerViewAdapter(Context context, Set<UUID> tripParameterUuidsUsed) {
        this.layoutInflater = LayoutInflater.from(context);
        this.tripParameters = new ArrayList<>(PacklestApplication.getInstance().packlestData.tripParameters.values());

        tripParametersSelectedForUse = new HashMap<>();
        for (TripParameter tripParameter : tripParameters) {
            tripParametersSelectedForUse.put(tripParameter.uuid, false);
        }
        for (UUID tripParameterUuid : tripParameterUuidsUsed) {
            tripParametersSelectedForUse.put(tripParameterUuid, true);
        }
    }

    // Because we do not want to persist any changes until the "Save" button has been clicked,
    // we retain local state describing which trip parameters have been selected in the UI.
    // When "Save" is clicked, this method is used to then persist those selections.
    HashSet<UUID> getTripParametersSelectedForUse() {
        HashSet<UUID> tripParametersInUse = new HashSet<>();
        for (TripParameter tripParameter : tripParameters) {
            //noinspection ConstantConditions - By construction, all trip parameters are in the map.
            if (tripParametersSelectedForUse.get(tripParameter.uuid)) {
                tripParametersInUse.add(tripParameter.uuid);
            }
        }
        return tripParametersInUse;
    }

    @Override
    @NonNull
    public TripParameterRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recyclerview_trip_parameter, parent, false);
        return new TripParameterRecyclerViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripParameterRecyclerViewViewHolder tripParameterRecyclerViewViewHolder, int position) {
        TripParameter tripParameter = tripParameters.get(position);
        tripParameterRecyclerViewViewHolder.update(tripParameter.name, tripParametersSelectedForUse.get(tripParameter.uuid));
    }

    private Boolean toggleTripParameterUsage(int position) {
        UUID tripParameterUuid = tripParameters.get(position).uuid;
        Boolean tripParameterInUse = tripParametersSelectedForUse.get(tripParameterUuid);
        tripParametersSelectedForUse.put(tripParameterUuid, !Objects.requireNonNull(tripParameterInUse));
        return !tripParameterInUse;
    }

    @Override
    public int getItemCount() {
        return tripParameters.size();
    }

    public class TripParameterRecyclerViewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView textView;
        Boolean tripParameterSelectedForUse;

        TripParameterRecyclerViewViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.recycler_view_item_name);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            tripParameterSelectedForUse = toggleTripParameterUsage(getAdapterPosition());
            setBackground();
        }

        void setBackground() {
            if (tripParameterSelectedForUse) {
                textView.setBackground(textView.getResources().getDrawable(R.drawable.rounded_button_background_highlighted, null));
            } else {
                textView.setBackground(textView.getResources().getDrawable(R.drawable.rounded_button_background, null));
            }
        }

        void update(String name, Boolean tripParameterSelectedForUse) {
            textView.setText(name);
            this.tripParameterSelectedForUse = tripParameterSelectedForUse;
            setBackground();
        }
    }
}
