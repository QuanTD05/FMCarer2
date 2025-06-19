package com.example.fmcarer.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.fmcarer.Model.VideoItem;
import com.example.fmcarer.R;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<VideoItem> videoList;
    private Context context;

    public VideoAdapter(List<VideoItem> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem video = videoList.get(position);
        holder.videoTitle.setText(video.getTitle());

        // Configure WebView to load YouTube video
        holder.videoWebView.getSettings().setJavaScriptEnabled(true);
        holder.videoWebView.setWebViewClient(new WebViewClient());
        String html = "<html><body>" +
                "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/" + video.getVideoUrl() +
                "\" frameborder=\"0\" allowfullscreen></iframe>" +
                "</body></html>";
        holder.videoWebView.loadData(html, "text/html", "utf-8");
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView videoTitle;
        WebView videoWebView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoTitle = itemView.findViewById(R.id.videoTitle);
            videoWebView = itemView.findViewById(R.id.videoWebView);
        }
    }


}
