package com.rosa.swift.core.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rosa.motocross.R;
import com.rosa.swift.core.business.utils.DocumentsUtils;
import com.rosa.swift.core.network.json.sap.documents.JDriverDocumentType;
import com.rosa.swift.core.network.json.sap.documents.JDriverDocuments;

import java.io.File;
import java.util.List;

/**
 * Created by inurlikaev on 01.06.2016.
 */
public class DocumentsListAdapter extends ArrayAdapter<JDriverDocuments.JDriverDocument> {
    private OnItemClickListener onItemClickListener = null;

    private int resource;
    private Context context;
    private List<JDriverDocuments.JDriverDocument> values;

    public static class ViewHolder {
        TextView fileDescriptionTV;
        TextView fileNameTV;
        ImageView fileTypeIV;
        ImageView fileDownloadedIV;
        ProgressBar fileSpinner;
        JDriverDocuments.JDriverDocument driverDocument;
        int position;
    }

    public DocumentsListAdapter(Context context, int resource, List<JDriverDocuments.JDriverDocument> values) {
        super(context, resource, values);
        this.resource = resource;
        this.context = context;
        this.values = values;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View rowView = convertView;

        JDriverDocuments.JDriverDocument item = getItem(position);

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.dlv_documents_item_layout, parent, false);
            holder = new ViewHolder();
            holder.fileDescriptionTV = (TextView) rowView.findViewById(R.id.dlv_doc_item_description);
            holder.fileNameTV = (TextView) rowView.findViewById(R.id.dlv_doc_item_file_name);
            holder.fileTypeIV = (ImageView) rowView.findViewById(R.id.dlv_doc_item_type_icon);
            holder.fileSpinner = (ProgressBar) rowView.findViewById(R.id.dlv_doc_item_progress);
            holder.fileDownloadedIV = (ImageView) rowView.findViewById(R.id.dlv_doc_item_downloaded);
            rowView.setTag(holder);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        ViewHolder viewHolder = (ViewHolder) v.getTag();
                        onItemClickListener.onItemClick(v, viewHolder.driverDocument, viewHolder.position);
                    }
                }
            });
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        holder.position = position;
        holder.driverDocument = item;

        //заполняем данные
        String fileDescrition = "";
        String fileName = "";
        int fileTypeIconId = R.drawable.ic_file_type_unknown;
        boolean fileDownloaded = false;

        if (item != null) {
            fileDescrition = item.description;
            fileName = item.fileName;
            JDriverDocumentType documentType = JDriverDocumentType.getType(item.fileType);
            fileTypeIconId = JDriverDocumentType.getTypeIconId(documentType);

            File documentFile = DocumentsUtils.getSavedDocumentFile(item);
            fileDownloaded = documentFile != null;
        }

        holder.fileDescriptionTV.setText(fileDescrition);
        holder.fileNameTV.setText(fileName);
        holder.fileTypeIV.setImageResource(fileTypeIconId);
        holder.fileSpinner.setVisibility(View.GONE);
        if (fileDownloaded) holder.fileDownloadedIV.setVisibility(View.VISIBLE);
        else holder.fileDownloadedIV.setVisibility(View.GONE);
        return rowView;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, JDriverDocuments.JDriverDocument driverDocument, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getCount() {
        if (values == null)
            return 0;
        else return values.size();
    }
}
