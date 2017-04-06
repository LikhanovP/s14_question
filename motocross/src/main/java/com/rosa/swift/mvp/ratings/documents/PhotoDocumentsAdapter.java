package com.rosa.swift.mvp.ratings.documents;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.mvp.ratings.documents.repositories.PhotoDocumentTaskDto;

import java.io.File;
import java.util.List;

public class PhotoDocumentsAdapter extends ArrayAdapter<PhotoDocumentTaskDto> {

    private Context mContext;
    private List<PhotoDocumentTaskDto> mDocuments;

    PhotoDocumentsAdapter(Context context, int textViewResId, List<PhotoDocumentTaskDto> documents) {
        super(context, textViewResId, documents);
        mContext = context;
        mDocuments = documents;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.list_item_photo_document, parent, false);
        TextView catNameTextView = (TextView) row.findViewById(R.id.photo_item_name);
        ImageView iconImageView = (ImageView) row.findViewById(R.id.photo_item_image);

        PhotoDocumentTaskDto document = mDocuments.get(position);

        catNameTextView.setText(document.getDocumentType());
        iconImageView.setImageResource(R.drawable.ic_action_id_card_light);
        File documentFile = new File(document.getPhotoPath());
        if (documentFile.exists()) {
            iconImageView.setAlpha(128);
        } else {
            iconImageView.setAlpha(255);
        }
        return row;
    }
}
