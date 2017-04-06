package com.rosa.swift.core.network.json.sap.documents;

import com.rosa.motocross.R;

/**
 * Created by inurlikaev on 30.05.2016.
 */
public enum JDriverDocumentType {
    PNG("PNG"),
    JPG("JPG"),
    JPEG("JPEG"),
    GIF("GIF"),
    BMP("BMP"),
    PDF("PDF"),
    DOC("DOC"),
    DOCX("DOCX"),
    RTF("RTF"),
    XLS("XLS"),
    XLSX("XLSX"),
    TXT("TXT"),
    ZIP("ZIP"),
    UNKNOWN("UNKNOWN");

    private String typeValue;

    private JDriverDocumentType(String type) {
        typeValue = type;
    }

    static public JDriverDocumentType getType(String pType) {
        for (JDriverDocumentType type : JDriverDocumentType.values()) {
            if (type.getTypeValue().equals(pType)) {
                return type;
            }
        }

        return UNKNOWN;
    }

    public String getTypeValue() {
        return typeValue;
    }

    static public int getTypeIconId(JDriverDocumentType type) {
        int typeIconId;
        switch (type) {
            case PNG:
                typeIconId = R.drawable.ic_file_type_png;
                break;
            case JPG:
            case JPEG:
                typeIconId = R.drawable.ic_file_type_jpg;
                break;
            case BMP:
                typeIconId = R.drawable.ic_file_type_bmp;
                break;
            case GIF:
                typeIconId = R.drawable.ic_file_type_gif;
                break;
            case PDF:
                typeIconId = R.drawable.ic_file_type_pdf;
                break;
            case DOC:
            case DOCX:
                typeIconId = R.drawable.ic_file_type_doc;
                break;
            case XLS:
            case XLSX:
                typeIconId = R.drawable.ic_file_type_xls;
                break;
            case RTF:
                typeIconId = R.drawable.ic_file_type_rtf;
                break;
            case TXT:
                typeIconId = R.drawable.ic_file_type_txt;
                break;
            case ZIP:
                typeIconId = R.drawable.ic_file_type_zip;
                break;
            default:
                typeIconId = R.drawable.ic_file_type_unknown;
        }

        return typeIconId;
    }
}
