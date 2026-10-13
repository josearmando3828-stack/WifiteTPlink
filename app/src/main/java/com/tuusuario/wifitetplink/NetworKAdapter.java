package com.tuusuario.wifitetplink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NetworkAdapter extends RecyclerView.Adapter<NetworkAdapter.NetworkViewHolder> {

    private final List<NetworkModel> networkList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NetworkModel network);
    }

    public NetworkAdapter(List<NetworkModel> networkList, OnItemClickListener listener) {
        this.networkList = networkList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NetworkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.network_item, parent, false);
        return new NetworkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NetworkViewHolder holder, int viewType) {
        NetworkModel network = networkList.get(viewType);
        holder.bind(network, listener);
    }

    @Override
    public int getItemCount() {
        return networkList.size();
    }

    static class NetworkViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtSsid, txtBssid, txtChannel, txtVulnerability;

        public NetworkViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSsid = itemView.findViewById(R.id.txtSsid);
            txtBssid = itemView.findViewById(R.id.txtBssid);
            txtChannel = itemView.findViewById(R.id.txtChannel);
            txtVulnerability = itemView.findViewById(R.id.txtVulnerability);
        }

        public void bind(final NetworkModel network, final OnItemClickListener listener) {
            txtSsid.setText(network.getSsid());
            txtBssid.setText("BSSID: " + network.getBssid());
            txtChannel.setText("CH: " + network.getChannel());
            txtVulnerability.setText("Vulnerable: " + network.getVulnerability());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(network);
                }
            });
        }
    }
}
